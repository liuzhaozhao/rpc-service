package com.service.rpc.client.connect.manage;

import org.apache.log4j.Logger;

import com.service.rpc.client.connect.pool.Pool;
import com.service.rpc.common.Utils;

/**
 * 该连接方式为注册服务zookeeper地址，通过查找zookeeper节点，更新服务
 * @author liuzhao
 *
 */
public class ZookeeperConnectManage implements ConnectManage {
	private static final Logger log = Logger.getLogger(ZookeeperConnectManage.class);
	private Pool pool;
	
	public ZookeeperConnectManage(Pool pool) {
		Utils.checkArgument(pool != null, "参数不能为null");
		this.pool = pool;
	}

	@Override
	public void connect(String... servers) {
		
	}
	
}
