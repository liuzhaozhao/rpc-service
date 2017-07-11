package com.service.rpc.server.http.returnType;

import javax.ws.rs.core.MediaType;

import com.service.rpc.common.json.FastJson;

public enum ReturnType {
	JSON(ReturnType.JSON_TYPE, new JsonReturn(new FastJson())),
	XML(ReturnType.XML_TYPE, new XmlReturn());
	public static final String JSON_TYPE = MediaType.APPLICATION_JSON+";"+MediaType.CHARSET_PARAMETER+"=utf-8";
	public static final String XML_TYPE = MediaType.APPLICATION_XML+";"+MediaType.CHARSET_PARAMETER+"=utf-8";
	
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
	
	public static final ReturnType get(String type) {
		for(ReturnType returnType : ReturnType.values()) {
			if(returnType.getType().equals(type)) {
				return returnType;
			}
		}
		return null;
	}
}
