package test.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.service.rpc.common.HashKit;
import com.service.rpc.common.Utils;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import test.service.ITestService;

public class Test {
	public static void main(String[] args) throws Exception {
//		ITestService serviceProxy = getJavaProxy();
		ITestService serviceProxy = getJavassistProxy();
//		int times = 2000000;
		int times = 2147483647;
//		long times = 2147483647;
		long start = System.currentTimeMillis();
		for(int i = 0; i < times; i++) {
			serviceProxy.testM();
		}
		System.err.println(System.currentTimeMillis() - start);
		
//		serviceProxy.testM();
//		serviceProxy.testM2(1, "test");
//		serviceProxy.testM();
//		serviceProxy.testM("1111");
//		serviceProxy.testM("1111", 1);
//		serviceProxy.testM(1, "1111");
//		serviceProxy.testM("1111", 1);
	}
	
	private static ITestService getJavaProxy() {
		return (ITestService) Proxy.newProxyInstance(ITestService.class.getClassLoader(), new Class<?>[] { ITestService.class }, new InvocationHandler() {
			private Map<Method, String> identifys = new HashMap<Method, String>();
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String identify = identifys.get(method);
				if(identify != null) {
					System.err.println("cache identify="+identify);
					return "proxy1";
				}
				identify = Utils.getMethodIdentify(method);
				identifys.put(method, identify);
				System.err.println("identify="+identify);
				identifys.put(method, identify);
				return "proxy1";
			}
		});
		
	}
	
	private static ITestService getJavassistProxy() throws Exception {
		ProxyFactory proxyFactory = new ProxyFactory();  
        proxyFactory.setInterfaces(new Class[]{ITestService.class});  //指定接口  
        Class<?> proxyClass = proxyFactory.createClass();  
        ITestService javassistProxy = (ITestService) proxyClass.newInstance(); //设置Handler处理器  
        ((ProxyObject) javassistProxy).setHandler(new MethodHandler() {
        	private Map<Method, String> identifys = new HashMap<Method, String>();
        	
			@Override
			public Object invoke(Object arg0, Method method, Method arg2, Object[] arg3) throws Throwable {
				String identify = identifys.get(method);
				if(identify != null) {
//					System.err.println("cache identify="+identify);
					return "proxy2";
				}
				identify = HashKit.md5(Utils.getMethodIdentify(method));
				identifys.put(method, identify);
//				System.err.println("identify="+identify);
				return "proxy2";
			}
		});  
        return javassistProxy;  
	}
	
}
