package com.service.rpc.server.http.nettyChannel;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.service.rpc.common.Utils;
import com.service.rpc.server.http.HttpMethod;
import com.service.rpc.server.http.HttpType;
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
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;

public class HttpRequestHandler extends ChannelInboundHandlerAdapter {
	private Logger log = Logger.getLogger(this.getClass());
	
	private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");
    private static final byte[] NOT_FOUND = new byte[]{'N','o','t',' ','F','o','u','n','d'};
//    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
    
    private ChannelHandlerContext ctx;
    protected HttpHeaders headers;
    protected HttpRequest request;
//    private HttpPostRequestDecoder decoder;
    protected Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
    protected Map<String, List<String>> postParams = new HashMap<String, List<String>>();
    protected Map<String, String> pathParams;
    protected String postBody;
    
//    protected FullHttpResponse response;
//    protected FullHttpRequest fullRequest;
    protected String url;
    protected HttpType httpType;
    private PostRequestContentType contentType;
    protected MethodInfo methodInfo;
    
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		try{
			if(!(msg instanceof HttpRequest)) {// 丢弃请求
				return;
			}
			this.ctx = ctx;
	        this.request = (HttpRequest) msg;
	        this.headers = request.headers();
	        this.url = Utils.getUrl(request.uri());
	        System.err.println(this.url);
	        this.httpType = HttpType.get(request.method().name());
	        if(this.httpType == null) {
	        	render(HttpResponseStatus.NOT_FOUND, NOT_FOUND);
	    		return;
	        }
	        setContentType();
	        if(this.httpType == HttpType.POST && this.contentType == null) {
	        	render(HttpResponseStatus.NOT_FOUND, NOT_FOUND);
	    		return;
	        }
	        methodInfo = HttpMethod.getMethodInfo(url, httpType.getType());
        	if(methodInfo == null) {
        		render(HttpResponseStatus.NOT_FOUND, NOT_FOUND);
        		return;
        	}
        	initParams();
        	Object returnData = methodInfo.invoke(getMethodData());
        	render(HttpResponseStatus.OK, methodInfo.getReturnType().getReturnData(returnData));
        } catch(Exception e){
        	String errorMsg = "请求异常";
        	if(e.getMessage() != null) {
        		errorMsg = e.getMessage();
        	}
        	render(HttpResponseStatus.NOT_FOUND, errorMsg.getBytes());
        	log.warn("执行http调用异常", e);
        }finally {
        	ReferenceCountUtil.release(msg);
		}
    }
	
	private void setContentType() {
		String typeStr = headers.get("Content-Type");
		if(StringUtils.isBlank(typeStr)){
			return;
		}
		String[] list = typeStr.split(";");
		contentType = PostRequestContentType.get(list[0]);
	}
	
	/**
	 * 根据请求，初始化四类参数：路径参数、get请求参数、post form表单请求参数、post body体请求参数
	 * @throws IOException 
	 */
	private void initParams() throws IOException {
		initQueryParams();
		pathParams = methodInfo.getPathParams(url);// 初始化路径请求参数
		initPostParams();
		initPostBody();
	}
	
	/**
	 * 初始化url上的请求参数
	 */
	private void initQueryParams() {
		QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri(), StandardCharsets.UTF_8);
		Map<String, List<String>> params = queryDecoder.parameters();
		if(params != null) {
			queryParams = params;
		}
	}
	
	/**
	 * 初始化post form请求参数
	 * @throws IOException 
	 */
	private void initPostParams() throws IOException {
		if(this.httpType != HttpType.POST || this.contentType != PostRequestContentType.FORM) {
			return;
		}
		// 如果request为FullHttpRequest则可使用下面的方式
		if(request instanceof FullHttpRequest) {
			String kvStr = ((FullHttpRequest)request).content().toString(StandardCharsets.UTF_8);
			QueryStringDecoder queryDecoder = new QueryStringDecoder(kvStr, false);
			Map<String, List<String>> params = queryDecoder.parameters();
			if(params != null) {
				postParams = params;
			}
//		} else {// 加了HttpObjectAggregator后不需要下面解析数据的代码了
//			if (decoder != null) {  
//	            decoder.cleanFiles();  
//	            decoder = null;  
//	        }
//			decoder = new HttpPostRequestDecoder(factory, request, StandardCharsets.UTF_8);
////			if (!(request instanceof HttpContent)) {
////				return;
////			}
////			// New chunk is received
////			HttpContent chunk = (HttpContent) request;
////			decoder.offer(chunk);
////			while (decoder.hasNext()) {
////                InterfaceHttpData data = decoder.next();
////                if (data == null) {
////                	continue;
////                }
////                try {
////                	Attribute attribute = (Attribute) data;
////            		String name = attribute.getName();
////            		String val = attribute.getValue();
////            		if(postParams.containsKey(name)) {
////            			postParams.get(name).add(val);
////            		} else {
////            			List<String> vals = new ArrayList<String>();
////            			vals.add(val);
////            			postParams.put(name, vals);
////            		}
////                } finally {
////                    data.release();
////                }
////            }
//			
//			List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
//            for (InterfaceHttpData data : datas) {
//            	if(data.getHttpDataType() == HttpDataType.Attribute) {
//            		Attribute attribute = (Attribute) data;
//            		String name = attribute.getName();
//            		String val = attribute.getValue();
//            		if(postParams.containsKey(name)) {
//            			postParams.get(name).add(val);
//            		} else {
//            			List<String> vals = new ArrayList<String>();
//            			vals.add(val);
//            			postParams.put(name, vals);
//            		}
//            	}
//            }
		}
	}
	
	/**
	 * 初始化post请求数据（非form表单）
	 */
	private void initPostBody() {
		if(this.httpType != HttpType.POST || this.contentType != PostRequestContentType.JSON || !(request instanceof FullHttpRequest)) {
			return;
		}
		postBody = ((FullHttpRequest)request).content().toString(StandardCharsets.UTF_8);
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
				List<String> paramList = postParams.get(methodParam.getName());
				if(paramList != null) {
					paramData = paramList.get(0);
				}
			} else if (methodParam.getParamType() == ParamType.PATH_PARAM) {
				paramData = pathParams.get(methodParam.getName());
			} else if (methodParam.getParamType() == ParamType.QUERY_PARAM) {
				List<String> paramList = queryParams.get(methodParam.getName());
				if(paramList != null) {
					paramData = paramList.get(0);
				}
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
        response.headers().set(CONTENT_TYPE, (methodInfo==null||status != HttpResponseStatus.OK)?"text/plain":methodInfo.getReturnType().getType());
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
	
//	@Override
//	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		if (decoder != null) {
//            decoder.cleanFiles();
//        }
//	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
	
}
