package com.demo.redisclient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SendMessageHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel ch = ctx.channel();
		for (int i = 0; i <= 100000; i++) {
			//ch.writeAndFlush("*2\r\n$4\r\nlpop\r\n$7\r\nmyQueue\r\n");
			ch.writeAndFlush("*3\r\n$5\r\nlpush\r\n$7\r\nmyQueue\r\n$10\r\n1111111111\r\n");
			//ch.writeAndFlush("*3\r\n$4\r\nsadd\r\n$7\r\nmyQueue\r\n$10\r\n"+i+"1111111111\r\n");
			System.out.println(i);
		}
		System.out.println("发送消息jieshu");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.close();
	}
}
