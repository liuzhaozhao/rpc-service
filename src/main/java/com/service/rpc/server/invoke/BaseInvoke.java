package com.service.rpc.server.invoke;

import java.lang.reflect.InvocationTargetException;

public interface BaseInvoke {
	public Object invoke(Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
