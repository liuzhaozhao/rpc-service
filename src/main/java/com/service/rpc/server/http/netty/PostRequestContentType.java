package com.service.rpc.server.http.netty;

import org.apache.commons.lang3.StringUtils;

public enum PostRequestContentType {
	JSON("application/json"),
	FORM("application/x-www-form-urlencoded");
	
	private String contentType;
	private PostRequestContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public static PostRequestContentType get(String contentType) {
		if(StringUtils.isBlank(contentType)) {
			return null;
		}
		PostRequestContentType type = null;
		for(PostRequestContentType postRequestContentType : PostRequestContentType.values()) {
			if(postRequestContentType.getContentType().equalsIgnoreCase(contentType)) {
				type = postRequestContentType;
				break;
			}
		}
		return type;
	}
}
