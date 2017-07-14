package rpc;

import org.jupiter.common.util.SystemPropertyUtil;
import org.jupiter.monitor.MonitorServer;
import org.jupiter.rpc.DefaultServer;
import org.jupiter.rpc.JServer;
import org.jupiter.transport.JOption;
import org.jupiter.transport.netty.JNettyTcpAcceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.service.rpc.exception.RepeatedPathException;
import com.service.rpc.server.http.HttpServer;
import com.service.rpc.server.rpc.RpcServer;

import service.IService;
import service.Service;

public class TestServer {
	@org.junit.Test
	public void start() throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
//		RpcServer.get().start(8809, TestService.class);
		// .setSerialize(new FastJsonSerialize())
		RpcServer.get().start(8809, Service.class);
		
	}
	
	@org.junit.Test
	public void startHttp() throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
//		RpcServer.get().start(8809, TestService.class);
		// .setSerialize(new FastJsonSerialize())
		HttpServer.get().start(8080, Service.class);
		
	}
	
//	@org.junit.Test
//	public void startRmi() throws Exception {
//		LocateRegistry.createRegistry(8888); 
//		Naming.bind("rmi://localhost:8888/Service",new Service()); 
//		System.err.println("服务器已启动");
//		Thread.sleep(1000000);
//	}
	
	@org.junit.Test
	public void startMotan() throws InterruptedException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:motan_server3.xml");
		System.err.println("服务器已启动");
		Thread.sleep(1000000);
	}
	
	@org.junit.Test
	public void startJupiter() throws Exception {
		// final int processors = Runtime.getRuntime().availableProcessors();
		// SystemPropertyUtil.setProperty("jupiter.executor.factory.provider.core.workers",
		// String.valueOf(processors));
		 SystemPropertyUtil.setProperty("jupiter.metric.needed", "false");//
		// 是否启用provider的指标度量, 默认启用
		// SystemPropertyUtil.setProperty("jupiter.metric.csv.reporter",
		// "false");
		SystemPropertyUtil.setProperty("jupiter.metric.report.period", "1");
		// SystemPropertyUtil.setProperty("jupiter.executor.factory.provider.queue.capacity",
		// "65536");
		JServer server = new DefaultServer().withAcceptor(new JNettyTcpAcceptor(18090));
		server.acceptor().configGroup().child().setOption(JOption.WRITE_BUFFER_HIGH_WATER_MARK, 256 * 1024);
		server.acceptor().configGroup().child().setOption(JOption.WRITE_BUFFER_LOW_WATER_MARK, 128 * 1024);
		MonitorServer monitor = new MonitorServer();
		// provider
		IService service = new Service();
		// IUser userService = new UserService();
		monitor.start();
		// 本地注册
		// ServiceWrapper provider =
		server.serviceRegistry().interfaceClass(IService.class).group("testGroup").providerName("testProvider")
				.version("1.0.0").provider(service)
				// .provider(userService)
				.register();
		// 连接注册中心
		// server.connectToRegistryServer("127.0.0.1:20001");
		// 向注册中心发布服务
		// server.publish(provider);
		// 启动server
		server.acceptor().start();
	}
	
//	@org.junit.Test
//	public void startHttpServer() throws InterruptedException {
//		Server.start(9091, new SimpleAuth() {
//			@Override
//			public String getKey(String busCode) {
//				return "key";
//			}
//		}, Service.class);
//		System.err.println("服务器已启动");
//		Thread.sleep(1000000);
//	}
}
