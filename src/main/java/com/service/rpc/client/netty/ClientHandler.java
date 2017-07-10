package com.service.rpc.client.netty;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.service.rpc.client.RpcFuture;
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

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    } 

    /**
     * 发送数据请求
     * @param request
     * @return
     */
    public RpcFuture send(RpcRequest request) {
        final CountDownLatch latch = new CountDownLatch(1);
        RpcFuture rpcFuture = new RpcFuture(request);
        pendingRequest.put(request.getRequestId(), rpcFuture);
//        channel.writeAndFlush(request);
        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
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
