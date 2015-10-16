package com.demo.rpush.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * @author kyrin
 * @date 2015年10月16日
 *
 */
public class RedisProtocolEncoder extends MessageToByteEncoder<String> {

	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
		ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
		out.writeBytes(buf);
	}

}
