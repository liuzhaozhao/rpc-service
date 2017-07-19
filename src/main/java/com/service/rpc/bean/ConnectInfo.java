package com.service.rpc.bean;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * 客户端连接信息
 * @author liuzhao
 *
 */
public class ConnectInfo implements Serializable {
	private static final long serialVersionUID = -3278109869307708038L;
	
	private String ip;// 客户端IP
	private String hostName;// 客户端机器名称
	private String path;// 客户端服务所在目录
	private Date startTime;// 服务开启时间
	
	public ConnectInfo() {
		File directory = new File("");
		path = directory.getAbsolutePath();
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress().toString(); //获取本机ip  
			hostName = addr.getHostName().toString(); //获取本机计算机名称  
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}   
		startTime = new Date();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public String getConnectIdentify() {
		return ip + "_" + hostName + "_" + (int)(Math.random()*1000);
	}
	
}
