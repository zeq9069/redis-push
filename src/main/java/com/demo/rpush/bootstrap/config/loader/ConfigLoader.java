package com.demo.rpush.bootstrap.config.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author kyrin
 *
 */
public class ConfigLoader {
	
	private static final Logger logger=LoggerFactory.getLogger(ConfigLoader.class);
	private Properties properties = new Properties();
	private static final String DEFAULT_PROPERTIES = "/redis-push.properties";
	private String config;

	public ConfigLoader() {
		this(DEFAULT_PROPERTIES);
	}

	public ConfigLoader(String conf) {

		this.config = conf;

		InputStream file = null;
		try {
			if (config == null || "".equals(config.trim())) {
				throw new FileNotFoundException("缺少配置文件");
			}
			if (!config.endsWith(".properties")) {
				throw new IllegalArgumentException("配置文件必须是properties类型");
			}
			file =ConfigLoader.class.getResourceAsStream(config);
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(0);
		}

		try {
			properties.load(file);
		} catch (IOException e) {
			logger.error("加载配置文件失败");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public Properties getProperties() {
		return this.properties;
	}

}
