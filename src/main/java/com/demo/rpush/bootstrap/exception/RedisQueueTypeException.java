package com.demo.rpush.bootstrap.exception;

public class RedisQueueTypeException extends RuntimeException {

	private static final long serialVersionUID = 5514974964256038897L;

	public RedisQueueTypeException() {
		super();
	}

	public RedisQueueTypeException(String message) {
		super(message);
	}

}
