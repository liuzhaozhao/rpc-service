package test;

import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.jfinal.kit.HttpKit;
import com.service.rpc.serialize.json.FastJson;
import com.service.rpc.serialize.json.IJson;

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
		IJson json = new FastJson();
		DataBean<Integer> data = json.toBean("{\"code\":-1,\"msg\":\"error msg\",\"successCode\":false}", new TypeToken<DataBean<Integer>>(){}.getType());
		System.err.println(data.getMsg());
	}
}
