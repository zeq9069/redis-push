package com.demo.rpush.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.rpush.handler.ClientHandler;

/**
 *  client 客户端
 *  与server 连接通信通信
 * @author kyrin
 * @date 2015年10月16日
 *
 */
public class RClient {

	private static final Logger logger=LoggerFactory.getLogger(RClient.class);
	private static ScheduledExecutorService exec = new ScheduledThreadPoolExecutor(1);

	public void start() {
		logger.info("starting to connect redis-push server...");
		EventLoopGroup b = new NioEventLoopGroup();
		Bootstrap client = new Bootstrap();
		client.group(b);
		client.channel(NioSocketChannel.class);
		client.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pip = ch.pipeline();
				pip.addLast(new DelimiterBasedFrameDecoder(1024, true, Unpooled.copiedBuffer("\r\n".getBytes())));//分割
				pip.addLast(new StringDecoder());
				pip.addLast(new StringEncoder());
				pip.addLast(new ClientHandler());
			}
		});
		try {
			ChannelFuture cf = client.connect("127.0.0.1", 7379).sync();
			logger.info("The client start successfully");
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error("client error.");
			e.printStackTrace();
		} finally {
			logger.info("client try to reconnect the redis-push server...");
			exec.execute(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(5);
						try {
							start();
						} catch (Exception e) {
							logger.error("reconnecting redis-push server fialed.{}",e.getMessage());
						}
					} catch (InterruptedException e) {
						logger.error("client reconect redis-push server fialed.");
						e.printStackTrace();
					}
				}
			});
		}
	}

	public static void main(String[] args) {
		RClient rc = new RClient();
		rc.start();
	}
}
