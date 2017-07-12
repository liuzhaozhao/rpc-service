package com.service.rpc.server.rpc.netty;

import org.apache.log4j.Logger;

import com.service.rpc.common.JsonUtil;
import com.service.rpc.server.common.MethodInfo;
import com.service.rpc.server.rpc.RpcServer;
import com.service.rpc.transport.RpcRequest;
import com.service.rpc.transport.RpcResponse;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger log = Logger.getLogger(ServerHandler.class);

    @Override
    public void channelRead0(final ChannelHandlerContext ctx,final RpcRequest request) {
    	long startTime = System.currentTimeMillis();
    	MethodInfo methodInfo = RpcServer.getMethodInfo(request.getMethodIdentify());
    	RpcResponse response = null;
    	if(methodInfo == null) {
    		response = new RpcResponse(request, RpcResponse.CODE_NO_METHOD, "不存在该服务");
    	} else {
    		try {
				response = new RpcResponse(request, RpcResponse.CODE_SUCCESS, methodInfo.invoke(request.getArgs()));
			} catch (Exception e) {
				String errorMsg = e.getCause() == null ?e.getMessage() : e.getCause().getMessage();
				response = new RpcResponse(request, RpcResponse.CODE_SERVER_EXCEPTION, errorMsg);
				log.warn("获取方法调用数据异常", e);
			}
    	}
    	if(RpcServer.isEnableLog()) {
    		log.info("接收请求："+methodInfo.getMethodStr()+"，耗时："+(System.currentTimeMillis() - startTime)+"毫秒，请求值："+JsonUtil.toJson(request)+"，返回值："+JsonUtil.toJson(response));
    	}
    	ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
            	if(!channelFuture.isSuccess()) {// 如序列化异常
            		String errorMsg = "";
            		if(channelFuture.cause() != null) {
            			errorMsg = channelFuture.cause().getMessage();
            			log.warn(errorMsg, channelFuture.cause());
            		} else {
            			errorMsg = "响应数据异常";
            			log.warn(errorMsg);
            		}
            	}
            }
        });
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {// 此处异常后，客户端的当次请求只能等待超时返回了
//        log.warn("server caught exception", cause);
    	log.warn("server caught exception："+cause.getMessage());
        ctx.close();
    }
}
