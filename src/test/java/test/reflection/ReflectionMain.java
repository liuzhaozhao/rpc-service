package test.reflection;

import com.esotericsoftware.reflectasm.MethodAccess;

import test.service.TestService;

/**
 * 测试不同反射的性能
 * 结果：
 * 	asm使用方法名调用与java直接缓存反射调用时间差不多，是直接调用的几千倍
 * 	asm使用index的方式是直接调用的时间20倍，直接调用不会因为调用次数多有明显的时间增加，asm使用index的方式也是
 * 
 * 注意点：此处把times修改未long类型会发现执行时间长很多
 * @author liuzhao
 *
 */
public class ReflectionMain {
	public static void main(String[] args) throws Exception {
		TestService service = new TestService();
//		int times = 100000000;
		int times = 2147483647;
//		long times = 2147483647;
		long start = System.currentTimeMillis();
		
		// asm反射调用时间测试
		MethodAccess access = MethodAccess.get(TestService.class);
//		for(int i = 0; i < times; i++) {
////			System.err.println((String)access.invoke(service, "testM"));
//			access.invoke(service, "testM");
////			System.err.println((String)access.invoke(service, "testM2", 1, "arg"));
//			access.invoke(service, "testM2", 1, "arg");
//		}
//		System.err.println(System.currentTimeMillis() - start);
		
		// asm反射调用时间测试（使用方法位置调用）
//		int index_11 = access.getIndex("testM", 0);
//		int index_12 = access.getIndex("testM", String.class);// 测试重载方法
//		int index_13 = access.getIndex("testM", String.class, int.class);// 测试重载方法
		int index_14 = access.getIndex("testM", int.class, String.class);// 测试重载方法
		int index_2 = access.getIndex("testM2");
		start = System.currentTimeMillis();
		String arg = "arg";
		for(int i = 0; i < times; i++) {
//			System.err.println(access.invoke(service, index_11));
//			access.invoke(service, index_11);
//			System.err.println(access.invoke(service, index_12, "arg"));
//			System.err.println(access.invoke(service, index_13, "arg", 123));
			
			access.invoke(service, index_14, new Object[]{1, arg});
//			System.err.println(access.invoke(service, index_14, 123, "arg"));
			
			access.invoke(service, index_2, new Object[]{1, arg});
//			System.err.println(access.invoke(service, index_2, new Object[]{1, "arg11"}));
		}
		System.err.println(System.currentTimeMillis() - start);
		
		// asm反射调用时间测试（使用参数类型调用）
//		Class<?>[] argsType = new Class[]{int.class, String.class};
//		start = System.currentTimeMillis();
//		for(int i = 0; i < times; i++) {
//			access.invoke(service, "testM");
//			access.invoke(service, "testM2", argsType, 1, "arg");
//		}
//		System.err.println(System.currentTimeMillis() - start);
		
		// java直接调用
//		start = System.currentTimeMillis();
//		for(int i = 0; i < times; i++) {
////			System.err.println(service.testM());
//			service.testM();
////			System.err.println(service.testM2(1, "arg"));
//			service.testM2(1, "arg");
//		}
//		System.err.println(System.currentTimeMillis() - start);
		
		// java反射调用
//		Class<?> c = Class.forName("test.reflection.TestService");
//	    Class<?>[] argsType = new Class[]{int.class, String.class};
//	    Method testM = c.getMethod("testM");
//	    Method testM2 = c.getMethod("testM2", argsType);
//	    start = System.currentTimeMillis();
//		for(int i = 0; i < times; i++) {
//			testM.invoke(service);
//			testM2.invoke(service, 1, "arg");
//		}
//		System.err.println(System.currentTimeMillis() - start);
	}
}
