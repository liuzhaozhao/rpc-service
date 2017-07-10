package test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.google.common.reflect.TypeToken;
import com.jfinal.kit.HttpKit;
import com.service.rpc.common.json.FastJson;
import com.service.rpc.common.json.IJson;
import com.service.rpc.transport.RpcResponse;

import test.service.DataBean;

public class Test {
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
	
	@org.junit.Test
	public void testType() {
		IJson json = new FastJson();
		RpcResponse response = new RpcResponse(0, "11", null);
		response.setDataType(new TypeToken<DataBean<Integer>>(){}.getType());
		System.err.println(new TypeToken<DataBean<Integer>>(){}.getType());
		String jsonStr = json.toStr(response);
		System.err.println(jsonStr);
		RpcResponse response2 = json.toBean(jsonStr, new TypeToken<RpcResponse>(){}.getType());
		System.err.println(response2.getDataType());
		
		
		
//		List<Type> types = new ArrayList<Type>();
//		types.add(new TypeToken<DataBean<Integer>>(){}.getType());
//		String jsonStr = json.toStr(types);
//		System.err.println(jsonStr);
//		
//		List<Type> tl2 = json.toBean(jsonStr, new TypeToken<List<Type>>(){}.getType());
//		Type t = tl2.get(0);
//		System.err.println(t.getTypeName());
	}
}
