package com.service.rpc.server.http.returnType;

import javax.ws.rs.core.MediaType;

import com.service.rpc.serialize.json.FastJson;

public enum ReturnType {
	JSON(MediaType.APPLICATION_JSON+";"+MediaType.CHARSET_PARAMETER+"=utf-8", new JsonReturn(new FastJson())),
	XML(MediaType.APPLICATION_XML+";"+MediaType.CHARSET_PARAMETER+"=utf-8", new XmlReturn());
	
	private String type;
	private IReturn iReturn;
	
	private ReturnType(String type, IReturn iReturn) {
		this.type = type;
		this.iReturn = iReturn;
	}
	
	public String getType() {
		return type;
	}
	
	public byte[] getReturnData(Object obj) {
		return iReturn.toByte(obj);
	}
}
