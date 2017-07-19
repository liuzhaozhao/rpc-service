package com.service.rpc.client;

import java.util.HashMap;
import java.util.Map;

import com.service.rpc.client.connect.manage.ConnectManage;
import com.service.rpc.client.connect.manage.DefaultConnectManage;
import com.service.rpc.client.connect.pool.NettyPool;
import com.service.rpc.client.connect.pool.Pool;
import com.service.rpc.common.Utils;
import com.service.rpc.serialize.FstSerialize;
import com.service.rpc.serialize.ISerialize;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

public class ServiceFactory {
	private static ServiceFactory factory = new ServiceFactory();// 客户端静态调用
	
//	private Logger log = Logger.getLogger(this.getClass());
	private Map<Class<?>, Object> proxyService = new HashMap<Class<?>, Object>();// 接口代理实例缓存
	private Pool connectPool;
	private ISerialize serialize;
	private String[] serverAddress;
	private ResetReturn resetReturn;
	private int readTimeoutMills = 10000;// 读取响应超时时间，不要设置太长，如果数据在服务器解析过程出现异常，则客户端只能等待这么长时间才能收到响应
	private boolean enableLog = true;
	private boolean start = false;
	private int retryTimes = 3;// 请求数据失败时，最多重试的次数（不算第一次请求）
	private long waitconnectTimeoutMills = 5000;// 当所有连接都不可用时，最大等待连接的时间
	private ConnectManage connectManage;
	
	// 禁止外部创建实例
	private ServiceFactory(){}
	
	public static ServiceFactory get() {
		return factory;
	}
	
	public synchronized void init(String[] serverAddress) {
		Utils.checkArgument(serverAddress != null && serverAddress.length > 0, "客户端连接不能为null");
		Utils.checkStatus(!start, "服务已启动，不可以重复调用");
		this.serverAddress = serverAddress;
		if(connectPool == null) {
			connectPool = NettyPool.getInstance();
		}
		if(serialize == null) {
			serialize = new FstSerialize();
		}
//		connect.updateConnect(Arrays.asList(serverAddress));
		if(connectManage == null) {
			connectManage = new DefaultConnectManage();
		}
		connectManage.connect(serverAddress);
		start = true;
	}
	
	public ServiceFactory setConnectPool(Pool connectPool) {
		Utils.checkArgument(connectPool != null, "客户端连接不能为null");
		Utils.checkStatus(!start, "服务已启动，不可以设置连接池");
		this.connectPool = connectPool;
		return this;
	}
	
	public ServiceFactory setResetReturn(ResetReturn resetReturn) {
		this.resetReturn = resetReturn;
		return this;
	}
	
	public ServiceFactory setSerialize(ISerialize serialize) {
		Utils.checkArgument(serialize != null, "序列化配置不能为null");
		Utils.checkStatus(!start, "服务已启动，不可以设置序列化");
		this.serialize = serialize;
		return this;
	}
	
	public ServiceFactory setReadTimeoutMills(int readTimeoutMills) {
		Utils.checkArgument(readTimeoutMills > 0, "读超时不能小于0");
		this.readTimeoutMills = readTimeoutMills;
		return this;
	}
	
	public ServiceFactory setEnableLog(boolean enableLog) {
		this.enableLog = enableLog;
		return this;
	}
	
	public ServiceFactory setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
		return this;
	}
	
	public ServiceFactory setWaitconnectTimeoutMills(long waitconnectTimeoutMills) {
		this.waitconnectTimeoutMills = waitconnectTimeoutMills;
		return this;
	}
	
	public ServiceFactory setConnectManage(ConnectManage connectManage) {
		this.connectManage = connectManage;
		return this;
	}
	

	public static ISerialize getSerialize() {
		return factory.serialize;
	}
	
	public static int getReadTimeoutMills() {
		return factory.readTimeoutMills;
	}
	
	public static boolean isEnableLog() {
		return factory.enableLog;
	}
	
	public static int getRetryTimes() {
		return factory.retryTimes;
	}
	
	public static long getWaitconnectTimeoutMills() {
		return factory.waitconnectTimeoutMills;
	}
	
	public static Pool getConnectPool() {
		return factory.connectPool;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> cls) throws InstantiationException, IllegalAccessException {
		Utils.checkArgument(factory.serverAddress != null && factory.serverAddress.length > 0, "未初始化服务地址，请执行init方法");
		Utils.validateServiceInterface(cls);
		Object instance = factory.proxyService.get(cls);
		if (instance != null) {// 缓存中已存在改实例则直接返回
			return (T) instance;
		}
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setInterfaces(new Class[] { cls });// 指定接口
		Class<?> proxyClass = proxyFactory.createClass();
		T service = (T) proxyClass.newInstance();// 设置Handler处理器
		((ProxyObject) service).setHandler(new ServiceProxy(factory.connectPool, factory.resetReturn));// 所有服务代理使用一个代理实例
		factory.proxyService.put(cls, service);
		return service;
	}
}
