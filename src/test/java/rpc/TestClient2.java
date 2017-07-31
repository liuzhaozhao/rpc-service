package rpc;

import java.util.ArrayList;
import java.util.List;

import com.service.rpc.client.connect.manage.ZookeeperConnectManage;
import com.service.rpc.common.JsonUtil;

import service.Bean;
import service.DataBean;
import service.IService;

public class TestClient2 {
	
	private IService getService() throws Exception {
		// .setSerialize(new FastJsonSerialize())
//		com.service.rpc.client.ServiceFactory.get().setConnectManage(new ZookeeperConnectManage()).init(new String[]{"127.0.0.1:8808","127.0.0.1:8809"});
		try{
			com.service.rpc.client.ServiceFactory.get().setConnectManage(new ZookeeperConnectManage()).init(new String[]{"127.0.0.1:2181"});
			return com.service.rpc.client.ServiceFactory.get(IService.class);
		}catch(Exception e) {
			e.printStackTrace(); 
		}
		return null;
		
		
//		cn.jugame.http.client.ServiceFactory.ServiceFactory.factory.init(new ServiceSetting("http://localhost:9091", "code", "key").setConnectTimeout(100000).setReadTimeout(100000));
//		return cn.jugame.http.client.ServiceFactory.ServiceFactory.factory.get(IService.class);
		
		
//		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:motan_client.xml");
//		return (IService) ctx.getBean("service");
		
		
//		return (IService) Naming.lookup("rmi://localhost:8888/Service");
		
		
//		int processors = Runtime.getRuntime().availableProcessors();// CPU核心数量，用于初始化数据传输渠道
//		JClient client = new DefaultClient().withConnector(new JNettyTcpConnector(processors + 1));
//		client.connector().config().setOption(JOption.WRITE_BUFFER_HIGH_WATER_MARK, 512 * 1024);
//		client.connector().config().setOption(JOption.WRITE_BUFFER_LOW_WATER_MARK, 256 * 1024);
//		UnresolvedAddress[] addresses = new UnresolvedAddress[processors];
//		for (int i = 0; i < processors; i++) {
//			addresses[i] = new UnresolvedAddress("127.0.0.1", 18090);
//			client.connector().connect(addresses[i]);// 向服务器建立多个连接
//		}
//		return ProxyFactory.factory(IService.class).group("testGroup").providerName("testProvider")
//				.version("1.0.0").client(client).serializerType(SerializerType.HESSIAN)
//				.loadBalancerType(LoadBalancerType.ROUND_ROBIN).addProviderAddress(addresses).newProxyInstance();
	}
	
	/**
	 * 基本测试，包含数据序列化
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@org.junit.Test
	public void start() throws Exception {
		IService service = getService();
//		Thread.sleep(100000);
//		service.test1();
//		service.test2("arg1", 1, 1.23, new Bean(), BeanUtil.getListBean(), BeanUtil.getMapBean());
		System.err.println(service.test3());
		List<DataBean<Bean>> bean = service.test4("arg12", 12, 1.234, new Bean(), BeanUtil.getListBean(), BeanUtil.getMapBean());
		System.err.println(JsonUtil.toJson(bean));
//		System.err.println(bean.get(0).getData().getDataBeans().get(0).getData().getStr());
//		System.err.println(bean.get(0).getData().getMapBean().get("map2").getMsg());
		
	}
	
	/**
	 * 性能测试
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InterruptedException
	 */
	@org.junit.Test
	public void testThread() throws Exception {
		IService service = getService();
//		service.test3();// 预热
		long startTime = System.currentTimeMillis();
		
		int threadNum = 20;
		List<Thread> ts = new ArrayList<>();
		for(int i=0; i<threadNum; i++) {
			ts.add(new Thread(() -> {
				try {
					service.test3();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(6000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}));
			
			ts.add(new Thread(() -> {
				try {
					service.test4("arg12", 12, 1.234, new Bean(), BeanUtil.getListBean(), BeanUtil.getMapBean());
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(6000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}));
		}
		for(Thread t : ts) {
			t.run();
		}
//		for(Thread t : ts) {
//			t.join();
//		}
		System.err.println("耗时："+(System.currentTimeMillis() - startTime));
	}
	
	
}
