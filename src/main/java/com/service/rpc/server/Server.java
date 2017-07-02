package com.service.rpc.server;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.service.rpc.common.Utils;
import com.service.rpc.exception.NoPathException;
import com.service.rpc.exception.RepeatedPathException;
import com.service.rpc.server.http.HttpMethod;
import com.service.rpc.server.http.MethodInfo;

public class Server {
	private static final Logger log = Logger.getLogger(Server.class);
	
	/**
	 * 启动服务(包含RPC服务和HTTP服务)
	 * @param port
	 * @param classes
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws RepeatedPathException 
	 */
	public static void start(int port, Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException {
		initRpcMethod(classes);
		startHttp(port, classes);
	}
	/**
	 * 仅启动HTTP服务
	 * @param port
	 * @param classes
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws RepeatedPathException 
	 */
	public static void startHttp(int port, Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException {
		initHttpMethod(classes);
	}
	
	/**
	 * 初始化rpc方法调用
	 */
	private static void initRpcMethod(Class<?>... classes) {
		
	}
	
	/**
	 * 初始化Http方法调用
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws RepeatedPathException 
	 */
	private static void initHttpMethod(Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException {
		for(Class<?> cls : classes) {
			Object instance = cls.newInstance();
			for(Method method : cls.getMethods()) {
				if(Utils.isObjectMethod(method)) {// 排除Object的公有方法
					continue;
				}
				MethodInvoke invoke = new MethodInvoke(method, instance);
				try {
					MethodInfo methodInfo = new MethodInfo(invoke, method);
					HttpMethod.addMethodInfo(methodInfo);
				} catch (NoPathException e) {
					log.info(e.getMessage(), e);
				}
//				System.err.println(Utils.getMethodIdentify(method));
			}
		}
	}
	
	/**
	 * 关闭服务(停止对外提供http、rpc服务)
	 */
	public static void stop() {
		 
	}
}
