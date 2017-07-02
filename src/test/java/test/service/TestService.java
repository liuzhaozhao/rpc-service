package test.service;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/testService")
public class TestService implements ITestService {
	
	public String testM(String arg) {
//		System.err.println("testM");
		return "return testM " + arg;
	}
	
	public String testM() {
//		System.err.println("testM");
		return "return testM";
	}
	
	public String testM(String msg, int aa) {
//		System.err.println("testM");
//		return "return testM " + msg + "-" + aa;
		return "return testM";
	}
	
	public String testM(int aa, String msg) {
//		System.err.println("testM");
//		return "return testM " + aa + "-" + msg;
		return "return testM";
	}
	
	public String testM2(int arg1, String arg) {
//		System.err.println("arg1="+arg1+",arg="+arg);
		return "return testM2";
	}
	
	@POST @Path("/test_1/{arg1}")
	public String test_1(@PathParam("arg1") @DefaultValue("1") int arg1, @QueryParam("arg") String arg, 
			@FormParam("arg3") Integer arg3, @FormParam("dataBean") DataBean<Integer> dataBean) {
//		System.err.println("testM");
		return "return testM";
	}
	
	String test_2() {
//		System.err.println("testM");
		return "return testM";
	}
	
	private String test_3() {
//		System.err.println("testM");
		return "return testM";
	}
}
