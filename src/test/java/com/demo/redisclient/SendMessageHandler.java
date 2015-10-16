package com.demo.redisclient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SendMessageHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel ch = ctx.channel();
		for (int i = 0; i <= 10000000; i++) {
			ch.writeAndFlush("*3\r\n$5\r\nlpush\r\n$7\r\nmyQueue\r\n$10\r\n1111111111\r\n");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.close();
	}
}
