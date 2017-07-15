package com.service.rpc.client.netty;

import java.net.SocketAddress;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.service.rpc.client.RpcFuture;
import com.service.rpc.client.ServiceFactory;
import com.service.rpc.exception.ResponseTimeoutException;
import com.service.rpc.transport.RpcRequest;
import com.service.rpc.transport.RpcResponse;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static Logger log = Logger.getLogger(ClientHandler.class);
    
    public ConcurrentHashMap<String, RpcFuture> pendingRequest = new ConcurrentHashMap<String, RpcFuture>();// 所有未响应（未完成）的请求
    
    private volatile Channel channel;
    private SocketAddress remotePeer;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
        new Thread(){// 另起线程检测超时响应的请求
        	@Override
        	public void run() {
        		while(true) {
        			try{// 防止异常退出检测
        				for(RpcFuture rpcFuture : pendingRequest.values()) {
        					if((System.currentTimeMillis() - rpcFuture.getStartRequest().getTime()) > ServiceFactory.getReadTimeoutMills()) {
        						setResponse(new RpcResponse(rpcFuture.getRequest(), RpcResponse.CODE_CLIENT_EXCEPTION, new ResponseTimeoutException("获取数据超时")));
        					}
        				}
        			}catch(Exception e) {
        				log.warn("检测超时响应异常", e);
        			}
        			try {
        				Thread.sleep(500);
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        			}
        		}
        	}
        }.start();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
    	setResponse(response);
    }
    
    private void setResponse(RpcResponse response) {
    	String requestId = response.getRequestId();
    	RpcFuture rpcFuture = pendingRequest.get(requestId);
        if (rpcFuture != null) {
        	pendingRequest.remove(requestId);
            rpcFuture.setResponse(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client caught exception", cause);
        ctx.close();
    }
    
    /**
     * 此处需注意requestId不能重复
     * @return
     */
    private String getRequestId() {
		return UUID.randomUUID().toString();
	}

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    } 

    /**
     * 发送数据请求
     * @param request
     * @return
     */
    public RpcFuture send(RpcRequest request) {
    	request.setRequestId(getRequestId());
        final CountDownLatch latch = new CountDownLatch(1);
        RpcFuture rpcFuture = new RpcFuture(request);
        rpcFuture.setStartRequest(new Date());
        pendingRequest.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request)
	        .addListener(new ChannelFutureListener() {
	            @Override
	            public void operationComplete(ChannelFuture future) {
	            	if(!future.isSuccess()) {// 客户端异常后需要执行rpcFuture.setResponse(response)否则会一直等待响应
	            		Throwable error = null;
	            		if(future.cause() != null) {
	            			error = future.cause();
	            			log.warn(error.getMessage(), future.cause());
	            		} else {
	            			error = new RuntimeException("发送请求异常");
	            			log.warn(error.getMessage());
	            		}
	            		setResponse(new RpcResponse(request, RpcResponse.CODE_CLIENT_EXCEPTION, error));
	            	}
	                latch.countDown();
	            }
	        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        return rpcFuture;
    }
    
    
}
