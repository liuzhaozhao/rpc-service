package com.service.rpc.client;

import java.net.InetSocketAddress;
import java.util.List;

import com.service.rpc.transport.RpcRequest;

public interface ClientConnect {
	
	/**
	 * 更新连接池中的连接（外部触发，如zookeeper）
	 * @param ipPort	包含所有可用的连接，该方法的实现为添加List中存在但连接池中不存在的连接，去除List中不存在的连接
	 */
	public void updateConnect(List<String> ipPort);
	
	/**
	 * 如果连接存在，则重新连接，如果不存在则连接（外部触发，如zookeeper）
	 * @param remotePeer
	 */
	public void connect(InetSocketAddress remotePeer);
	
	/**
	 * 删除连接（外部触发，如zookeeper）
	 * @param remotePeer
	 */
	public void remove(InetSocketAddress remotePeer);
	
	/**
	 * 发送请求
	 * @return
	 */
	public RpcFuture send(RpcRequest request);
	
	/**
	 * 关闭服务
	 */
	public void stop();
}
