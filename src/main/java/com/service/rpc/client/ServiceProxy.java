package com.service.rpc.client;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.service.rpc.common.HashKit;
import com.service.rpc.common.Utils;
import com.service.rpc.common.json.FastJson;
import com.service.rpc.common.json.IJson;
import com.service.rpc.exception.ServerRuntimeException;
import com.service.rpc.transport.RpcRequest;
import com.service.rpc.transport.RpcResponse;

import javassist.util.proxy.MethodHandler;

/**
 * 接口代理类
 * @author liuzhao
 *
 */
public class ServiceProxy implements MethodHandler {
	private static Logger log = Logger.getLogger(ServiceProxy.class);
	private static Map<Method, String> identifys = new HashMap<Method, String>();// 缓存已计算过唯一标识的方法
	private static Map<String, Method> identifyMethod = new HashMap<String, Method>();// 缓存方法唯一标识和方法
	private static Map<String, String> methodStr = new HashMap<String, String>();// 缓存方法的描述
	private static IJson json = new FastJson();
	
	private ConnectManage connect;
	private ResetReturn resetReturn;
	
	public ServiceProxy(ConnectManage connect, ResetReturn resetReturn) {
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
		
		long startTime = System.currentTimeMillis();
		String identify = identifys.get(method);
		if(identify == null) {
			String methodToString = Utils.getMethodIdentify(method);
			identify = HashKit.md5(methodToString);
			identifys.put(method, identify);
			identifyMethod.put(identify, method);
			methodStr.put(identify, methodToString);
		}
		RpcRequest request = new RpcRequest(identify, args);
		RpcFuture future = connect.send(request);
//		Object data = future.get(ServiceFactory.factory.getReadTimeoutMills(), TimeUnit.MILLISECONDS);
		RpcResponse response = future.get();
		Object data = null;
		boolean warnError = false;
		try{
			if(response != null && response.getError() != null) {// 客户端异常
				throw new RuntimeException(response.getError());
			} else if (response != null && response.getResponseCode() == RpcResponse.CODE_SUCCESS) {
				data = response.getData();
			} else if (response != null && response.getErrorMsg() != null) {// 服务器端异常
				throw new ServerRuntimeException(response.getErrorMsg());
			} else {// 这里暂时没有情况执行
				warnError = false;
				throw new RuntimeException("请求异常");
			}
		}finally {
			if(ServiceFactory.factory.isEnableLog()) {
				log.info(methodStr.get(identify)+"耗时："+(System.currentTimeMillis() - startTime)+"毫秒，返回数据"+(warnError?"(异常数据无错误原因)":"")+"："+json.toStr(future.getResponse()));
			}
		}
		if(resetReturn == null) {
			return data;
		} else {
			return resetReturn.reset(method, data);
		}
	}
}
