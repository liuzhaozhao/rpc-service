package com.service.rpc.server.rpc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.service.rpc.common.HashKit;
import com.service.rpc.common.Utils;
import com.service.rpc.exception.RepeatedPathException;
import com.service.rpc.serialize.FstSerialize;
import com.service.rpc.serialize.ISerialize;
import com.service.rpc.server.common.MethodInfo;
import com.service.rpc.server.rpc.netty.ServerDecoder;
import com.service.rpc.server.rpc.netty.ServerEncoder;
import com.service.rpc.server.rpc.netty.ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class RpcServer {
	private static Logger log = Logger.getLogger(RpcServer.class);
	private static RpcServer server = new RpcServer();
	
	private Map<String, MethodInfo> methods = new HashMap<String, MethodInfo>();
	private int port;
	private Class<?>[] classes;
	private ISerialize serialize;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private boolean enableLog = true;
	private boolean serverStart = false;
	
	private RpcServer() {}
	
	public static RpcServer get() {
		return server;
	}
	
	/**
	 * 开启RPC服务
	 * @throws RepeatedPathException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InterruptedException 
	 */
	public synchronized void start(int port, Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
		Utils.checkStatus(!serverStart, "服务已启动，不可以重复调用");
		this.port = port;
		this.classes = classes;
		if(serialize == null) {
			serialize = new FstSerialize();
		}
		initMethod();
		startServer();
		serverStart = true;
	}
	
	public RpcServer setSerialize(ISerialize serialize) {
		Utils.checkArgument(serialize != null, "序列化配置不能为null");
		Utils.checkStatus(!serverStart, "服务已启动，不可以设置序列化");
		this.serialize = serialize;
		return this;
	}
	
	public RpcServer setEnableLog(boolean enableLog) {
		this.enableLog = enableLog;
		return this;
	}
	
	public static boolean isEnableLog() {
		return server.enableLog;
	}
	
	/**
	 * 关闭服务
	 */
	public static void stop() {
		if(!server.serverStart) {
			return;
		}
		try{
			server.bossGroup.shutdownGracefully();
			server.workerGroup.shutdownGracefully();
		} catch (Exception e) {
			log.warn("关闭http服务异常", e);
		}
	}
	
	public static MethodInfo getMethodInfo(String identify) {
		return server.methods.get(identify);
	}
	
	/**
	 * 初始化RPC方法调用
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void initMethod() throws InstantiationException, IllegalAccessException {
		for(Class<?> cls : classes) {
			Object instance = cls.newInstance();
			Utils.checkArgument(cls.getInterfaces().length > 0, "类必须继承接口");
			for(Class<?> api : cls.getInterfaces()) {
				for(Method method : api.getMethods()) {
					if(Utils.isObjectMethod(method)) {// 排除Object的公有方法
						continue;
					}
					MethodInfo methodInfo = new MethodInfo(method, instance);
					addMethodInvoke(methodInfo);
				}
			}
		}
	}
	
	/**
	 * 添加方法反射缓存
	 * @param method
	 * @param methodInvoke
	 */
	private synchronized void addMethodInvoke(MethodInfo methodInfo) {
		if(methodInfo == null) {
			return;
		}
		String identify = HashKit.md5(methodInfo.getMethodStr());
		if(methods.get(identify) != null) {// 以第一次设置的为准，后面的忽略（重复设置）
			return;
		}
		log.info("添加rpc方法："+methodInfo.getMethodStr());
		methods.put(identify, methodInfo);
	}
	
	/**
	 * 开启服务
	 * @param port
	 * @param serialize
	 * @throws InterruptedException
	 */
	private void startServer() throws InterruptedException {
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024)
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(65536,0,4,0,0))
                                .addLast(new ServerDecoder(serialize))
                                .addLast(new ServerEncoder(serialize))
                                .addLast(new ServerHandler());
                    }
                })
	            .childOption(ChannelOption.SO_KEEPALIVE, true)
	            .childOption(ChannelOption.TCP_NODELAY, true);
			
			ChannelFuture future = bootstrap.bind(port).sync();
			
			log.info("rpc server listening on port " + port);
			future.channel().closeFuture().sync();
		}finally{
			stop();
		}
	}
}
