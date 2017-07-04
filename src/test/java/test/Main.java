package test;

import com.service.rpc.server.Server;
import com.service.rpc.server.http.HttpMethod;
import com.service.rpc.server.http.MethodInfo;

import test.service.DataBean;
import test.service.TestService;

public class Main {
	public static void main(String[] args) throws Exception {
		Server.start(111, TestService.class);
		MethodInfo methodInfo = HttpMethod.getMethodInfo("/test_1/123", "post");
		Object data = null;
		if(methodInfo != null) {
			data = methodInfo.invoke(new Object[]{123, "arg", 456, new DataBean<Integer>("msg")});
		}
		System.err.println(">>>>>>>>>"+data);
	}
}
