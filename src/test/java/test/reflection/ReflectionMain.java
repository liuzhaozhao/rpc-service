package test.reflection;

import java.lang.reflect.Method;

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
 * 忽略上面的测试结果，经再次测试发现上面的测试结果是无参方法的测试结果
 * 
 * 如需测试其他的自己运行
 *
 */
public class ReflectionMain {
//	private static int times = 1;
	private static int times = 100000000;
//	private static int times = 2147483647;
//	private static long times = 2147483647;
	
	private static TestService service = new TestService();
	private static MethodAccess access = MethodAccess.get(TestService.class);
	private static Object[] objs = new Object[]{1, "arg"};
	private static Class<?>[] argsType = new Class[]{int.class, String.class};
	
	public static void main(String[] args) throws Exception {
//		asm1();
//		asm2();
//		direct();
		javaReflect();
	}
	
	private static void asm1() {
		// asm反射调用时间测试（使用方法位置调用）
		int index_11 = access.getIndex("testM", 0);
//		int index_12 = access.getIndex("testM", String.class);// 测试重载方法
//		int index_13 = access.getIndex("testM", String.class, int.class);// 测试重载方法
		int index_14 = access.getIndex("testM", int.class, String.class);// 测试重载方法
		long start = System.currentTimeMillis();
		for(int i = 0; i < times; i++) {
//			System.err.println(access.invoke(service, index_11));
//			System.err.println(access.invoke(service, index_14, objs));
					
//			access.invoke(service, index_11);
			access.invoke(service, index_14, objs);
		}
		System.err.println(System.currentTimeMillis() - start);
	}
	
	private static void asm2() {
		// asm反射调用时间测试（使用参数类型调用）
		long start = System.currentTimeMillis();
		for(int i = 0; i < times; i++) {
//			System.err.println(access.invoke(service, "testM", argsType, objs));
			access.invoke(service, "testM", argsType, objs);
		}
		System.err.println(System.currentTimeMillis() - start);
	}
	
	private static void direct() {
		// java直接调用
		long start = System.currentTimeMillis();
		for(int i = 0; i < times; i++) {
//			System.err.println(service.testM(1, "arg"));
			service.testM(1, "arg");
		}
		System.err.println(System.currentTimeMillis() - start);
	}
	
	private static void javaReflect() throws Exception {
		// java反射调用
		Class<?> c = Class.forName(TestService.class.getName());
	    Method testM = c.getMethod("testM", argsType);
	    long start = System.currentTimeMillis();
		for(int i = 0; i < times; i++) {
//			System.err.println(testM.invoke(service, objs));
			testM.invoke(service, objs);
		}
		System.err.println(System.currentTimeMillis() - start);
	}
	
}
