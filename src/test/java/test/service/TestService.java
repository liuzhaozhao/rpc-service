package test.service;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/testService")
//@Produces(MediaType.APPLICATION_JSON+";"+MediaType.CHARSET_PARAMETER+"=utf-8")// 设置返回值类型
public class TestService implements ITestService {
	
	public String testM(String arg) {
//		System.err.println("testM");
		return "return testM(" + arg  +")";
	}
	
	public String testM() {
//		System.err.println("testM");
		return "return testM()";
	}
	
	public String testM(String msg, int aa) {
//		System.err.println("testM");
		return "return testM(" + msg + ", " + aa + ")";
	}
	
	public String testM(int aa, String msg) {
//		System.err.println("testM");
//		return "return testM " + aa + "-" + msg;
		return "return testM(" + aa + ", " + msg + ")";
	}
	
	public String testM2(int arg1, String arg) {
//		System.err.println("arg1="+arg1+",arg="+arg);
		return "return testM2(" + arg1 + ", " + arg + ")";
	}
	
	@POST @Path("/test_1/{arg1}")
	public DataBean<List<String>> test_1(@PathParam("arg1") int arg1, @QueryParam("arg") @DefaultValue("1") String arg, 
			@FormParam("arg3") @DefaultValue("3") Integer arg3, @BeanParam DataBean<List<String>> dataBean) {
//		if(dataBean != null) {
//			System.err.println("dataBean.getMsg()="+dataBean.getMsg());
//		}
		DataBean<List<String>> returnData = new DataBean<List<String>>();
		returnData.setData(Arrays.asList(arg1+"", arg, arg3+"", dataBean.toString()));
		return returnData;
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
