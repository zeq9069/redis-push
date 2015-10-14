package com.demo.rpush.bootstrap.config;

import java.util.Properties;

import com.demo.rpush.bootstrap.config.loader.ConfigLoader;
import com.demo.rpush.utils.Util;

public class RPushPropertiesConfig {

	private final RedisConfig redisConfig;
	private final RServerConfig rServerConfig;
	private ConfigLoader loader;

	public RPushPropertiesConfig(ConfigLoader loader) {
		this.loader = loader;
		this.redisConfig = loadRedisConfig();
		this.rServerConfig = loadRServerConfig();
	}

	private RedisConfig loadRedisConfig() {
		Properties property = loader.getProperties();
		RedisConfig redisConfig = new RedisConfig();

		String host = property.getProperty(ConfigProperties.RPUSH_REDIS_HOST);
		if (Util.hasText(host)) {
			redisConfig.setHost(host);
		} else {
			throw new IllegalArgumentException(String.format("缺少参数:%s", ConfigProperties.RPUSH_REDIS_HOST));
		}
		String port = property.getProperty(ConfigProperties.RPUSH_REDIS_PORT);
		if (Util.hasText(port)) {
			redisConfig.setPort(Integer.parseInt(port));
		} else {
			throw new IllegalArgumentException(String.format("缺少参数:%s", ConfigProperties.RPUSH_REDIS_PORT));
		}
		String queue = property.getProperty(ConfigProperties.RPUSH_REDIS_QUEUE);
		if (Util.hasText(queue)) {
			redisConfig.setQueue(queue);
		} else {
			throw new IllegalArgumentException(String.format("缺少参数:%s", ConfigProperties.RPUSH_REDIS_QUEUE));
		}
		String queueType = property.getProperty(ConfigProperties.RPUSH_REDIS_QUEUE_TYPE);
		if (Util.hasText(queueType)) {
			redisConfig.setQueueType(queueType);
		} else {
			throw new IllegalArgumentException(String.format("缺少参数:%s", ConfigProperties.RPUSH_REDIS_QUEUE_TYPE));
		}
		return redisConfig;
	}

	private RServerConfig loadRServerConfig() {
		RServerConfig rServerConfig = new RServerConfig();
		Properties property = loader.getProperties();
		String port = property.getProperty(ConfigProperties.RPUSH_SERVER_PORT);
		if (Util.hasText(port)) {
			rServerConfig.setPort(Integer.parseInt(port));
		} else {
			throw new IllegalArgumentException(String.format("缺少参数:%s", ConfigProperties.RPUSH_SERVER_PORT));
		}
		return rServerConfig;
	}

	public RedisConfig getRedisConfig() {
		return redisConfig;
	}

	public RServerConfig getrServerConfig() {
		return rServerConfig;
	}

}
