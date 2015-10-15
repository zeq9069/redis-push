package com.demo.rpush.bootstrap.exception;

/**
 * redis queue 中的数据出现超长元素
 * @author kyrin
 *
 */
public class RedisOneElementTooLongException extends RuntimeException {

	private static final long serialVersionUID = -6819043691513143751L;

	public RedisOneElementTooLongException() {
		super();
	}

	public RedisOneElementTooLongException(String message) {
		super(message);
	}

}
