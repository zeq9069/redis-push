package com.demo.rpush.utils;

public class Util {

	public static boolean hasText(String value) {
		if (value != null && !"".equals(value.trim())) {
			return true;
		}
		return false;
	}
}
