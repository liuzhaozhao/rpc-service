package com.service.rpc.server.tcp;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.service.rpc.common.Utils;
import com.service.rpc.server.invoke.BaseInvoke;
import com.service.rpc.server.invoke.JavaInvoke;

public class MethodInfo {
	private Method method;
	private String methodStr;
	private BaseInvoke invoke;// 反射调用类
//	private ISerialize serialize;// 序列化类
	private List<MethodParam> methodParams = new ArrayList<MethodParam>();// 方法对应的请求参数数组
	
	public MethodInfo(Method method, Object clsInstance) {
		this.method = method;
		this.methodStr = Utils.getMethodIdentify(method);
		this.invoke = new JavaInvoke(method, clsInstance);// 默认使用java反射类
//		this.serialize = new FstSerialize();// 默认使用FstSerialize做序列化
		setMethodParam(); 
	}
	
	/** 
	 * 外部可配置反射类
	 * @param invoke
	 */
	public MethodInfo setInvoke(BaseInvoke invoke) {
		this.invoke = invoke;
		return this;
	}
	
	/**
	 * 外部可配置序列化类
	 * 注意：如果使用json做序列化，需要对每个序列化的对象指定明确泛型（如果有的话）
	 * @param serialize
	 */
//	public MethodInfo setSerialize(ISerialize serialize) {
//		this.serialize = serialize;
//		return this;
//	}
	
	/**
	 * 设置方法参数，如注解、参数类型
	 */
	private void setMethodParam() {
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		for(int i = 0; i < genericParameterTypes.length; i++) {
			methodParams.add(getMethodParam(genericParameterTypes[i], parameterTypes[i], parameterAnnotations[i]));
		}
	}
	
	/**
	 * 可重写，返回MethodParam的子类
	 * @param type
	 * @param cls
	 * @param annotations
	 * @return
	 */
	public MethodParam getMethodParam(Type type, Class<?> cls, Annotation[] annotations) {
		return new MethodParam(type, cls, annotations);
	}
	
	/**
	 * 反射请求方法
	 * @param bytes
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object invoke(Object[] data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return invoke.invoke(data);
	}
	
	public List<MethodParam> getMethodParams() {
		return methodParams;
	}
	
	public String getMethodStr() {
		return methodStr;
	}
	
	public Method getMethod() {
		return method;
	}
}
