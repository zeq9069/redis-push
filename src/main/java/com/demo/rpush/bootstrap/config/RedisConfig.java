package com.demo.rpush.bootstrap.config;

/**
 * redis配置参数
 * @author kyrin
 *
 */
public class RedisConfig {

	private String host;
	private int port;
	private String queue;

	public RedisConfig() {
	}

	public RedisConfig(String host, int port, String queue) {
		this.host = host;
		this.port = port;
		this.queue = queue;

	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

}
