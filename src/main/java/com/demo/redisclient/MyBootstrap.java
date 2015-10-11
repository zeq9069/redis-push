package com.demo.redisclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * redis client demo
 * 
 *  根据redis s/c的通信协议原理，实现与redis server的通信
 *
 * @author kyrin
 *
 */
public class MyBootstrap {
	
	public static void main(String[] args) {
		EventLoopGroup b=new NioEventLoopGroup();
		
		Bootstrap client=new Bootstrap();
		client.group(b);
		client.channel(NioSocketChannel.class);
		client.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pip=ch.pipeline();
				pip.addLast(new StringDecoder());
				pip.addLast(new StringEncoder());
				pip.addLast(new MyHandler());
			}
		});
		
		try {
			ChannelFuture cf=client.connect("127.0.0.1",6379).sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			b.shutdownGracefully();
		}
	}
}
