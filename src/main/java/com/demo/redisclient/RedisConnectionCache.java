package com.demo.redisclient;

import io.netty.channel.Channel;

import java.util.LinkedList;

/**
 * redis channel 缓存
 * @author kyrin
 *
 */
public class RedisConnectionCache {

	private static LinkedList<Channel> redisConnection = new LinkedList<Channel>();

	public static Channel getFirst() {
		return redisConnection.get(0);
	}

	public static void add(Channel value) {
		redisConnection.add(value);
	}

	public static void remove(Channel value) {
		redisConnection.remove(value);
	}

	public static boolean isEmpty() {
		return redisConnection.isEmpty();
	}

}
