package com.demo.redisclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class SendMessage {

	private static void clientStart() {
		EventLoopGroup b = new NioEventLoopGroup();
		Bootstrap client = new Bootstrap();
		client.group(b);
		client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 6000).option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.SO_SNDBUF, 65535).option(ChannelOption.SO_RCVBUF, 65535);
		client.channel(NioSocketChannel.class);
		client.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pip = ch.pipeline();
				//pip.addLast(new DelimiterBasedFrameDecoder(1024, false, Unpooled.copiedBuffer("\r\n".getBytes())));
				pip.addLast(new StringDecoder());
				pip.addLast(new StringEncoder());
				pip.addLast(new SendMessageHandler());
			}
		});
		try {
			ChannelFuture cf = client.connect("202.205.180.29", 6379).sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			b.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		clientStart();
	}

}
