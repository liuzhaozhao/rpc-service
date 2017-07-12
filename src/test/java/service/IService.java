package service;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IService {
	/**
	 * 测试无参、无返回值方法
	 */
//	@GET("test1")
//	public void test1();
	
	/**
	 * 测试复杂有参无返回值方法（泛型）
	 */
//	@GET("test2")
//	public void test2(@Query("arg1") String arg1, @Query("arg2") int arg2, @Query("arg3") double arg3, 
//			@Query("arg4") Bean arg4, @Query("arg5") List<DataBean<Bean>> arg5, @Query("arg6") Map<String, DataBean<Bean>> arg6);
	
	/**
	 * 无参、简单返回值
	 * @return
	 */
	@GET("test3")
	public String test3();
	
	/**
	 * 复杂有参、复杂返回值（泛型）
	 */
	@GET("test4")
	public List<DataBean<Bean>> test4(@Query("arg1") String arg1, @Query("arg2") int arg2, @Query("arg3") double arg3, 
			@Query("arg4") Bean arg4, @Query("arg5") List<DataBean<Bean>> arg5, @Query("arg6") Map<String, DataBean<Bean>> arg6);
	
}
