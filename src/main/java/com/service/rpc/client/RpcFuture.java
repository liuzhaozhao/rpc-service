package com.service.rpc.client;

import com.service.rpc.transport.RpcRequest;
import com.service.rpc.transport.RpcResponse;

public class RpcFuture {
	private RpcRequest request;
	private RpcResponse response;
	
	public RpcFuture(RpcRequest request) {
		this.request = request;
	}
	
	public RpcRequest getRequest() {
		return request;
	}
	
	public void setResponse(RpcResponse response) {
		this.response = response;
	}
	
	public Object getData() {
		if(response == null || response.getResponseCode() != RpcResponse.CODE_SUCCESS) {
			return null;
		}
		return response.getData();
	}
}
