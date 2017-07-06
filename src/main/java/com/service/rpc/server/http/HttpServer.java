package com.service.rpc.server.http;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.service.rpc.common.Utils;
import com.service.rpc.exception.NoPathException;
import com.service.rpc.exception.RepeatedPathException;
import com.service.rpc.server.MethodInvoke;
import com.service.rpc.server.Server;
import com.service.rpc.server.http.method.HttpMethod;
import com.service.rpc.server.http.method.MethodInfo;
import com.service.rpc.server.http.nettyChannel.HttpServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpServer {
	private static Logger log = Logger.getLogger(Server.class);
	private static EventLoopGroup bossGroup;
	private static EventLoopGroup workerGroup;
	
	/**
	 * 开启http服务
	 * @throws RepeatedPathException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InterruptedException 
	 */
	public static void start(int port, Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
		initMethod(classes);
		startServer(port);
	}
	
	/**
	 * 关闭服务
	 */
	public static void stop() {
		try{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		} catch (Exception e) {
			log.warn("关闭http服务异常", e);
		}
	}
	
	/**
	 * 初始化Http方法调用
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws RepeatedPathException 
	 */
	private static void initMethod(Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException {
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
					log.info(e.getMessage());
				}
//				System.err.println(Utils.getMethodIdentify(method));
			}
		}
	}
	
	private static void startServer(int port) throws InterruptedException {
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024)
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new HttpServerInitializer())
	            .childOption(ChannelOption.SO_KEEPALIVE, true)
	            .childOption(ChannelOption.TCP_NODELAY, true);
			
			ChannelFuture future = bootstrap.bind(port).sync();
			
			log.info("http server listening on port " + port);
			future.channel().closeFuture().sync();
		}finally{
			stop();
		}
	}
}
