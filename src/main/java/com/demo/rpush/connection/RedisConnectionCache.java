package com.demo.rpush.connection;

import io.netty.channel.Channel;

import java.util.LinkedList;

/**
 * redis channel 缓存
 * @author kyrin
 * @date 2015年10月16日
 */
public class RedisConnectionCache {

	private static LinkedList<Channel> redisConnection = new LinkedList<Channel>();

	public static Channel getFirst() {
		Channel ch=redisConnection.get(0);
		if(ch==null || !ch.isActive()){
			redisConnection.remove();
		}
		return ch;
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
