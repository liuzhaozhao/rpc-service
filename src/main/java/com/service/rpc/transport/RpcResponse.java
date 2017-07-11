package com.service.rpc.transport;

import java.io.Serializable;

/**
 * rpc请求响应数据
 * 
 * get、set、无参构造方法是用于json序列化的
 * Serializable是用于字节流序列化
 * 
 * @author liuzhao
 *
 */
public class RpcResponse implements Serializable {
	private static final long serialVersionUID = -2562138100343463822L;
//	public static final int CODE_REQUEST = -1;// 请求中
	public static final int CODE_SUCCESS = 0;// 正常返回值
	public static final int CODE_SERVER_EXCEPTION = -10;// 返回值异常
	public static final int CODE_CLIENT_EXCEPTION = -20;// 客户端异常
	public static final int CODE_TRANSPORT_EXCEPTION = -30;// 网络传输异常
	public static final int CODE_NO_METHOD = -40;// 服务器没有对应的方法

	private int responseCode;// 响应码
	private String requestId;// 请求标识（客户端识别 ）
	private String methodIdentify;// 方法唯一标识字符串
//	private boolean isJson;// 响应数据是否是json传输，json数据特殊处理
	private Object data;// 服务器响应数据
	private Throwable error;// 异常
	private String errorMsg;// 异常消息
	
	public RpcResponse() {}
	
//	public RpcResponse(int responseCode, String requestId) {
//		this.responseCode = responseCode;
//		this.requestId = requestId;
//	}
	
	public RpcResponse(RpcRequest request, int responseCode, Object data) {
		this(request, responseCode, data, null, null);
	}
	
	/**
	 * 客户端异常使用
	 * @param request
	 * @param responseCode
	 * @param data
	 * @param error
	 */
	public RpcResponse(RpcRequest request, int responseCode, Throwable error) {
		this(request, responseCode, null, error, error == null ? null : error.getMessage());
	}
	/**
	 * 服务器端使用
	 * 服务器端不直接使用Throwable的原因有两个：
	 * 	1.使用fastJson时，无法反序列化Throwable对象；
	 * 	2.Throwable序列化后数据量很大，不便于传输
	 * @param request
	 * @param responseCode
	 * @param data
	 * @param errorMsg
	 */
	public RpcResponse(RpcRequest request, int responseCode, String errorMsg) {
		this(request, responseCode, null, null, errorMsg);
	}
	public RpcResponse(RpcRequest request, int responseCode, Object data, Throwable error, String errorMsg) {
		if(request == null) {
			throw new IllegalArgumentException("参数不能为null");
		}
		this.responseCode = responseCode;
		this.requestId = request.getRequestId();
		this.methodIdentify = request.getMethodIdentify();
//		this.isJson = isJson;
		this.data = data;
		this.error = error;
		this.errorMsg = errorMsg;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

//	public boolean isJson() {
//		return isJson;
//	}
//
//	public void setJson(boolean isJson) {
//		this.isJson = isJson;
//	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public String getMethodIdentify() {
		return methodIdentify;
	}
	
	public void setMethodIdentify(String methodIdentify) {
		this.methodIdentify = methodIdentify;
	}

	public Throwable getError() {
		return error;
	}
	
	public void setError(Throwable error) {
		this.error = error;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
