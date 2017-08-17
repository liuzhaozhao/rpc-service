package com.service.rpc.transport;

import java.io.Serializable;

import com.service.rpc.client.ServiceFactory;

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
	private ClientInfo clientInfo = new ClientInfo(ServiceFactory.getClientVersion());// 客户端信息，用于服务器标识客户端
	public RpcRequest() {}// json序列化用到
	
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
	
	public ClientInfo getClientInfo() {
		return clientInfo;
	}
	
	public void setClientInfo(ClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}
	
}
