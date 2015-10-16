package com.demo.rpush.connection;

import io.netty.channel.Channel;

/**
 * 
 * @author kyrin
 * @date 2015年10月16日
 *
 */
public class ClientConnection {

	//IP+port
	String clientId;

	Channel channel;

	//权重,默认为1
	int weight;

	public ClientConnection() {
		this(null, null, 1);
	}

	public ClientConnection(String clientId, Channel channel) {
		this(clientId, channel, 1);
	}

	public ClientConnection(String clientId, Channel channel, int weight) {
		this.clientId = clientId;
		this.channel = channel;
		this.weight = weight;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
