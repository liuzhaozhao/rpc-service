package com.service.rpc.server.http;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

/**
 * 定义支持的请求类型
 * @author liuzhao
 *
 */
public enum HttpType {
	POST(POST.class, "post"),
	GET(GET.class, "get");
	
	private Class<?> cls;
	private String type;
	
	private HttpType(Class<?> cls, String type) {
		this.cls = cls;
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public Class<?> getCls() {
		return cls;
	}
	
	/**
	 * 获取所有http类型
	 * 
	 * 启动时调用
	 * @return
	 */
	public static List<String> getTypes() {
		HttpType[] httpTypes = values();
		List<String> types = new ArrayList<String>();
		for(HttpType httpType : httpTypes) {
			types.add(httpType.getType());
		}
		return types;
	}
	
	/**
	 * 根据注解类型获取HttpType
	 * @param cls
	 * @return
	 */
	public static HttpType get(Class<?> cls) {
		for(HttpType httpType : HttpType.values()) {
			if(httpType.getCls() == cls) {
				return httpType;
			}
		}
		return null;
	}
	
	/**
	 * 根据http请求类型获取对应的HttpType
	 * @param type
	 * @return
	 */
//	public static HttpType get(String type) {
//		if(StringUtils.isBlank(type)) {
//			return null;
//		}
//		HttpType httpType = null;
//		try{
//			httpType = HttpType.valueOf(type.toLowerCase());
//		} catch(Exception e) {
//			
//		}
//		return httpType;
//	}
}
