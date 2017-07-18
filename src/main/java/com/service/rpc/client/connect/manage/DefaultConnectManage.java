package com.service.rpc.client.connect.manage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.service.rpc.client.connect.pool.Pool;
import com.service.rpc.common.Utils;

/**
 * 该连接方式为服务直连方式，如服务地址为：127.0.0.1:10001、127.0.0.1:10002，则该注册方式会定期检测服务的可用性，定时删除和添加服务
 * @author liuzhao
 *
 */
public class DefaultConnectManage implements ConnectManage {
	private static final Logger log = Logger.getLogger(DefaultConnectManage.class);
	private static final long CHECK_CONNECT_TIME_INTERVAL = 3000;
	private boolean checkConnect = true;// 是否定时检测连接是否可用，如果使用了nginx做代理，则不需要检测连接是否可用
	private Pool pool;
	private List<InetSocketAddress> serverAddress = new ArrayList<InetSocketAddress>();
	
	public DefaultConnectManage(Pool pool) {
		Utils.checkArgument(pool != null, "参数不能为null");
		this.pool = pool;
	}

	@Override
	public void connect(String... servers) {
		for(String server : servers) {
			String[] array = server.split(":");
            if (array.length != 2) {
            	continue;
            }
            String host = array[0];
            int port = Integer.parseInt(array[1]);
			serverAddress.add(new InetSocketAddress(host, port));
		}
		// 如果参数没有异常，则开启定时检测服务线程
		new Thread(){
			@Override
			public void run() {
				while(true) {
					checkConnect();
					if(!checkConnect) {
						return;
					}
					try {// 检测间隔时间
						Thread.sleep(CHECK_CONNECT_TIME_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private void checkConnect() {
		List<String> useableAddress = new ArrayList<String>();
		for(InetSocketAddress address : serverAddress) {
			try {
				Socket s = new Socket(address.getHostName(), address.getPort());
				s.close();
				useableAddress.add(address.getHostName() + ":" + address.getPort());
			} catch (IOException e) {
				log.warn("连接服务异常："+address, e);
			}
		}
		pool.updateConnect(useableAddress);
	}

}
