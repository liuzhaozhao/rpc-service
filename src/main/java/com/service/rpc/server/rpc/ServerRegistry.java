package com.service.rpc.server.rpc;

import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;

import com.service.rpc.bean.RegistryInfo;
import com.service.rpc.common.Utils;

public class ServerRegistry {
	private static final Logger log = Logger.getLogger(ServerRegistry.class);
	
	public static final String ROOT_PATH = "/rpcService";
	public static final String SERVER_PATH = "/server";
	private ZkClient zkClient;
	
	public ServerRegistry(String address) {
		zkClient = new ZkClient(address, 3000, 5000);
	}
	
	/**
	 * 创建服务节点信息
	 * @param connectInfo
	 */
	public void registry(RegistryInfo connectInfo) {
		Utils.checkArgument(connectInfo != null, "注册信息不能为空");
		// 节点必须一层一层的创建
		if(!zkClient.exists(ROOT_PATH)) {
			zkClient.createPersistent(ROOT_PATH);
		}
		String serverRootPath = ROOT_PATH + SERVER_PATH;
		if(!zkClient.exists(serverRootPath)) {
			zkClient.createPersistent(serverRootPath);
		}
		String serverPath = serverRootPath + "/" + connectInfo.getIpPort();
		if(zkClient.exists(serverPath)) {// 存在则更新
			zkClient.writeData(serverPath, connectInfo);
		} else {// 不存在则新增
			zkClient.createEphemeral(serverPath, connectInfo);
		}
		log.info("已注册服务节点："+serverPath);
	}
}
