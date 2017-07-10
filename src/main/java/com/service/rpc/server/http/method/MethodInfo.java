package com.service.rpc.server.http.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;

import org.apache.commons.lang3.StringUtils;

import com.service.rpc.common.Utils;
import com.service.rpc.exception.NoPathException;
import com.service.rpc.server.http.MethodInvoke;
import com.service.rpc.server.http.returnType.ReturnType;

public class MethodInfo {
	private MethodInvoke invoke;// 反射调用类
	private Method method;
	private String urlPath;// http服务的URL地址
	private boolean hasUrlPathVar;// URL地址是否包含变量
	private String pathPattern;// 正则匹配的url（hasPathVar=true时有值）
	private Pattern pattern;// url规则匹配
	private String[] pathVarNames;// URL地址变量名字数组（hasPathVar=true时有值） 
	
	private List<String> supportHttpType = new ArrayList<String>();// http支持的请求类型，具体类型见HttpType.java
	private MethodParam[] methodParams;// 方法对应的请求参数数组 
	// 暂时不实现返回值类型配置，统一使用json
	private ReturnType returnType = ReturnType.JSON;// 方法返回数据类型，可支持json、xml，具体参考：javax.ws.rs.core.MediaType类，根据该值生成返回值
	
	public MethodInfo(MethodInvoke invoke, Method method) throws NoPathException {
		this.invoke = invoke;
		this.method = method;
		setUrlPath();
		checkUrlPathVar();// 必须放到setUrlPath后
		setSupportHttpType();
		setMethodParam();
	}
	
	/**
	 * 获取方法配置的http url
	 * urlPath一定是以/开头的
	 * @throws NoPathException
	 */
	private void setUrlPath() throws NoPathException {
		Path path = method.getAnnotation(Path.class);
		if(path == null) {// 方法无Path注解，不开启该方法的http服务
			throw new NoPathException(Utils.getMethodIdentify(method)+"方法未配置Path注解，不开启http服务");
		}
		String urlPath = path.value().trim();
		if(urlPath.startsWith("/")) {// 不需要在前面追加classUrlPath
			this.urlPath = urlPath;
			return;
		}
		String classUrlPath = "/";
		Path classPath = method.getDeclaringClass().getAnnotation(Path.class);
		if(classPath != null && !"".equals(classPath.value().trim())) {
			classUrlPath = classPath.value().trim();
			if(!classUrlPath.startsWith("/")) {
				classUrlPath = "/" + classUrlPath;
			}
		}
		if(!classUrlPath.endsWith("/")) {
			classUrlPath += "/";
			
		}
		this.urlPath = classUrlPath + urlPath;
	}
	
	/**
	 * 检测url是否包含变量，并初始化全局参数
	 */
	private void checkUrlPathVar() {
		Matcher m = Pattern.compile("(\\{[^\\/\\}]+})").matcher(urlPath);
		List<String> varNames = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			String group = m.group();
			varNames.add(group.substring(1, group.length() - 1));
			m.appendReplacement(sb, "([^\\/]+)");
			hasUrlPathVar = true;
		}
		m.appendTail(sb);
		pathPattern = sb.toString();
		pattern = Pattern.compile(pathPattern);
		pathVarNames = varNames.toArray(new String[]{});
	}
	
	/**
	 * 设置方法支持的http请求方式
	 */
	private void setSupportHttpType() {
		for(Annotation annotation : method.getAnnotations()) {
			HttpType httpType = HttpType.get(annotation.annotationType());
			if(httpType != null){
				supportHttpType.add(httpType.getType());
			}
		}
		if(supportHttpType.size() == 0) {// 如果没有配置支持的http类型，则支持所有类型
			supportHttpType.addAll(HttpType.getTypes());
		}
	}
	
	/**
	 * 设置方法参数，如注解、参数类型
	 */
	private void setMethodParam() {
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		List<MethodParam> paramsList = new ArrayList<MethodParam>();
		for(int i = 0; i < genericParameterTypes.length; i++) {
			Type type = genericParameterTypes[i];
//			Annotation[] annotations = parameterAnnotations[i];
			String defaultValue = null;
			ParamType paramType = null;
			String name = null;
			for(Annotation annotation : parameterAnnotations[i]) {// 重复的配置以第一个生效
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
			paramsList.add(new MethodParam(type, paramType, name, defaultValue));
		}
		methodParams = paramsList.toArray(new MethodParam[]{});
	}
	
	/**
	 * 根据url获取匹配的url变量信息
	 * @param url
	 * @return
	 */
	public Map<String, String> getPathParams(String url) {
		Map<String, String> pathParams = new HashMap<String, String>();
		if(!hasUrlPathVar) {
			return pathParams;
		}
		Matcher mUrl = pattern.matcher(url);
		if (!mUrl.matches() || mUrl.groupCount() != pathVarNames.length) {// 个数必须匹配才能获取url变量，理论上不会出现不匹配的
			return pathParams;
		}
		for (int i = 0; i < mUrl.groupCount(); i++) {
			pathParams.put(pathVarNames[i], mUrl.group(i + 1));
		}
		return pathParams;
	}
	
	/**
	 * 判断服务是否支持该请求url
	 * 
	 * 运行时调用
	 * @return
	 */
	public boolean isSupportHttpType(String httpType) {
		if(StringUtils.isBlank(httpType)) {
			return false;
		}
		return supportHttpType.contains(httpType.toLowerCase());
	}
	
	public Object invoke(Object[] params) {
		return invoke.invoke(params);
	}
	
	public boolean isHasPathVar() {
		return hasUrlPathVar;
	}
	
	public String getPathPattern() {
		return pathPattern;
	}
	
	public String getUrlPath() {
		return urlPath;
	}
	
	public ReturnType getReturnType() {
		return returnType;
	}
	
	public MethodParam[] getMethodParams() {
		return methodParams;
	}
	
	public String[] getPathVarNames() {
		return pathVarNames;
	}
	
	public Method getMethod() {
		return method;
	}
	
}
