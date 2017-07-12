package rpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.service.rpc.client.ServiceFactory;

import service.DataBean;
import test.service.ITestService;

public class TestClient {
	@org.junit.Test
	public void start() throws InstantiationException, IllegalAccessException {
		// .setSerialize(new FastJsonSerialize())
		ServiceFactory.get().init(new String[]{"127.0.0.1:8808","127.0.0.1:8809"});
		ITestService service = ServiceFactory.get(ITestService.class);
		System.err.println(service.testM());
		System.err.println(service.testM("arg1"));
		System.err.println(service.testM(1, "arg2"));
		System.err.println(service.testM("arg3", 2));
		System.err.println(service.testM2(3, "arg4"));
		DataBean<List<String>> dataBean = new DataBean<List<String>>();
		dataBean.setData(Arrays.asList("str1","str2"));
		dataBean.setMsg("测试信息");
		System.err.println(service.test_1(4, "arg5", 5, dataBean).getData().get(0));
	}
	
	@org.junit.Test
	public void testThread() throws InstantiationException, IllegalAccessException, InterruptedException {
		ServiceFactory.get().init(new String[]{"127.0.0.1:8808","127.0.0.1:8809"});
		ITestService service = ServiceFactory.get(ITestService.class);
		System.err.println(service.testM());
		long startTime = System.currentTimeMillis();
		
		int threadNum = 100;
		List<Thread> ts = new ArrayList<>();
		for(int i=0; i<threadNum; i++) {
			ts.add(new Thread(() -> {
				String data = service.testM(); 
				if(!"testM()".equals(data)) {
					System.err.println("testM() 返回值异常："+data);
				}
			}));
			
			ts.add(new Thread(() -> {
				String data = service.testM("arg1");
				if(!"testM(arg1)".equals(data)) {
					System.err.println("testM(arg1) 返回值异常："+data);
				}
			}));
			
			ts.add(new Thread(() -> {
				String data = service.testM(1, "arg2");
				if(!"testM(1, arg2)".equals(data)) {
					System.err.println("testM(1, arg2) 返回值异常："+data);
				}
			}));

			ts.add(new Thread(() -> {
				String data = service.testM("arg3", 2);
				if(!"testM(arg3, 2)".equals(data)) {
					System.err.println("testM(arg3, 2) 返回值异常："+data);
				}
			}));
			
			ts.add(new Thread(() -> {
				String data = service.testM2(3, "arg4");
				if(!"testM2(3, arg4)".equals(data)) {
					System.err.println("testM2(3, arg4) 返回值异常："+data);
				}
			}));
			
			ts.add(new Thread(() -> {
				DataBean<List<String>> dataBean = new DataBean<List<String>>();
				dataBean.setData(Arrays.asList("str1","str2"));
				dataBean.setMsg("测试信息");
				DataBean<List<String>> bean =service.test_1(4, "arg5", 5, dataBean);
//				if(!"return testM2(3, arg4)".equals(bean)) {
//					System.err.println("testM2(3, arg4) 返回值异常："+bean);
//				}
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
