package com.service.rpc.server.http.nettyChannel;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;
import java.util.Map;

import com.service.rpc.server.http.HttpMethod;
import com.service.rpc.server.http.MethodInfo;
import com.service.rpc.server.http.MethodParam;
import com.service.rpc.server.http.ParamType;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;

public class HttpRequestHandler extends ChannelInboundHandlerAdapter {
	private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");
    private static final byte[] NOT_FOUND = new byte[]{'N','o','t',' ','F','o','u','n','d'};
    
    private ChannelHandlerContext ctx;
    protected HttpHeaders headers;
    protected HttpRequest request;
    protected Map<String, List<String>> queryParams;
    protected Map<String, List<String>> postParams;
    protected String postBody;
    protected Map<String, String> pathParams;
    
    protected FullHttpResponse response;
    protected FullHttpRequest fullRequest;
    protected String uri;
    protected String httpType;
    protected MethodInfo methodInfo;
    
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if(!(msg instanceof HttpRequest)) {// 丢弃请求
			ReferenceCountUtil.release(msg);
			return;
		}
		this.ctx = ctx;
        this.request = (HttpRequest) msg;
        this.headers = request.headers();
        this.uri = request.uri();
        this.httpType = request.method().name().toLowerCase();
        methodInfo = HttpMethod.getMethodInfo(uri, httpType);
        try{
        	if(methodInfo == null) {
        		render(HttpResponseStatus.NOT_FOUND, NOT_FOUND);
        		return;
        	}
        	initParams();
        	
        	render(HttpResponseStatus.OK, methodInfo.getReturnType().getReturnData(getMethodData()));
        }finally {
//        	ReferenceCountUtil.release(msg);
		}
    }
	
	/**
	 * 初始化四类参数：路径参数、get请求参数、post form表单请求参数、post body体请求参数
	 */
	private void initParams() {
		
	}
	
	/**
	 * 生成方法请求参数
	 * @return
	 */
	private Object[] getMethodData() {
		Object[] params = new Object[methodInfo.getMethodParams().length];
		for(int i=0; i<methodInfo.getMethodParams().length; i++) {
			MethodParam methodParam = methodInfo.getMethodParams()[i];
			String paramData = null;
			if(methodParam.getParamType() == ParamType.BEAN_PARAM) {
				paramData = postBody;
			} else if (methodParam.getParamType() == ParamType.FORM_PARAM) {
				paramData = postParams.get(methodParam.getName()).get(0);
			} else if (methodParam.getParamType() == ParamType.PATH_PARAM) {
				paramData = pathParams.get(methodParam.getName());
			} else if (methodParam.getParamType() == ParamType.QUERY_PARAM) {
				paramData = queryParams.get(methodParam.getName()).get(0);
			}
			params[i] = methodParam.getParamObj(paramData);
		}
		return params;
	}
	
	/**
	 * 响应http请求
	 * @param ctx
	 * @param msg
	 */
	protected void render(HttpResponseStatus status, byte[] msg) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.wrappedBuffer(msg));
        response.headers().set(CONTENT_TYPE, methodInfo==null?"text/plain":methodInfo.getReturnType().getType());
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(response);
        }
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
	
}
