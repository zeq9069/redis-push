package com.demo.rpush;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接my server的client缓存
 * @author kyrin 
 *
 */
public class ClientConnectionCache {
	private static ConcurrentHashMap<String, Channel> clientConnect = new ConcurrentHashMap<String, Channel>();

	public static Channel get() {
		return clientConnect.elements().nextElement();
	}

	public static Channel get(String key) {
		return clientConnect.get(key);
	}

	public static void put(String key, Channel value) {
		clientConnect.put(key, value);
	}

	public static void remove(Channel value) {
		clientConnect.remove(value);
	}

	public static boolean isEmpty() {
		return clientConnect.isEmpty();
	}

}
