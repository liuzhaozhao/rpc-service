package com.service.rpc.server.http;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.service.rpc.common.Utils;
import com.service.rpc.common.json.FastJson;
import com.service.rpc.common.json.IJson;
import com.service.rpc.exception.NoPathException;
import com.service.rpc.exception.RepeatedPathException;
import com.service.rpc.server.http.method.HttpMethod;
import com.service.rpc.server.http.method.HttpMethodInfo;
import com.service.rpc.server.http.netty.HttpServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpServer {
	private static Logger log = Logger.getLogger(HttpServer.class);
	private static HttpServer server = new HttpServer();
	private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private int port;
	private Class<?>[] classes;
	private IJson json;
	private boolean enableLog = true;
	private boolean serverStart = false;
	private IAuth auth;
	
	private HttpServer() {}// 私有化构造方法，防止外部创建实例
	
	public static HttpServer get() {
		return server;
	}
	
	/**
	 * 开启http服务
	 * @throws RepeatedPathException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InterruptedException 
	 */
	public synchronized void start(int port, Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
		Utils.checkStatus(!serverStart, "服务已启动，不可以重复调用");
		this.port = port;
		this.classes = classes;
		if(json == null) {
			json = new FastJson();
		}
		initMethod();
		startServer();
		serverStart = true;
	}
	
	public HttpServer setEnableLog(boolean enableLog) {
		this.enableLog = enableLog;
		return this;
	}
	
	public HttpServer setJson(IJson json) {
		Utils.checkArgument(json != null, "序列化配置不能为null");
		Utils.checkStatus(!serverStart, "服务已启动，不可以设置序列化");
		this.json = json;
		return this;
	}
	
	public HttpServer auth(IAuth auth) {
		this.auth = auth;
		return this;
	}
	
	public static boolean isEnableLog() {
		return server.enableLog;
	}
	
	public static IJson getJson() {
		return server.json;
	}
	
	public static IAuth getAuth() {
		return server.auth;
	}
	
	/**
	 * 关闭服务
	 */
	public static void stop() {
		try{
			server.bossGroup.shutdownGracefully();
			server.workerGroup.shutdownGracefully();
		} catch (Exception e) {
			log.warn("关闭http服务异常", e);
		}
	}
	
	public static void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }
	
	/**
	 * 初始化Http方法调用
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws RepeatedPathException 
	 */
	private void initMethod() throws InstantiationException, IllegalAccessException, RepeatedPathException {
		for(Class<?> cls : classes) {
			Object instance = cls.newInstance();
			for(Method method : cls.getMethods()) {
				if(Utils.isObjectMethod(method)) {// 排除Object的公有方法
					continue;
				}
				try {
					HttpMethodInfo methodInfo = new HttpMethodInfo(method, instance);
					HttpMethod.addMethodInfo(methodInfo);
				} catch (NoPathException e) {
					log.info(e.getMessage());
				}
			}
		}
	}
	
	private void startServer() throws InterruptedException {
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
