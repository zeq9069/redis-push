package com.demo.rpush;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

/**
 * 
 * @author kyrin
 *
 */
public class MyServerHandler extends ChannelHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelActive");
		Channel ch = ctx.channel();
		if (ch instanceof SocketChannel) {
			SocketChannel is = (SocketChannel) ch;
			ClientConnectionCache.put(is.remoteAddress().getHostName(), ctx.channel());
			System.out.println(is.remoteAddress().getHostName() + ":" + is.remoteAddress().getPort());
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelReadComplete");
		ctx.flush();
	}

}
