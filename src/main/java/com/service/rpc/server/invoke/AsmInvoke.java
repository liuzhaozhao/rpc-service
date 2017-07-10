package com.service.rpc.server.invoke;

import java.lang.reflect.Method;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * asm反射调用类
 * 通过测试方法method(int, String)，在连续1亿次调用，是直接调用的2-2.5倍时间
 * @author liuzhao
 *
 */
public class AsmInvoke implements BaseInvoke {
	private MethodAccess methodAccess;// 类反射实例
	private int methodIndex;// 方法对应类的位置
	private Object instance;// 服务类实例
	
	public AsmInvoke(Method method, Object instance) {
		String methodName = method.getName();
		Class<?>[] paramTypes = method.getParameterTypes();
		this.methodAccess = MethodAccess.get(method.getDeclaringClass());
		if(paramTypes != null && paramTypes.length > 0) {
			this.methodIndex = methodAccess.getIndex(methodName, paramTypes);
		} else {
			this.methodIndex = methodAccess.getIndex(methodName, 0);
		}
		this.instance = instance;
	}

	@Override
	public Object invoke(Object[] args) {
		return methodAccess.invoke(instance, methodIndex, args);
	}
	
}
