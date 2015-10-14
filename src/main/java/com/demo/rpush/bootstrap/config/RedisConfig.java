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
	private String queueType;

	public RedisConfig() {
	}

	public RedisConfig(String host, int port, String queue, String queueType) {
		this.host = host;
		this.port = port;
		this.queue = queue;
		this.queueType = queueType;
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

	public String getQueueType() {
		return queueType;
	}

	public void setQueueType(String queueType) {
		this.queueType = queueType;
	}

	public String getCommand() {
		switch (queueType) {
		case "list":
			return String.format("*2\r\n$4\r\nLPOP\r\n$%d\r\n%s\r\n", queue.length(), queue);
		case "set":
			return String.format("*2\r\n$4\r\nSPOP\r\n$%d\r\n%s\r\n", queue.length(), queue);
		default:
			throw new IllegalArgumentException(String.format("queueType 不是合法队列类型：list or set "));
		}
	}
}
