package com.service.rpc.server.tcp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * 方法参数信息
 * @author liuzhao
 *
 */
public class MethodParam {
	private Type type;// 方法参数Type类型（包含泛型信息） 
	private Class<?> cls;// 方法参数Class类型
	private Annotation[] annotations;// 方法参数注解
	
	public MethodParam(Type type, Class<?> cls, Annotation[] annotations) {
		this.type = type;
		this.cls = cls;
		this.annotations = annotations;
	}
	
	public Type getType() {
		return type;
	}
	
	public Class<?> getCls() {
		return cls;
	}
	
	public Annotation[] getAnnotations() {
		return annotations;
	}
	
}
