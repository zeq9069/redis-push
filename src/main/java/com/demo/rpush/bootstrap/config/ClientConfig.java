package com.demo.rpush.bootstrap.config;

/**
 * 
 * @author kyrin
 * @date 2015年10月16日
 *
 */
public class ClientConfig {

	public static final String ROUTE_POLL = "poll";
	public static final String ROUTE_WEIGHT = "weight";

	//允许 client 连接的最大数量
	private int max = -1;
	//消息路由算法eg.poll,weight(轮询，权重)
	private String route = ROUTE_POLL;

	public ClientConfig() {
	}

	public ClientConfig(int max, String route) {
		this.max = max;
		this.route = route;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}
}
