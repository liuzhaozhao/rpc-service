package com.service.rpc.exception;

public class ResponseTimeoutException extends Exception {
	private static final long serialVersionUID = 8651215630787820090L;
	
	public ResponseTimeoutException() {
	}

	public ResponseTimeoutException(String arg0) {
		super(arg0);
	}

}
