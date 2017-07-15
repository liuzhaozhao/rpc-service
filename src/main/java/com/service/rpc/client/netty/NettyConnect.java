package com.service.rpc.client.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.service.rpc.client.ConnectManage;
import com.service.rpc.client.RpcFuture;
import com.service.rpc.transport.RpcRequest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyConnect implements ConnectManage {
	private Logger log = Logger.getLogger(this.getClass());
	private volatile static NettyConnect connect;
	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
	private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
	private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    protected long connectTimeoutMillis = 6000;
    private AtomicInteger roundRobin = new AtomicInteger(0);
	
    private CopyOnWriteArrayList<ClientHandler> connectedHandlers = new CopyOnWriteArrayList<ClientHandler>();
//    private Map<InetSocketAddress, ClientHandler> connectedServerNodes = new ConcurrentHashMap<>();
    private ThreadLocal<List<ClientHandler>> threadUsedConnect = new ThreadLocal<List<ClientHandler>>();// 存储当前线程使用过的ClientHandler
    
    private NettyConnect() {}
    
	/**
	 * 获取连接实例
	 * @return
	 */
	public static NettyConnect getInstance() {
        if (connect != null) {
            return connect;
        }
        synchronized (NettyConnect.class) {
            if (connect == null) {
            	connect = new NettyConnect();
            }
        }
        return connect;
    }

	@Override
	public void updateConnect(List<String> ipPortList) {
		if(ipPortList == null || ipPortList.size() == 0) {
			remove(null);
			return;
		}
		HashSet<InetSocketAddress> newAllServerNodeSet = new HashSet<InetSocketAddress>();
		for(String ipPort : ipPortList) {
			String[] array = ipPort.split(":");
            if (array.length != 2) { // Should check IP and port
            	continue;
            }
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            newAllServerNodeSet.add(new InetSocketAddress(host, port));
		}
		// 删除不存在的连接
		for(int i = connectedHandlers.size() -1; i >= 0; i--) {
			ClientHandler clientHandler = connectedHandlers.get(i);
			SocketAddress remotePeer = clientHandler.getRemotePeer();
            if (!newAllServerNodeSet.contains(remotePeer)) {
            	clientHandler.close();
                connectedHandlers.remove(clientHandler);
            }
		}
		// 添加新连接
		for (final InetSocketAddress socketAddress : newAllServerNodeSet) {
			boolean exist = false;
			for(int i=0; i<connectedHandlers.size(); i++) {
				if(socketAddress.equals(connectedHandlers.get(i).getRemotePeer())) {
					exist = true;
					break;
				}
			}
			if(!exist) {
				connectServerNode(socketAddress);
			}
        }
	}
	
	@Override
	public void connect(InetSocketAddress remotePeer) {
		remove(remotePeer);// 已存在，则删除
//		int processors = Runtime.getRuntime().availableProcessors();// 一个地址初始化CPU个数个连接
//		for(int i=0; i<processors; i++) {
			connectServerNode(remotePeer);
//		}
	}

	/**
	 * 
	 * @param remotePeer	为null则关闭所有连接
	 */
	@Override
	public void remove(InetSocketAddress remotePeer) {
		for (int i = 0; i < connectedHandlers.size(); ++i) {
			ClientHandler clientHandler = connectedHandlers.get(i);
			if(remotePeer != null && !clientHandler.getRemotePeer().equals(remotePeer)) {
				continue;
			}
            clientHandler.close();
            connectedHandlers.remove(clientHandler);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RpcFuture send(RpcRequest request) {
		CopyOnWriteArrayList<ClientHandler> handlers = (CopyOnWriteArrayList<ClientHandler>) this.connectedHandlers.clone();
        int size = handlers.size();
        while (size <= 0) {// TODO 此处需测试超时未获取连接时抛异常
            try {
                boolean available = waitConnect();
                if (available) {
                    handlers = (CopyOnWriteArrayList<ClientHandler>) this.connectedHandlers.clone();
                    size = handlers.size();
                }
            } catch (InterruptedException e) {
                log.error("Waiting for available node is interrupted! ", e);
                throw new RuntimeException("Can't connect any servers!", e);
            }
        }
        List<ClientHandler> usedConnect = threadUsedConnect.get();
        if(usedConnect == null) {// 当前线程没使用过连接，则直接返回最近未使用过的连接
        	ClientHandler handler = handlers.get(roundRobin.getAndAdd(1) % size);//  + size
        	usedConnect = new ArrayList<ClientHandler>();
        	usedConnect.add(handler);
        	threadUsedConnect.set(usedConnect);
        	System.err.println("当前线程无使用过的connnect，使用连接："+handler.getRemotePeer().toString());
        	return handler.send(request);
        }
        ClientHandler useHandler = null;
        
        // 从当前所有线程使用的位置开始搜索当前线程未使用的连接（此处应该从当前线程第一个使用的位置处查找，但是即便是这样也可能出现在当前线程下一次获取连接时，总连接数有变动，
    	// 位置依然对不上当前线程上一次连接的下一个），为什么不从handlers的第一个查找，因为这样会导致前面位置的连接使用的次数多于后面的
        int startIndex = roundRobin.get();
        for(int i=0; i<handlers.size(); i++) {// 同一个线程，使用不同连接，可以在异常重试时，避免重复使用一个有问题的连接
        	ClientHandler handler = handlers.get(startIndex % handlers.size());
        	if(!usedConnect.contains(handler)) {
        		usedConnect.add(handler);
        		useHandler = handler;
        		break;
        	}
        	startIndex++;
        }
        if(useHandler == null) {// 没有找到未使用过的Handler，则使用当前线程最早使用过的Handler
        	System.err.println("所有的connect当前线程都是用过");
        	for(int i=0; i<usedConnect.size(); i++) {
        		ClientHandler handler = usedConnect.get(i);
        		if(handlers.contains(handler)) {
        			useHandler = handler;
        			usedConnect.remove(handler);
        			usedConnect.add(handler);// 将刚使用的连接放最后
        		}
        	}
        }
        // 此处useHandler不会为null
        System.err.println("使用连接："+useHandler.getRemotePeer().toString());
        return useHandler.send(request);
	}

	@Override
	public void stop() {
		remove(null);
		weakupWaitConnect();
		threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
	}

	private void connectServerNode(final InetSocketAddress remotePeer) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ClientInitializer());

                ChannelFuture channelFuture = b.connect(remotePeer);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                        	ClientHandler handler = channelFuture.channel().pipeline().get(ClientHandler.class);
                            connectedHandlers.add(handler);
                            weakupWaitConnect();
                        }
                    }
                });
            }
        });
    }
	
	private void weakupWaitConnect() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitConnect() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(this.connectTimeoutMillis, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }
}
