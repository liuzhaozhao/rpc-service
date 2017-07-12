package rpc;

import java.util.ArrayList;
import java.util.List;

import com.service.rpc.client.ServiceFactory;
import com.service.rpc.common.JsonUtil;
import com.service.rpc.serialize.FastJsonSerialize;

import service.Bean;
import service.DataBean;
import service.IService;

public class TestClient2 {
	
	private IService getService() throws InstantiationException, IllegalAccessException {
		// .setSerialize(new FastJsonSerialize())
		ServiceFactory.init(new String[]{"127.0.0.1:8808","127.0.0.1:8809"}).setSerialize(new FastJsonSerialize());
		return ServiceFactory.get(IService.class);
		
//		cn.jugame.http.client.ServiceFactory.ServiceFactory.factory.init(new ServiceSetting("http://localhost:9091", "code", "key").setConnectTimeout(100000).setReadTimeout(100000));
//		return cn.jugame.http.client.ServiceFactory.ServiceFactory.factory.get(IService.class);
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
