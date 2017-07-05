package com.service.rpc.serialize.json;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.service.rpc.common.DateUtil;

public class FastJson implements IJson {
	
	public String toStr(Object obj) {
		// 优先使用对象级的属性 datePattern, 然后才是全局性的 defaultDatePattern
//		if(obj instanceof String) {// 去除字符串前后的双引号
//			return obj.toString();
//		}
		return JSON.toJSONStringWithDateFormat(obj, DateUtil.DATE_FORMATE, SerializerFeature.WriteDateUseDateFormat);	// return JSON.toJSONString(object, SerializerFeature.WriteDateUseDateFormat);
	}
	
	public <T> T toBean(String jsonString, Type type) {
		return JSON.parseObject(jsonString, type);
	}

	@Override
	public byte[] toByte(Object obj) {
//		if(obj instanceof String) {// 去除字符串前后的双引号
//			return ((String)obj).getBytes();
//		}
		return JSON.toJSONBytes(obj);
	}
}
