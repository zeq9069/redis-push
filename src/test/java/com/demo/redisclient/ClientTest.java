package com.demo.redisclient;

import com.demo.rpush.client.RClient;

public class ClientTest {

	public static void main(String[] args) {
		RClient rc = new RClient();
		rc.start();
	}

}
