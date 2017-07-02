package com.service.rpc.client;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.service.rpc.common.HashKit;
import com.service.rpc.common.Utils;

import javassist.util.proxy.MethodHandler;

/**
 * 接口代理类
 * @author liuzhao
 *
 */
public class ServiceProxy implements MethodHandler {
	public static ServiceProxy proxy = new ServiceProxy();
	
	private static Map<Method, String> identifys = new HashMap<Method, String>();// 缓存已计算过唯一标识的方法
	
	@Override
	public Object invoke(Object arg0, Method method, Method arg2, Object[] arg3) throws Throwable {
		String identify = identifys.get(method);
		if(identify == null) {
			identify = HashKit.md5(Utils.getMethodIdentify(method));
			identifys.put(method, identify);
		}
		// TODO 封装方法唯一标识与参数值，通过TCP调用服务器
		return "proxy2";
	}
}
