package com.service.rpc.client;

import java.util.HashMap;
import java.util.Map;

import com.service.rpc.common.Utils;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

public class ServiceFactory {
	public static ServiceFactory factory = new ServiceFactory();// 客户端静态调用
	
//	private Logger log = Logger.getLogger(this.getClass());
	private Map<Class<?>, Object> proxyService = new HashMap<Class<?>, Object>();// 接口代理实例缓存
	// 禁止外部创建实例
	private ServiceFactory(){}
	
	public void init() {
		
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
		((ProxyObject) service).setHandler(ServiceProxy.proxy);// 所有服务代理使用一个代理实例，TODO 需测试并发
		proxyService.put(cls, service);
		return service;
	}
}
