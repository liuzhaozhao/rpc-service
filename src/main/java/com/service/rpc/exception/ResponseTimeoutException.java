package com.service.rpc.exception;

public class ResponseTimeoutException extends Exception {
	private static final long serialVersionUID = 4832364927956221645L;

	public ResponseTimeoutException() {
	}

	public ResponseTimeoutException(String arg0) {
		super(arg0);
	}

}
