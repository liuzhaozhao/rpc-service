package rpc;

import java.util.ArrayList;
import java.util.List;

import org.jupiter.rpc.DefaultClient;
import org.jupiter.rpc.JClient;
import org.jupiter.rpc.consumer.ProxyFactory;
import org.jupiter.rpc.load.balance.LoadBalancerType;
import org.jupiter.serialization.SerializerType;
import org.jupiter.transport.JOption;
import org.jupiter.transport.UnresolvedAddress;
import org.jupiter.transport.netty.JNettyTcpConnector;

import com.service.rpc.client.ServiceFactory;
import com.service.rpc.common.JsonUtil;

import service.Bean;
import service.DataBean;
import service.IService;

public class TestClient2 {
	
	private IService getService() throws InstantiationException, IllegalAccessException {
		// .setSerialize(new FastJsonSerialize())
//		ServiceFactory.get().init(new String[]{"127.0.0.1:8808","127.0.0.1:8809"});
//		return ServiceFactory.get(IService.class);
		
//		cn.jugame.http.client.ServiceFactory.ServiceFactory.factory.init(new ServiceSetting("http://localhost:9091", "code", "key").setConnectTimeout(100000).setReadTimeout(100000));
//		return cn.jugame.http.client.ServiceFactory.ServiceFactory.factory.get(IService.class);
		
//		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:motan_client.xml");
//		return (IService) ctx.getBean("service");
		
		int processors = Runtime.getRuntime().availableProcessors();// CPU核心数量，用于初始化数据传输渠道
		// SystemPropertyUtil.setProperty("jupiter.executor.factory.consumer.core.workers",
		// String.valueOf(processors));
		// SystemPropertyUtil.setProperty("jupiter.tracing.needed", "false");//
		// Tracing是否开启(链路跟踪), 默认开启
		// SystemPropertyUtil.setProperty("jupiter.use.non_blocking_hash",
		// "true");
		JClient client = new DefaultClient().withConnector(new JNettyTcpConnector(processors + 1));
		client.connector().config().setOption(JOption.WRITE_BUFFER_HIGH_WATER_MARK, 512 * 1024);
		client.connector().config().setOption(JOption.WRITE_BUFFER_LOW_WATER_MARK, 256 * 1024);
		UnresolvedAddress[] addresses = new UnresolvedAddress[processors];
		for (int i = 0; i < processors; i++) {
			addresses[i] = new UnresolvedAddress("127.0.0.1", 18090);
			client.connector().connect(addresses[i]);// 向服务器建立多个连接
		}
		// 连接RegistryServer
		// client.connectToRegistryServer("127.0.0.1:20001");
		// 自动管理可用连接
		// JConnector.ConnectionWatcher watcherIOrder =
		// client.watchConnections(IOrder.class);
		// 等待连接可用
		// if (!watcherIOrder.waitForAvailable(3000)) {
		// throw new ConnectFailedException();
		// }

		return ProxyFactory.factory(IService.class).group("testGroup").providerName("testProvider")
				.version("1.0.0").client(client).serializerType(SerializerType.HESSIAN)
				.loadBalancerType(LoadBalancerType.ROUND_ROBIN).addProviderAddress(addresses).newProxyInstance();
		// // 自动管理可用连接
		// JConnector.ConnectionWatcher watcherIUser =
		// client.watchConnections(IUser.class);
		// // 等待连接可用
		// if (!watcherIUser.waitForAvailable(3000)) {
		// throw new ConnectFailedException();
		// }
		// userService = ProxyFactory.factory(IUser.class)
		// .version("1.0.0").client(client).newProxyInstance();
	}
	
	/**
	 * 基本测试，包含数据序列化
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@org.junit.Test
	public void start() throws InstantiationException, IllegalAccessException {
		IService service = getService();
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
	public void testThread() throws InstantiationException, IllegalAccessException, InterruptedException {
		IService service = getService();
		service.test3();// 预热
		long startTime = System.currentTimeMillis();
		
		int threadNum = 100;
		List<Thread> ts = new ArrayList<>();
		for(int i=0; i<threadNum; i++) {
//			ts.add(new Thread(() -> {
//				service.test2("arg1", 1, 1.23, new Bean(), BeanUtil.getListBean(), BeanUtil.getMapBean());
//			}));
			
			ts.add(new Thread(() -> {
				try{
					service.test3();
				}catch(Exception e) {
					System.err.println(e.getMessage());
				}
			}));
			
			ts.add(new Thread(() -> {
				service.test4("arg12", 12, 1.234, new Bean(), BeanUtil.getListBean(), BeanUtil.getMapBean());
			}));
		}
		for(Thread t : ts) {
			t.start();
		}
		for(Thread t : ts) {
			t.join();
		}
		System.err.println("耗时："+(System.currentTimeMillis() - startTime));
	}
	
	
}
