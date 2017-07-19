package com.service.rpc.client.connect.manage;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.service.rpc.bean.ConnectInfo;
import com.service.rpc.client.ServiceFactory;
import com.service.rpc.common.Utils;
import com.service.rpc.server.rpc.ServerRegistry;

/**
 * 该连接方式为注册服务zookeeper地址，通过查找zookeeper节点，更新服务
 * @author liuzhao
 *
 */
public class ZookeeperConnectManage implements ConnectManage {
	private static final Logger log = Logger.getLogger(ZookeeperConnectManage.class);
	private static final String CLIENT_PATH = "/client";
	private ZkClient zkClient;
	
	@Override
	public void connect(String... servers) {
		Utils.checkArgument(servers.length != 0, "必须配置至少一个服务地址");
		zkClient = new ZkClient(StringUtils.join(servers, ","), 3000, 5000);
		ConnectInfo connectInfo = new ConnectInfo();
		
		if(!zkClient.exists(ServerRegistry.ROOT_PATH)) {
			zkClient.createPersistent(ServerRegistry.ROOT_PATH);
		}
		String clientRootPath = ServerRegistry.ROOT_PATH + CLIENT_PATH;
		if(!zkClient.exists(clientRootPath)) {
			zkClient.createPersistent(clientRootPath);
		}
		String clientPath = clientRootPath + "/" + connectInfo.getConnectIdentify();
		if(zkClient.exists(clientPath)) {// 存在则更新
			zkClient.writeData(clientPath, connectInfo);
		} else {// 不存在则新增
			zkClient.createEphemeral(clientPath, connectInfo);
		}
		log.info("已注册客户端节点："+clientPath);
		String serverRootPath = ServerRegistry.ROOT_PATH + ServerRegistry.SERVER_PATH;
		zkClient.subscribeChildChanges(serverRootPath, new ZKChildListener());
		
		List<String> serversPath = zkClient.getChildren(serverRootPath);
		ServiceFactory.getConnectPool().updateConnect(serversPath);
	}
	
	private static class ZKChildListener implements IZkChildListener {
		/**
		 * handleChildChange： 用来处理服务器端发送过来的通知 parentPath：对应的父节点的路径
		 * currentChilds：子节点的相对路径
		 */
		public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
			ServiceFactory.getConnectPool().updateConnect(currentChilds);
		}

	}
}
