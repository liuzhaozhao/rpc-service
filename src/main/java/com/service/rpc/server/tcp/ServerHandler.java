package com.service.rpc.server.tcp;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import com.service.rpc.common.JsonUtil;
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
    	MethodInfo methodInfo = RpcServer.server.getMethodInfo(request.getMethodIdentify());
    	RpcResponse response = null;
    	if(methodInfo == null) {
    		response = new RpcResponse(request, RpcResponse.CODE_NO_METHOD, null);
    	} else {
    		try {
				response = new RpcResponse(request, RpcResponse.CODE_SUCCESS, methodInfo.invoke(request.getArgs()));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				response = new RpcResponse(request, RpcResponse.CODE_SERVER_EXCEPTION, null);
				response.setErrorMsg(e.getMessage());
				log.warn("获取方法调用数据异常", e);
			}
    	}
    	if(RpcServer.server.isEnableLog()) {
    		log.info("接收请求："+methodInfo.getMethodStr()+"，耗时："+(System.currentTimeMillis() - startTime)+"毫秒，返回值："+JsonUtil.toJson(response));
    	}
    	ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("Send response for request " + request.getRequestId());
            }
        });
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("server caught exception("+cause.getMessage()+")");
        ctx.close();
    }
}
