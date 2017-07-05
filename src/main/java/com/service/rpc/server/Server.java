package com.service.rpc.server;

import org.apache.log4j.Logger;

import com.service.rpc.exception.RepeatedPathException;
import com.service.rpc.server.http.HttpServer;

public class Server {
	private static final Logger log = Logger.getLogger(Server.class);
	
	/**
	 * 启动服务(包含RPC服务和HTTP服务)
	 * @param port
	 * @param classes
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws RepeatedPathException 
	 * @throws InterruptedException 
	 */
	public static void start(int port, Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
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
	 * @throws InterruptedException 
	 */
	public static void startHttp(int port, Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
		HttpServer.start(port, classes);
	}
	
	/**
	 * 初始化rpc方法调用
	 */
	private static void initRpcMethod(Class<?>... classes) {
		
	}
	
	/**
	 * 关闭服务(停止对外提供http、rpc服务)
	 */
	public static void stop() {
		HttpServer.stop();
	}
}
