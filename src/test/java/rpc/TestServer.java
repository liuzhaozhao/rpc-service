package rpc;

import com.service.rpc.exception.RepeatedPathException;
import com.service.rpc.serialize.FastJsonSerialize;
import com.service.rpc.server.rpc.RpcServer;

import cn.jugame.http.server.Server;
import cn.jugame.http.server.auth.SimpleAuth;
import service.Service;

public class TestServer {
	@org.junit.Test
	public void start() throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
//		RpcServer.start(8809, TestService.class);
		//
		RpcServer.start(8808, Service.class).setSerialize(new FastJsonSerialize());
		
	}
	
	@org.junit.Test
	public void startHttpServer() throws InterruptedException {
		Server.start(9091, new SimpleAuth() {
			@Override
			public String getKey(String busCode) {
				return "key";
			}
		}, Service.class);
		System.err.println("服务器已启动");
		Thread.sleep(1000000);
	}
}
