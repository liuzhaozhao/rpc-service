package com.service.rpc.client.connect;

public interface Registry {
	/**
	 * 需要连接的服务
	 * @param servers	服务注册地址，格式如：ip:port
	 */
	public void connect(String... servers);
}
