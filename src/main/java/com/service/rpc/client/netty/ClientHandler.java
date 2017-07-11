package com.service.rpc.client.netty;

import java.net.SocketAddress;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.service.rpc.client.RpcFuture;
import com.service.rpc.client.ServiceFactory;
import com.service.rpc.transport.RpcRequest;
import com.service.rpc.transport.RpcResponse;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by luxiaoxun on 2016-03-14.
 */
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static Logger log = Logger.getLogger(ClientHandler.class);
    
    private ConcurrentHashMap<String, RpcFuture> pendingRequest = new ConcurrentHashMap<String, RpcFuture>();// 所有未响应（未完成）的请求

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
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        String requestId = response.getRequestId();
//        System.err.println(requestId+"	响应数据已收到:"+new FastJson().toStr(response));
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
        RpcFuture rpcFuture = new RpcFuture(ServiceFactory.factory.getReadTimeoutMills(), request);
        pendingRequest.put(request.getRequestId(), rpcFuture);
//        System.err.println(request.getRequestId()+"	请求准备发送");
        channel.writeAndFlush(request)
	        .addListener(new ChannelFutureListener() {
	            @Override
	            public void operationComplete(ChannelFuture future) {
//	            	System.err.println(request.getRequestId()+"	请求已发送出");
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
