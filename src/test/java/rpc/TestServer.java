package rpc;

import com.service.rpc.exception.RepeatedPathException;
import com.service.rpc.serialize.FastJsonSerialize;
import com.service.rpc.server.tcp.RpcServer;

import test.service.TestService;

public class TestServer {
	@org.junit.Test
	public void start() throws InstantiationException, IllegalAccessException, RepeatedPathException, InterruptedException {
		RpcServer.server.start(8809, TestService.class);
	}
}
