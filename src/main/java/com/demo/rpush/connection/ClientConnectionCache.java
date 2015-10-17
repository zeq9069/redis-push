package com.demo.rpush.connection;

import java.util.LinkedList;
import java.util.Random;

import com.demo.rpush.bootstrap.config.ClientConfig;
import com.demo.rpush.bootstrap.config.RPushPropertiesConfig;
import com.demo.rpush.bootstrap.config.loader.ConfigLoader;

/**
 * 连接my server的client缓存
 * @author kyrin 
 * @date 2015年10月16日
 *
 */
public class ClientConnectionCache {
	private static LinkedList<ClientConnection> clientConnect = new LinkedList<ClientConnection>();
	private static final ClientConfig cc = new RPushPropertiesConfig(new ConfigLoader()).getClientConfig();

	public static ClientConnection get() {
		ClientConnection ccon;
		if (cc.getRoute().equals(ClientConfig.ROUTE_WEIGHT)) {
			ccon= weights();
		} else if(cc.getRoute().equals(ClientConfig.ROUTE_POLL)){
			ccon= poll();
		}else{
			ccon=poll();//默认轮询
		}
		if(ccon.channel==null || !ccon.channel.isActive()){
			clientConnect.remove(ccon);
			ccon=get();
		}
		return ccon;
	}

	//轮询
	private static synchronized ClientConnection poll() {
		ClientConnection cc = clientConnect.pop();
		clientConnect.addLast(cc);
		return cc;
	}

	//权重
	private static ClientConnection weights() {
		int total = 0;
		for (ClientConnection cc : clientConnect) {
			total += cc.getWeight();
		}

		Random ran = new Random();
		int number = ran.nextInt(total) + 1;
		int start = 0, end = 0;
		for (int i = 1; i <= clientConnect.size(); i++) {
			if (i == 1) {
				start = clientConnect.get(i - 1).getWeight();
				end = start + clientConnect.get(i).getWeight();
			} else {
				start = end;
				end = clientConnect.get(i).getWeight();
			}

			if (number >= start && number <= end) {
				return clientConnect.get(i);
			} else if (number < start) {
				return clientConnect.get(i - 1);
			} else {
				continue;
			}
		}
		return clientConnect.getFirst();
	}

	public static ClientConnection get(int index) {
		return clientConnect.get(index);
	}

	public static synchronized void add(ClientConnection client) {
		clientConnect.addLast(client);
	}

	public static synchronized void remove(ClientConnection value) {
		clientConnect.remove(value);
	}

	public static boolean isEmpty() {
		return clientConnect.isEmpty();
	}

	public static int size() {
		return clientConnect.size();
	}

}
