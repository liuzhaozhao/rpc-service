package com.service.rpc.server.http;

import java.lang.annotation.Annotation;

import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public enum ParamType {
	BEAN_PARAM(BeanParam.class),// 针对post方式的非k-v格式的数据
	FORM_PARAM(FormParam.class),// 针对Post方式的k-v请求参数
	PATH_PARAM(PathParam.class),// 定义在url上的参数
	QUERY_PARAM(QueryParam.class);// get请求参数
	
	private Class<?> cls;
	
	private ParamType(Class<?> cls) {
		this.cls = cls;
	}
	
	public Class<?> getCls() {
		return cls;
	}
	
	/**
	 * 根据注解获取对应的注解值
	 * @param annotation
	 * @return
	 */
	public static String getVal(Annotation annotation) {
		if (annotation instanceof FormParam) {
			return ((FormParam)annotation).value();
		} else if (annotation instanceof PathParam) {
			return ((PathParam)annotation).value();
		} else if (annotation instanceof QueryParam) {
			return ((QueryParam)annotation).value();
		}
		return null;
	}
	
	/**
	 * 根据class获取paramType
	 * @param cls
	 * @return
	 */
	public static ParamType get(Class<?> cls) {
		for(ParamType paramType : ParamType.values()) {
			if(paramType.getCls() == cls) {
				return paramType;
			}
		}
		return null;
	}
}
