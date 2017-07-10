package com.service.rpc.server.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * java反射调用类
 * 通过测试方法method(int, String)，在连续1亿次调用，是直接调用的2倍时间
 * @author liuzhao
 *
 */
public class JavaInvoke implements BaseInvoke {
	private Method method;// 类反射实例
	private Object instance;// 服务类实例
	
	public JavaInvoke(Method method, Object instance) {
		this.method = method;
		this.instance = instance;
	}

	@Override
	public Object invoke(Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return method.invoke(instance, args);
	}
	
}
