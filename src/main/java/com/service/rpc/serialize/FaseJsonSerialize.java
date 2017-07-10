package com.service.rpc.serialize;

import java.lang.reflect.Type;

import com.service.rpc.common.json.FastJson;
import com.service.rpc.common.json.IJson;

public class FaseJsonSerialize extends ISerialize {
	private IJson convert = new FastJson();

	@Override
	public byte[] toByte(Object obj) {
		return convert.toByte(obj);
	}

	@Override
	public String toStr(Object obj) {
		return convert.toStr(obj);
	}

	@Override
	public <T> T toBean(byte[] bytes, Type type) {
		return convert.toBean(bytes, type);
	}

	@Override
	public <T> T toBean(String str, Type type) {
		return convert.toBean(str, type);
	}

	@Override
	public boolean isJson() {
		return true;
	}
}
