package com.service.rpc.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务注册信息
 * @author liuzhao
 *
 */
public class RegistryInfo implements Serializable {
	private static final long serialVersionUID = -6433795982639378453L;

	private String ip;// 服务IP
	private int port;// 服务端口
	private Date startTime;// 服务开启时间
	
	public RegistryInfo(String ip, int port) {
		this.ip = ip;
		this.port = port;
		startTime = new Date();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public String getIpPort() {
		return ip+":"+port;
	}
	
}
