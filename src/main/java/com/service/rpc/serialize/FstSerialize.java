package com.service.rpc.serialize;

import java.lang.reflect.Type;

import org.nustaq.serialization.FSTConfiguration;

public class FstSerialize extends ISerialize {
	private static FSTConfiguration configuration = 
//			FSTConfiguration.createJsonConfiguration(false, false);
			FSTConfiguration.createDefaultConfiguration();
//			FSTConfiguration.createStructConfiguration();
	static{
//		configuration.setForceSerializable(true);// 即使对象不实现Serializable接口也可序列化
	}
	
	@Override
	public byte[] toByte(Object obj) {
		return configuration.asByteArray(obj);
	}
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public <T> T toBean(byte[] bytes, List<MethodParam> params) {
//		return (T) configuration.asObject(bytes);
//	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T toBean(byte[] bytes, Type type) {
		return (T) configuration.asObject(bytes);
	}

	@Override
	public String toStr(Object obj) {
		return new String(toByte(obj));
	}

	@Override
	public <T> T toBean(String str, Type type) {
		return toBean(str.getBytes(), type);
	}

}
