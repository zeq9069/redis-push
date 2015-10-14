package com.demo.rpush.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Timer;
import java.util.TimerTask;

import com.demo.rpush.bootstrap.config.RPushPropertiesConfig;
import com.demo.rpush.bootstrap.config.loader.ConfigLoader;
import com.demo.rpush.cache.ClientConnectionCache;
import com.demo.rpush.cache.RedisConnectionCache;
import com.demo.rpush.codec.RedisProtocolDecoder;
import com.demo.rpush.codec.RedisProtocolEncoder;
import com.demo.rpush.handler.RedisClientHandler;
import com.demo.rpush.handler.ServerHandler;

/**
 *  根据redis s/c的通信协议原理，实现与redis server的通信
 *  
 * @see http://redis.readthedocs.org/en/latest/topic/protocol.html
 *
 * @author kyrin
 *
 */
public class RBootstrap {

	private static RPushPropertiesConfig rPushConfig;
	private static String command;

	@SuppressWarnings("static-access")
	public RBootstrap() {
		rPushConfig = new RPushPropertiesConfig(new ConfigLoader());
		this.command = rPushConfig.getRedisConfig().getCommand();
	}

	public static void healthbeat() {
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!RedisConnectionCache.isEmpty() && RedisConnectionCache.getFirst().isActive()
						&& !ClientConnectionCache.isEmpty()) {
					Channel ch = RedisConnectionCache.getFirst();
					ch.writeAndFlush(command);
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
				/*pip.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("\r\n".getBytes())));*/
				pip.addLast(new RedisProtocolDecoder());
				pip.addLast(new RedisProtocolEncoder());
				pip.addLast(new RedisClientHandler());
			}
		});
		try {
			healthbeat();
			ChannelFuture cf = client.connect(rPushConfig.getRedisConfig().getHost(),
					rPushConfig.getRedisConfig().getPort()).sync();
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
			ChannelFuture cf = server.bind(rPushConfig.getrServerConfig().getPort()).sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			boss.shutdownGracefully();
			work.shutdownGracefully();
		}

	}

	private void start() {
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

	public static void main(String[] args) {
		RBootstrap rb = new RBootstrap();
		rb.start();
	}
}
