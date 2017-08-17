package com.service.rpc.server.http;

import java.util.List;
import java.util.Map;

import com.service.rpc.server.http.method.HttpMethodInfo;
import com.service.rpc.server.http.method.HttpType;

public interface IAuth {
	
	/**
	 * 
	 * @param methodInfo	当前请求的方法信息
	 * @param url	当前请求的url
	 * @param httpType	请求类型
	 * @param queryParams	url后的请求参数
	 * @param postParams	post请求参数（key-val）
	 * @param pathParams	路径中的参数
	 * @param postBody	请求体中的参数
	 * @return
	 */
	public boolean auth(HttpMethodInfo methodInfo, String url, HttpType httpType, 
			Map<String, List<String>> queryParams, Map<String, List<String>> postParams, 
			Map<String, String> pathParams, String postBody);
}
