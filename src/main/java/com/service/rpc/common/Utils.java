package com.service.rpc.common;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Utils {
	
	/**
	 * 获取method唯一标识（类名.方法名(参数类型...)），用于rpc服务器与客户端方法对应
	 * @param method
	 * @return
	 */
	public static String getMethodIdentify(Method method) {
		StringBuilder identifyBuilder = new StringBuilder();
		identifyBuilder.append(method.getDeclaringClass().getTypeName()).append('.')
				.append(method.getName()).append('(');
		Class<?>[] clses = method.getParameterTypes();
		for(Class<?> cls : clses) {
			identifyBuilder.append(cls.getTypeName()).append(',');
		}
		if(clses.length > 0) {
			identifyBuilder.deleteCharAt(identifyBuilder.length() - 1);
		}
		identifyBuilder.append(')');
		return identifyBuilder.toString();
	}
	
	public static boolean isObjectMethod(Method method) {
		return method.getDeclaringClass().equals(Object.class);
	}
	
	/**
	 * 检测参数是否为没有继承过其他接口的接口
	 * @param service
	 */
	public static <T> void validateServiceInterface(Class<T> service) {
		checkArgument(service == null, "参数不能为null");
		checkArgument(service.isInterface(), "参数必须为接口类型");
		// Prevent API interfaces from extending other interfaces. This not only avoids a bug in
		// Android (http://b.android.com/58753) but it forces composition of API
		// declarations which is the recommended pattern.
		checkArgument(service.getInterfaces().length == 0, "参数不能继承其他接口");
	}
	
	/**
	 * 检测参数是否合法，非法时抛出异常
	 * @param expression
	 * @param errorMessageTemplate
	 * @param errorMessageArgs
	 */
	public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
		if (!expression) {
			throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
		}
	}
	
	/**
	 * 将普通文本中的正则表达式的特殊字符转义
	 * @param keyword
	 * @return
	 */
	public static String escapeExprSpecialWord(String keyword) {
		if (StringUtils.isNotBlank(keyword)) {
			return keyword;
		}
		String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
		for (String key : fbsArr) {
			if (keyword.contains(key)) {
				keyword = keyword.replace(key, "\\" + key);
			}
		}
		return keyword;
	}
	
	/**
	 * 根据type获取对应的class类型
	 */
	public static Class<?> getRawType(Type type) {
		if (type == null)
			throw new NullPointerException("type == null");

		if (type instanceof Class<?>) {
			// Type is a normal class.
			return (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;

			// I'm not exactly sure why getRawType() returns Type instead of
			// Class. Neal isn't either but
			// suspects some pathological case related to nested classes exists.
			Type rawType = parameterizedType.getRawType();
			if (!(rawType instanceof Class))
				throw new IllegalArgumentException();
			return (Class<?>) rawType;
		}
		if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			return Array.newInstance(getRawType(componentType), 0).getClass();
		}
		if (type instanceof TypeVariable) {
			// We could use the variable's bounds, but that won't work if there
			// are multiple. Having a raw
			// type that's more general than necessary is okay.
			return Object.class;
		}
		if (type instanceof WildcardType) {
			return getRawType(((WildcardType) type).getUpperBounds()[0]);
		}

		throw new IllegalArgumentException("Expected a Class, ParameterizedType, or " + "GenericArrayType, but <" + type
				+ "> is of type " + type.getClass().getName());
	}
	
	/**
	 * 将text中的指定字符串替换成其他字符串
	 * @param text
	 * @param replace
	 * @return
	 */
	public static String replceText(String text, Map<String, String> replace) {
		if(StringUtils.isBlank(text)){
			return text;
		}
		try{
		    Pattern pattern = Pattern.compile(StringUtils.join(replace.keySet(), "|"));// TODO 此处最好对replace.keySet()做正则表达式特殊字符替换
	        Matcher matcher = pattern.matcher(text);
	        StringBuffer sb = new StringBuffer();
	        while (matcher.find()) {
	        	String matchStr = matcher.group();
	        	if(replace.containsKey(matcher.group())) {
	        		matcher.appendReplacement(sb, replace.get(matchStr));
//                replace.remove(matchStr);
	        	}
	        }
	        matcher.appendTail(sb);
	        return sb.toString();
		}catch(Exception e) {
			e.printStackTrace();
			return text;
		}
    }
}
