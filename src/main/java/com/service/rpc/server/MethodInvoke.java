package com.service.rpc.server;

import java.lang.reflect.Method;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * 该类用于通过反射调用类方法
 * @author liuzhao
 *
 * @param <T>
 */
public class MethodInvoke {
	private MethodAccess methodAccess;// 类反射实例
	private int methodIndex;// 方法对应类的位置
	private Object instance;// 服务类实例
	
	public MethodInvoke(Method method, Object instance) {
		this(method.getDeclaringClass(), method.getName(), instance, method.getParameterTypes());
	}
	
	private MethodInvoke(Class<?> serviceClass, String methodName, Object instance, Class<?>... paramTypes) {
		this.methodAccess = MethodAccess.get(serviceClass);
		if(paramTypes != null && paramTypes.length > 0) {
			this.methodIndex = methodAccess.getIndex(methodName, paramTypes);
		} else {
			this.methodIndex = methodAccess.getIndex(methodName, 0);
		}
		this.instance = instance;
	}
	
	public Object invoke(Object[] args) {
		return methodAccess.invoke(instance, methodIndex, args);
	}
}
