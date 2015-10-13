package com.demo.rpush.bootstrap.config.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
	private Properties properties = new Properties();

	private static final String DEFAULT_PROPERTIES = "src/main/resources/redis-push.properties";

	private String config;

	public ConfigLoader() {
		this(DEFAULT_PROPERTIES);
	}

	public ConfigLoader(String conf) {

		this.config = conf;

		FileInputStream file = null;
		try {
			if (config == null || "".equals(config.trim())) {
				throw new FileNotFoundException("缺少配置文件");
			}
			if (!config.endsWith(".properties")) {
				throw new IllegalArgumentException("配置文件必须是properties类型");
			}

			//如果是绝对路径
			if (config.startsWith("/") || config.indexOf(":") > 0) {
				file = new FileInputStream(new File(config));
			} else {
				file = new FileInputStream(new File(System.getProperty("user.dir") + "/" + config));
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(0);
		}

		try {
			properties.load(file);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public Properties getProperties() {
		return this.properties;
	}

}
