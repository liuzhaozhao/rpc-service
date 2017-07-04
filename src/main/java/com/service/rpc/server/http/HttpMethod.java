package com.service.rpc.server.http;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.service.rpc.exception.RepeatedPathException;

public class HttpMethod {
	private static Logger log = Logger.getLogger(HttpMethod.class);
	// 非匹配式url映射缓存
	private static Map<String, MethodInfo> methods = new HashMap<String, MethodInfo>();
	// 动态匹配式url映射缓存(此处使用LinkedHashMap是为了调整匹配优先级，目前按照url长度匹配的，例如有两个动态url：/aa/{name}、/aa/{name1}-{name2}，此时/aa/111-222会匹配到第二个url
	private static Map<String, MethodInfo> dynamicUrlmethods = new LinkedHashMap<String, MethodInfo>();
	
	/**
	 * 根据Url获取需要反射调用的方法信息
	 * @param url
	 * @return
	 */
	public static MethodInfo getMethodInfo(String url, String httpType) {
		if(StringUtils.isBlank(url)) {
			return null;
		}
		MethodInfo methodInfo = methods.get(url);
		if(methodInfo != null && methodInfo.isSupportHttpType(httpType)) {// 先精确匹配地址
			return methodInfo;
		}
		try{
			for(String dynamicUrl : dynamicUrlmethods.keySet()) {// 匹配动态url
				Matcher m = Pattern.compile(dynamicUrl).matcher(url);
				if(!m.matches()){
					continue;
				}
				methodInfo = dynamicUrlmethods.get(dynamicUrl);
				if(methodInfo != null && methodInfo.isSupportHttpType(httpType)) {
					return dynamicUrlmethods.get(dynamicUrl);
				}
			}
		} catch (Exception e){
			log.warn("匹配动态路由异常", e);
		}
		return null;// 没有对应的方法
	}
	
	/**
	 * 添加方法
	 * @throws RepeatedPathException 
	 */
	public static synchronized void addMethodInfo(MethodInfo methodInfo) throws RepeatedPathException {
		if(methodInfo == null) {
			return;
		}
		if(methodInfo.isHasPathVar()) {
			String pathPattern = methodInfo.getPathPattern();
			if(dynamicUrlmethods.containsKey(pathPattern)) {
				throw new RepeatedPathException("path 配置重复");
			}
			dynamicUrlmethods.put(methodInfo.getPathPattern(), methodInfo);
		} else {
			if(methods.containsKey(methodInfo.getUrlPath())) {
				throw new RepeatedPathException("path 配置重复");
			}
			methods.put(methodInfo.getUrlPath(), methodInfo);
		}
	}
}
