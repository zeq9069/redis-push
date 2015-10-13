package com.demo.rpush.bootstrap.config;

public class RServerConfig {

	private int port;

	public RServerConfig() {
	}

	public RServerConfig(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
