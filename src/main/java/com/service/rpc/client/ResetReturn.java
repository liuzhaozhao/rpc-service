package com.service.rpc.client;

import java.lang.reflect.Method;

public interface ResetReturn {
	/**
	 * 重置返回值
	 * @param method	当前请求的方法
	 * @param returnData	当前的返回值
	 * @return
	 */
	public Object reset(Method method, Object returnData);
}
