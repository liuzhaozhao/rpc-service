package com.service.rpc.serialize;

import java.lang.reflect.Type;

public abstract class ISerialize {
	
	/**
	 * 对象转换为byte数组
	 * @param obj
	 * @return
	 */
	public abstract byte[] toByte(Object obj);
	
	/**
	 * 对象转字符串
	 * @param obj
	 * @return
	 */
	public abstract String toStr(Object obj);
	
	/**
	 * 对象转换
	 * @param bytes
	 * @param cls
	 * @return
	 */
	public abstract <T> T toBean(byte[] bytes, Type type);
	
	/**
	 * 字符串转对象
	 * @param str
	 * @param type
	 * @return
	 */
	public abstract <T> T toBean(String str, Type type);
	
	/**
	 * byte数组转对象
	 * @param data	所有参数组成的数组字节
	 * @param params
	 * @return
	 */
//	public <T> T toBean(byte[] bytes, List<MethodParam> params);
	
	public boolean isJson() {
		return false;
	}
}
