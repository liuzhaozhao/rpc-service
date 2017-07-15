package test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

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
	public void test() throws InterruptedException {
		long startTime = System.currentTimeMillis();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		List<Thread> ts = new ArrayList<>();
		for(int i=0; i<100; i++) {
			ts.add(new Thread(){
				@Override
				public void run() {
					System.err.println(HttpKit.get("http://localhost:8080/test4?arg1=str&arg2=123&arg3="+Math.random()+"&arg4={\"d\":1.11,\"dataBeans\":[{\"code\":0,\"data\":{\"str\":\"Bean2_str\"},\"msg\":\"\",\"successCode\":0},{\"code\":-1,\"msg\":\"测试\",\"successCode\":0}],\"dou\":2.3,\"flo\":1.5,\"i\":0,\"list\":[1,2,3],\"lon\":123,\"mapBean\":{\"map2\":{\"$ref\":\"$.dataBeans[1]\"},\"map1\":{\"$ref\":\"$.dataBeans[0]\"}},\"s\":\"str\"}&arg5=[{\"code\":0,\"data\":{\"d\":1.11,\"dataBeans\":[{\"code\":0,\"data\":{\"str\":\"Bean2_str\"},\"msg\":\"\",\"successCode\":0},{\"code\":-1,\"msg\":\"测试\",\"successCode\":0}],\"dou\":2.3,\"flo\":1.5,\"i\":0,\"list\":[1,2,3],\"lon\":123,\"mapBean\":{\"map2\":{\"$ref\":\"$[0].data.dataBeans[1]\"},\"map1\":{\"$ref\":\"$[0].data.dataBeans[0]\"}},\"s\":\"str\"},\"msg\":\"\",\"successCode\":0},{\"code\":-1,\"msg\":\"测试\",\"successCode\":0}]&arg6={\"map2\":{\"code\":-1,\"msg\":\"测试\",\"successCode\":0},\"map1\":{\"code\":0,\"data\":{\"d\":1.11,\"dataBeans\":[{\"code\":0,\"data\":{\"str\":\"Bean2_str\"},\"msg\":\"\",\"successCode\":0},{\"code\":-1,\"msg\":\"测试\",\"successCode\":0}],\"dou\":2.3,\"flo\":1.5,\"i\":0,\"list\":[1,2,3],\"lon\":123,\"mapBean\":{\"map2\":{\"$ref\":\"$.map1.data.dataBeans[1]\"},\"map1\":{\"$ref\":\"$.map1.data.dataBeans[0]\"}},\"s\":\"str\"},\"msg\":\"\",\"successCode\":0}}", headers));
				}
			});
		}
		for(Thread t : ts) {
			t.start();
		}
		for(Thread t : ts) {
			t.join();
		}
		System.err.println((System.currentTimeMillis() - startTime));
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
	public void testServerReachable() throws UnknownHostException, IOException {
//		Socket s = new Socket("127.0.0.1",8809);  
//        s.close();
		
		InetSocketAddress address = new InetSocketAddress("127.0.0.1",8809);
		System.err.println(address.getHostName());
		System.err.println(address.getPort());
		
	}
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<>();
//		Set<String> list = new HashSet<String>();
		list.add("abc");
		list.add("qaz");
		list.add("wsx");
		
		System.err.println(StringUtils.join(list));
		list.remove("abc");
		list.add("abc");
		System.err.println(StringUtils.join(list));
	}
}
