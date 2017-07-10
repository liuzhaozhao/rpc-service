package old;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.service.rpc.common.Utils;
import com.service.rpc.server.http.MethodInvoke;
import com.service.rpc.server.tcp.MethodInfo;

public class TcpMethod {
//	private static Logger log = Logger.getLogger(TcpMethod.class);
	// 公有方法反射缓存
	private static Map<String, MethodInfo> methods = new HashMap<String, MethodInvoke>();
	
	public static MethodInvoke getMethodInvoke(String identify) {
		return methods.get(identify);
	}
	
	/**
	 * 添加方法反射缓存
	 * @param method
	 * @param methodInvoke
	 */
	public static synchronized void addMethodInvoke(Method method, MethodInvoke methodInvoke) {
		if(method == null) {
			return;
		}
		String identify = Utils.getMethodIdentify(method);
		if(methods.get(identify) != null) {// 以第一次设置的为准，后面的忽略（重复设置）
			return;
		}
		methods.put(identify, methodInvoke);
	}
}
