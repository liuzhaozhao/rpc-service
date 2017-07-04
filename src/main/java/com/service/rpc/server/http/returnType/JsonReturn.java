package com.service.rpc.server.http.returnType;

import com.service.rpc.serialize.json.IJson;

public class JsonReturn implements IReturn {
	private IJson jsonConvert;
	
	public JsonReturn(IJson jsonConvert) {
		this.jsonConvert = jsonConvert;
	}

	@Override
	public byte[] toByte(Object obj) {
		return jsonConvert.toByte(obj);
	}

}
