package com.service.rpc.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.service.rpc.client.netty.NettyConnect;
import com.service.rpc.common.Utils;
import com.service.rpc.serialize.FstSerialize;
import com.service.rpc.serialize.ISerialize;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

public class ServiceFactory {
	public static ServiceFactory factory = new ServiceFactory();// 客户端静态调用
	
//	private Logger log = Logger.getLogger(this.getClass());
	private Map<Class<?>, Object> proxyService = new HashMap<Class<?>, Object>();// 接口代理实例缓存
	private ClientConnect connect = NettyConnect.getInstance();
	private ISerialize serialize = new FstSerialize();
	private String[] serverAddress;
	private ResetReturn resetReturn;
	// 禁止外部创建实例
	private ServiceFactory(){}
	
	public ServiceFactory init(String[] serverAddress) {
		this.serverAddress = serverAddress;
		return this;
	}
	
	public ServiceFactory setConnect(ClientConnect connect) {
		if(connect == null) {
			throw new IllegalArgumentException("参数不能为null");
		}
		this.connect = connect;
		return this;
	}
	
	public ServiceFactory setResetReturn(ResetReturn resetReturn) {
		this.resetReturn = resetReturn;
		return this;
	}
	
	public ServiceFactory setSerialize(ISerialize serialize) {
		if(serialize == null) {
			throw new IllegalArgumentException("参数不能为null");
		}
		this.serialize = serialize;
		return this;
	}
	
	public ISerialize getSerialize() {
		return serialize;
	}
	
	/**
	 * 初始化完成后，必须执行该方法
	 */
	public void done(){
		connect.updateConnect(Arrays.asList(serverAddress));
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> cls) throws InstantiationException, IllegalAccessException {
		Utils.validateServiceInterface(cls);
		Object instance = proxyService.get(cls);
		if (instance != null) {// 缓存中已存在改实例则直接返回
			return (T) instance;
		}
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setInterfaces(new Class[] { cls });// 指定接口
		Class<?> proxyClass = proxyFactory.createClass();
		T service = (T) proxyClass.newInstance();// 设置Handler处理器
		((ProxyObject) service).setHandler(new ServiceProxy(connect, resetReturn));// 所有服务代理使用一个代理实例，TODO 需测试并发
		proxyService.put(cls, service);
		return service;
	}
}
