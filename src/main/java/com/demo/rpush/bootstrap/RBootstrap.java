package com.demo.rpush.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.util.Timer;
import java.util.TimerTask;

import com.demo.rpush.cache.ClientConnectionCache;
import com.demo.rpush.cache.RedisConnectionCache;
import com.demo.rpush.codec.RedisProtocolDecoder;
import com.demo.rpush.codec.RedisProtocolEncoder;
import com.demo.rpush.handler.RedisClientHandler;
import com.demo.rpush.handler.ServerHandler;

/**
 * redis client demo
 * 
 *  根据redis s/c的通信协议原理，实现与redis server的通信
 *  
 * @see http://redis.readthedocs.org/en/latest/topic/protocol.html
 *
 * @author kyrin
 *
 */
public class RBootstrap {

	public static void healthbeat() {
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				if (!RedisConnectionCache.isEmpty() && RedisConnectionCache.getFirst().isActive()
						&& !ClientConnectionCache.isEmpty()) {
					Channel ch = RedisConnectionCache.getFirst();
					ch.writeAndFlush("*2\r\n$4\r\nLPOP\r\n$3\r\nwww\r\n");
				}
			}
		}, 0, 1);
	}

	/**
	 * 连接redis-server
	 */
	public static void clientStart() {
		EventLoopGroup b = new NioEventLoopGroup();
		Bootstrap client = new Bootstrap();
		client.group(b);
		client.channel(NioSocketChannel.class);
		client.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pip = ch.pipeline();
				pip.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("$".getBytes())));
				pip.addLast(new RedisProtocolDecoder());
				pip.addLast(new RedisProtocolEncoder());
				pip.addLast(new RedisClientHandler());
			}
		});
		try {
			healthbeat();
			ChannelFuture cf = client.connect("127.0.0.1", 6379).sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			b.shutdownGracefully();
		}
	}

	/**
	 * 启动自定义服务my server
	 */
	public static void serverStart() {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup work = new NioEventLoopGroup();

		ServerBootstrap server = new ServerBootstrap();
		server.group(boss, work);
		server.channel(NioServerSocketChannel.class);
		server.option(ChannelOption.SO_BACKLOG, 1024);
		server.option(ChannelOption.SO_KEEPALIVE, true);
		server.childHandler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pip = ch.pipeline();
				pip.addLast(new RedisProtocolDecoder());
				pip.addLast(new RedisProtocolEncoder());
				pip.addLast(new ServerHandler());
			}
		});

		try {
			ChannelFuture cf = server.bind(7379).sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			boss.shutdownGracefully();
			work.shutdownGracefully();
		}

	}

	public static void main(String[] args) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				serverStart();
			}

		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				clientStart();
			}
		}).start();

	}
}
