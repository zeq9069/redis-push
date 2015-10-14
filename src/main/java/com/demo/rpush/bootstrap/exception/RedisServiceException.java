package com.demo.rpush.bootstrap.exception;

public class RedisServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7166892253912508254L;

	public RedisServiceException() {
		super();
	}

	public RedisServiceException(String message) {
		super(message);
	}

}
