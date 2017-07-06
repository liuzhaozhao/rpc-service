package com.service.rpc.server.http.method;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.service.rpc.serialize.json.FastJson;
import com.service.rpc.serialize.json.IJson;

public class MethodParam {
	private IJson jsonConcert = new FastJson();// 暂时写死，后续可配置
	private Type type;// 方法参数对应的java类型，用于Json转对象使用 
//	private Class<?> cls;// 方法参数对应的Class类
	// 注解类型，目前支持BeanParam（针对post方式的非k-v格式的数据）、FormParam（针对Post方式的k-v请求参数）、PathParam（定义在url上的参数）、QueryParam（get请求参数）
	private ParamType paramType;// 该参数标识数据从哪里取
	private String name;// 方法参数对应的名称，k-v时有该值
	private String defaultValue;// 方法参数默认值
	
	public MethodParam(Type type, ParamType paramType, String name, String defaultValue) {
		this.type = type;
		this.paramType = paramType;
		this.name = name;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * 将字符串转换为参数对象类型
	 * @param str
	 * @return
	 */
	public Object getParamObj(String str) {
		if(StringUtils.isBlank(str)) {
			str = defaultValue;
		}
		if(StringUtils.isBlank(str)) {
			return null;
		}
		return jsonConcert.toBean(str, type);
	}

	public Type getType() {
		return type;
	}

	public ParamType getParamType() {
		return paramType;
	}

	public String getName() {
		return name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

}
