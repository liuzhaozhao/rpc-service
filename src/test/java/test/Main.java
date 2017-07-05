package test;

import com.service.rpc.server.Server;

import test.service.TestService;

public class Main {
	public static void main(String[] args) throws Exception {
		Server.start(8080, TestService.class);
//		MethodInfo methodInfo = HttpMethod.getMethodInfo("/test_1/123", "post");
//		Object data = null;
//		if(methodInfo != null) {
//			data = methodInfo.invoke(new Object[]{123, "arg", 456, new DataBean<Integer>("msg")});
//		}
//		System.err.println(">>>>>>>>>"+data);
//		Object obj = "12qq3";
//		Object obj = new DataBean<Integer>("error msg");
//		IJson json = new FastJson();
//		System.err.println(json.toStr(obj));
		
//		System.err.println(new String(json.toByte(obj)));
		
	}
}
