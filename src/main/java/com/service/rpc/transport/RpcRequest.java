package com.service.rpc.transport;

import java.io.Serializable;

/**
 * get/set是用于json序列化的
 * Serializable是用于字节流序列化
 * @author liuzhao
 *
 */
public class RpcRequest implements Serializable {
	private static final long serialVersionUID = -2562138100343463822L;
	
//	private boolean isJson;// 请求数据是否是json传输，json数据特殊处理
	private String methodIdentify;// 要调用的服务器的方法标识
	private String requestId;// 请求ID，在所有未完成的请求中唯一，用于标识请求的响应
	private Object[] args;// 请求参数 
	public RpcRequest() {}
	
	public RpcRequest(String methodIdentify, Object[] args) {
//		this.isJson = isJson;
		this.methodIdentify = methodIdentify;
		this.args = args;
	}
	
//	public boolean isJson() {
//		return isJson;
//	}
//	
//	public void setJson(boolean isJson) {
//		this.isJson = isJson;
//	}

	public String getMethodIdentify() {
		return methodIdentify;
	}

	public void setMethodIdentify(String methodIdentify) {
		this.methodIdentify = methodIdentify;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
}
