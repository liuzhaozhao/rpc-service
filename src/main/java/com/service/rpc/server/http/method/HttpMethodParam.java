package com.service.rpc.server.http.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.DefaultValue;

import org.apache.commons.lang3.StringUtils;

import com.service.rpc.server.common.MethodParam;
import com.service.rpc.server.http.HttpServer;

public class HttpMethodParam extends MethodParam {
	// 注解类型，目前支持BeanParam（针对post方式的非k-v格式的数据）、FormParam（针对Post方式的k-v请求参数）、PathParam（定义在url上的参数）、QueryParam（get请求参数）
	private ParamType paramType;// 该参数标识数据从哪里取
	private String name;// 方法参数对应的名称，k-v时有该值
	private String defaultValue;// 方法参数默认值
	
	public HttpMethodParam(Type type, Class<?> cls, Annotation[] annotations) {
		super(type, cls, annotations);
		for(Annotation annotation : annotations) {// 重复的配置以第一个生效
			if(defaultValue == null && annotation.annotationType() == DefaultValue.class) {// 没有设置默认值时才能设置
				defaultValue = ((DefaultValue)annotation).value();
				continue;
			}
			if(paramType != null) {// 如果已经赋值，则不需要再设值
				continue;
			}
			paramType = ParamType.get(annotation.annotationType());
			if(paramType != null) {
				name = ParamType.getVal(annotation);
			}
		}
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
		return HttpServer.getJson().toBean(str, type);
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
