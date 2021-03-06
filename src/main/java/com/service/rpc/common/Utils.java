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
		checkArgument(service != null, "参数不能为null");
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
	 * 非法状态检测
	 * @param expression
	 * @param errorMessageTemplate
	 * @param errorMessageArgs
	 */
	public static void checkStatus(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
		if (!expression) {
			throw new IllegalStateException(String.format(errorMessageTemplate, errorMessageArgs));
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
	
	/**
	 * 获取不带参数的url
	 * @param uri
	 * @return
	 */
	public static String getUrl(String uri) {
		if(StringUtils.isBlank(uri)) {
			return uri;
		}
		int endIndex = uri.indexOf("?");
		if(endIndex == -1) {
			return uri;
		} else {
			return uri.substring(0, endIndex);
		}
	}
	
	/**
	 * 是否为基本数据类型，如：Integer、Double.... 都为基本数据类型
	 * @param cls
	 * @param additionClass	附加检测类型，即如果类型为给定的类型，也算作基本数据类型（如String）
	 * @return
	 */
	public static boolean isPrimitive(Class<?> cls, Class<?>... additionClass){
		for(Class<?> thisCls : additionClass){
			if(thisCls == cls){
				return true;
			}
		}
		boolean isPrimitive = cls.isPrimitive();
		if(!isPrimitive){
			try {
				isPrimitive = ((Class<?>) cls.getField("TYPE").get(null)).isPrimitive();
			} catch (Exception e) {
			}
		}
		return isPrimitive;
	}
	
	public static Object convertBaseType(Class<?> cls, String str) {
        if (Integer.class.isAssignableFrom(cls) || int.class.isAssignableFrom(cls)) {
            return Integer.parseInt(str);
        } else if (Long.class.isAssignableFrom(cls) || long.class.isAssignableFrom(cls)) {
            return Long.parseLong(str);
        } else if (Short.class.isAssignableFrom(cls) || short.class.isAssignableFrom(cls)) {
            return Short.parseShort(str);
        } else if (Double.class.isAssignableFrom(cls) || double.class.isAssignableFrom(cls)) {
            return Double.parseDouble(str);
        } else if (Float.class.isAssignableFrom(cls) || float.class.isAssignableFrom(cls)) {
            return Float.parseFloat(str);
        } else if (Byte.class.isAssignableFrom(cls) || byte.class.isAssignableFrom(cls)) {
        	return Byte.parseByte(str);
        } else if (Boolean.class.isAssignableFrom(cls) || boolean.class.isAssignableFrom(cls)) {
        	return Boolean.parseBoolean(str);
        } else if (Character.class.isAssignableFrom(cls) || char.class.isAssignableFrom(cls)) {
        	return str.charAt(0);
        }
        return str;
	}
	
	public static void main(String[] args) {
//		System.err.println(getUrl("http://localhost:8080/test_1/123/?arg=“arg2”&arg3=arg3"));
//		System.err.println(isPrimitive(Integer.class));
//		
//		System.err.println(Integer.class.isPrimitive());
//		
//		System.err.println(int.class.isPrimitive());
		
		System.err.println(convertBaseType(boolean.class, "true") instanceof Boolean);
		
	}
}
