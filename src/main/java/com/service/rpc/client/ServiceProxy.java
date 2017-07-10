package com.service.rpc.client;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.service.rpc.common.HashKit;
import com.service.rpc.common.Utils;
import com.service.rpc.transport.RpcRequest;

import javassist.util.proxy.MethodHandler;

/**
 * 接口代理类
 * @author liuzhao
 *
 */
public class ServiceProxy implements MethodHandler {
	private static Map<Method, String> identifys = new HashMap<Method, String>();// 缓存已计算过唯一标识的方法
	private static Map<String, Method> identifyMethod = new HashMap<String, Method>();// 缓存方法唯一标识和方法
	
	private ClientConnect connect;
	private ResetReturn resetReturn;
	
	public ServiceProxy(ClientConnect connect, ResetReturn resetReturn) {
		this.connect = connect;
		this.resetReturn = resetReturn;
	}
	
	public static Method getMethod(String methodIdentify) {
		return identifyMethod.get(methodIdentify);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Method method2, Object[] args) throws Throwable {
		if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
		
		String identify = identifys.get(method);
		if(identify == null) {
			identify = HashKit.md5(Utils.getMethodIdentify(method));
			identifys.put(method, identify);
			identifyMethod.put(identify, method);
		}
		
		RpcRequest request = new RpcRequest(identify, args);
		RpcFuture future = connect.send(request);
		Object data = future.getData();
		if(resetReturn == null) {
			return data;
		} else {
			return resetReturn.reset(method, data);
		}
	}
}
