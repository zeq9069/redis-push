package com.demo.rpush.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

import com.demo.rpush.bootstrap.config.ClientConfig;
import com.demo.rpush.connection.ClientConnection;
import com.demo.rpush.connection.ClientConnectionCache;

/**
 * 
 * @author kyrin
 *
 */
public class ServerHandler extends ChannelHandlerAdapter {
	
	private static final Logger logger=LoggerFactory.getLogger(RedisClientHandler.class);
	private ClientConfig clientConfig;
	
	public ServerHandler(ClientConfig clientConfig) {
		this.clientConfig=clientConfig;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("One client connection error.");
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//限制客户端的最大连接数
		int max=clientConfig.getMax() ;
		if (max==-1 ||  max<= ClientConnectionCache.size()) {
			logger.error("Over the max number client  : {}",max);
			ctx.writeAndFlush("Refuse to connect,because more than the number of max client connections.\r\n");
			ctx.close();
			return;
		}
		Channel ch = ctx.channel();
		if (ch instanceof SocketChannel) {
			SocketChannel is = (SocketChannel) ch;
			ClientConnection cc = new ClientConnection(is.remoteAddress().getHostName(), ctx.channel());
			ClientConnectionCache.add(cc);
			logger.info("Client successfully connected：{}:{} ",is.remoteAddress().getHostName(),is.remoteAddress().getPort());
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

}
