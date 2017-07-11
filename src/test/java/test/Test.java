package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.google.common.reflect.TypeToken;
import com.jfinal.kit.HttpKit;

import service.DataBean;

public class Test {
	@org.junit.Test
	public void testConcurrentHashMap() {
		ConcurrentHashMap<String, String> pendingRequest = new ConcurrentHashMap<String, String>();
		pendingRequest.put("q", "qqq");
		pendingRequest.put("w", "www");
		pendingRequest.put("e", "eee");
		pendingRequest.put("r", "rrr");
		for(String val : pendingRequest.values()) {// 需检测是否会出现异常，因为迭代时删除会导致异常，TODO 测试
			if(val.equals("www")) {
				pendingRequest.remove("w");
			} else if (val.equals("qqq")) {
				pendingRequest.remove("q");
			} else if (val.equals("rrr")) {
				pendingRequest.remove("r");
			}
		}
		System.err.println(pendingRequest.size());
	}
	
	@org.junit.Test
	public void test() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		System.err.println(HttpKit.post("http://localhost:8080/test_1/123", "{\"code\":-1,\"msg\":\"error msg\",\"successCode\":false}", headers));
	}
	
	@org.junit.Test
	public void testJson() {
//		IJson json = new FastJson();
//		DataBean<Integer> data = json.toBean("{\"code\":-1,\"msg\":\"error msg\",\"successCode\":false}", new TypeToken<DataBean<Integer>>(){}.getType());
		DataBean<Integer> data = JSON.parseObject("{\"code\":-1,\"msg\":\"error msg\",\"successCode\":false}".getBytes(), new TypeToken<DataBean<Integer>>(){}.getType());
		System.err.println(data.getMsg());
	}
	
	@org.junit.Test
	public void testAtomicInteger() throws InterruptedException {
		AtomicInteger roundRobin = new AtomicInteger(0);
		int threadNum = 1000;
		List<Thread> ts = new ArrayList<Thread>();
		for(int i=0; i<threadNum; i++) {
			ts.add(new Thread(new Runnable() {
				@Override
				public void run() {
					roundRobin.getAndIncrement();
				}
			}));
		}
		for(Thread t : ts) {
			t.start();
		}
		for(Thread t : ts) {
			t.join();
		}
		System.err.println(roundRobin.get());
	}
	
}
