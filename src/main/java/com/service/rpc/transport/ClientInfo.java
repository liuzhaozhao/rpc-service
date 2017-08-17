package com.service.rpc.transport;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class ClientInfo implements Serializable {
	private static final long serialVersionUID = -6017236297283900258L;
	private String path;// 客户端部署路径
	private String name;// 机器名称
	private String ip;// 客户端所在机器IP
	private String version;// 客户端使用的rpc版本，用于服务端特殊处理，注意与pom版本保持一致
	private Date requestDate = new Date();// 当前请求数据的时间
	
	public ClientInfo() {}
	
	public ClientInfo(String version) {
		this.version = version;
		path = new File("").getAbsolutePath();
		path = System.getProperty("user.dir");
		try {
			InetAddress address = InetAddress.getLocalHost();
			name = address.getHostName();
			ip = address.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	
}
