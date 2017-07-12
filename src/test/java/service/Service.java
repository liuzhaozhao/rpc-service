package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.service.rpc.common.JsonUtil;

import cn.jugame.http.server.jersey.MediaTypeCharset;

@Singleton
@Path("/")
@Produces(MediaTypeCharset.APPLICATION_JSON_UTF8)
public class Service implements IService {

//	@GET @Path("test1")
//	@Override
//	public void test1() {
//		System.err.println("test1()");
//	}

//	@GET @Path("test2")
//	@Override
//	public void test2(@QueryParam("arg1") String arg1, @QueryParam("arg2") int arg2, @QueryParam("arg3") double arg3, 
//			@QueryParam("arg4") Bean arg4, @QueryParam("arg5") List<DataBean<Bean>> arg5, @QueryParam("arg6") Map<String, DataBean<Bean>> arg6) {
//		System.err.println("test2("+arg1+", "+arg2+", "+arg3+", "+JsonUtil.toJson(arg4)+", "+JsonUtil.toJson(arg5)+", "+JsonUtil.toJson(arg6)+")");
//		System.err.println(arg5.get(0).getData().getDataBeans().get(0).getData().getStr());
//		System.err.println(arg6.get("map2").getMsg());
//	}


	@GET @Path("test3")
	@Override
	public String test3() {
		System.err.println("test3()");
//		if(1 == 1) {
//			throw new RuntimeException("测试异常");
//		}
//		System.err.println(1/0);
		return "test3中文";
	}

	@GET @Path("test4")
	@Override
	public List<DataBean<Bean>> test4(@QueryParam("arg1") String arg1, @QueryParam("arg2") int arg2, @QueryParam("arg3") double arg3, 
			@QueryParam("arg4") Bean arg4, @QueryParam("arg5") List<DataBean<Bean>> arg5, @QueryParam("arg6") Map<String, DataBean<Bean>> arg6) {
		System.err.println("test2("+arg1+", "+arg2+", "+arg3+", "+JsonUtil.toJson(arg4)+", "+JsonUtil.toJson(arg5)+", "+JsonUtil.toJson(arg6)+")");
		System.err.println(arg5.get(0).getData().getDataBeans().get(0).getData().getStr());
		System.err.println(arg6.get("map2").getMsg());
		
		List<DataBean<Bean>> list = new ArrayList<DataBean<Bean>>();
		DataBean<Bean> d1 = new DataBean<>();
		d1.setData(new Bean());
		list.add(d1);
		list.add(new DataBean<Bean>("测试"));
		return list;
	}
	
}
