package com.service.rpc.client.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
	
    private CopyOnWriteArrayList<ClientHandler> connectedHandlers = new CopyOnWriteArrayList<>();
//    private Map<InetSocketAddress, ClientHandler> connectedServerNodes = new ConcurrentHashMap<>();
    
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
		connectServerNode(remotePeer);
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
        while (size <= 0) {
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
        int index = (roundRobin.getAndAdd(1) + size) % size;
		return handlers.get(index).send(request);
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
