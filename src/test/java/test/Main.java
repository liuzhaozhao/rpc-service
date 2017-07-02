package test;

import javax.ws.rs.POST;

import com.service.rpc.common.Utils;
import com.service.rpc.server.Server;

import test.service.TestService;

public class Main {
	public static void main(String[] args) {
//		Server.start(111, TestService.class);
		
		System.err.println(POST.class == POST.class);
		System.err.println(POST.class.equals(POST.class));
	}
}
