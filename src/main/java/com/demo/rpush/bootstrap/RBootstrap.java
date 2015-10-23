package com.demo.rpush.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.rpush.bootstrap.config.RPushPropertiesConfig;
import com.demo.rpush.bootstrap.config.loader.ConfigLoader;
import com.demo.rpush.codec.RedisProtocolDecoder;
import com.demo.rpush.codec.RedisProtocolEncoder;
import com.demo.rpush.connection.ClientConnectionCache;
import com.demo.rpush.connection.RedisConnectionCache;
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

	private static final Logger logger=LoggerFactory.getLogger(RBootstrap.class);
	
	private static RPushPropertiesConfig rPushConfig;
	private static String command;
	private static ScheduledExecutorService exec = new ScheduledThreadPoolExecutor(1);
	private static ScheduledExecutorService pull = new ScheduledThreadPoolExecutor(Runtime.getRuntime()
			.availableProcessors());

	@SuppressWarnings("static-access")
	public RBootstrap() {
		rPushConfig = new RPushPropertiesConfig(new ConfigLoader());
		this.command = rPushConfig.getRedisConfig().getCommand();
	}

	private static void pull() {
		logger.info("starting pull redis data.");
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				pull.execute(new Runnable() {
					@Override
					public void run() {
						if (!RedisConnectionCache.isEmpty() && RedisConnectionCache.getFirst().isActive()
								&& !ClientConnectionCache.isEmpty()) {
							Channel ch = RedisConnectionCache.getFirst();
							for (int i = 0; i < 50; i++) {//按照redis每秒处理50000数据来处理，不可超出redis处理能力，过大会导致很多问题
								ch.writeAndFlush(command);
							}
						}
					}
				});
			}
		}, 0, 1);
	}

	/**
	 * 连接redis-server
	 */
	private static void clientStart() {
		logger.info("redis-client starting...");
		EventLoopGroup b = new NioEventLoopGroup();
		Bootstrap client = new Bootstrap();
		client.group(b);
		client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 6000).option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.SO_SNDBUF, 65535).option(ChannelOption.SO_RCVBUF, 65535).option(ChannelOption.RCVBUF_ALLOCATOR,AdaptiveRecvByteBufAllocator.DEFAULT);
		client.channel(NioSocketChannel.class);
		client.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pip = ch.pipeline();
				pip.addLast(new RedisProtocolDecoder());
				pip.addLast(new RedisProtocolEncoder());
				pip.addLast(new RedisClientHandler());
			}
		});
		try {
			ChannelFuture cf = client.connect(rPushConfig.getRedisConfig().getHost(),
					rPushConfig.getRedisConfig().getPort()).sync();
			logger.info("The redis-client start successfully");
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error("redis-client error.");
			e.printStackTrace();
		} finally {
			logger.info("redis-client restarting...");
			exec.execute(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(5);
						try {
							clientStart();
						} catch (Exception e) {
							logger.error("reconnecting redis server fialed.{}",e.getMessage());
						}
					} catch (Exception e) {
						logger.error("redis-client restart failed.{}",e.getMessage());
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * 启动自定义服务my server
	 */
	private static void serverStart() {
		logger.info("The redis-push server starting...");
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
				pip.addLast(new ServerHandler(rPushConfig.getClientConfig()));
			}
		});

		try {
			ChannelFuture cf = server.bind(rPushConfig.getrServerConfig().getPort()).sync();
			logger.info("The redis-push server start successfully");
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error("redis-push server error.");
			e.printStackTrace();
		} finally {
			logger.warn("redis-push server shutdown.");
			boss.shutdownGracefully();
			work.shutdownGracefully();
		}

	}

	public void start() {
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
		pull();
	}

	public static void main(String[] args) {
		RBootstrap rb = new RBootstrap();
		rb.start();
	}
}
