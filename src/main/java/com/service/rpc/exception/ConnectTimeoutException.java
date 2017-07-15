package com.service.rpc.exception;

public class ConnectTimeoutException extends Exception {
	private static final long serialVersionUID = -6746773287706662712L;

	public ConnectTimeoutException() {
	}

	public ConnectTimeoutException(String arg0) {
		super(arg0);
	}

}
