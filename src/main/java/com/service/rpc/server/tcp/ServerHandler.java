package com.service.rpc.server.tcp;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

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
    	MethodInfo methodInfo = RpcServer.getMethodInfo(request.getMethodIdentify());
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
    	ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("Send response for request " + request.getRequestId());
            }
        });
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server caught exception", cause);
        ctx.close();
    }
}
