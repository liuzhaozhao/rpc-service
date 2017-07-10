package com.service.rpc.common;

import com.service.rpc.common.json.FastJson;
import com.service.rpc.common.json.IJson;

public class JsonUtil {
	private static IJson json = new FastJson();
	
	public static String toJson(Object obj) {
		return json.toStr(obj);
	}
}
