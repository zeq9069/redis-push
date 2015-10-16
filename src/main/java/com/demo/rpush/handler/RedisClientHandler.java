package com.demo.rpush.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import com.demo.rpush.bootstrap.exception.RedisServiceException;
import com.demo.rpush.connection.ClientConnection;
import com.demo.rpush.connection.ClientConnectionCache;
import com.demo.rpush.connection.RedisConnectionCache;

public class RedisClientHandler extends ChannelHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("active");
		RedisConnectionCache.add(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("redis-client接收到：" + msg.toString());
		if (!ClientConnectionCache.isEmpty()) {
			ClientConnection cc = ClientConnectionCache.get();
			Channel ch = cc.getChannel();
			if (ch != null && ch.isActive()) {
					ch.writeAndFlush(msg + "\r\n");//消息以CLRF结尾
			} else {
				throw new RedisServiceException("redis 服务出现异常");
			}
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
}
