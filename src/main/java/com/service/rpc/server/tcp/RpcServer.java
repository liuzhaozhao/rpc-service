package com.service.rpc.server.tcp;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.service.rpc.common.Utils;
import com.service.rpc.exception.RepeatedPathException;
import com.service.rpc.serialize.FstSerialize;
import com.service.rpc.serialize.ISerialize;

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
	private static Map<String, MethodInfo> methods = new HashMap<String, MethodInfo>();
	private static EventLoopGroup bossGroup;
	private static EventLoopGroup workerGroup;
	
	/**
	 * 开启RPC服务
	 * @throws RepeatedPathException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InterruptedException 
	 */
	public static void start(int port, Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
		start(port, new FstSerialize(), classes);
	}
	public static void start(int port, ISerialize serialize, Class<?>... classes) throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
		initMethod(classes);
		startServer(port, serialize);
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
	 * 初始化RPC方法调用
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private static void initMethod(Class<?>... classes) throws InstantiationException, IllegalAccessException {
		for(Class<?> cls : classes) {
			Object instance = cls.newInstance();
			for(Method method : cls.getMethods()) {
				if(Utils.isObjectMethod(method)) {// 排除Object的公有方法
					continue;
				}
				MethodInfo invoke = new MethodInfo(method, instance);
				addMethodInvoke(method, invoke);
			}
		}
	}
	
	public static MethodInfo getMethodInfo(String identify) {
		return methods.get(identify);
	}
	
	/**
	 * 添加方法反射缓存
	 * @param method
	 * @param methodInvoke
	 */
	public static synchronized void addMethodInvoke(Method method, MethodInfo methodInfo) {
		if(method == null) {
			return;
		}
		String identify = Utils.getMethodIdentify(method);
		if(methods.get(identify) != null) {// 以第一次设置的为准，后面的忽略（重复设置）
			return;
		}
		methods.put(identify, methodInfo);
	}
	
	/**
	 * 开启服务
	 * @param port
	 * @param serialize
	 * @throws InterruptedException
	 */
	private static void startServer(int port, ISerialize serialize) throws InterruptedException {
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
