package com.demo.rpush.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class RedisProtocolDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		byte[] bb = new byte[in.readableBytes()];
		in.readBytes(bb, 0, in.readableBytes());
		String str = new String(bb, Charset.defaultCharset());
		if (str.toLowerCase().equals("-1\r\n")) {
			out.add("0");
			return;
		}
		int firstEnter = str.indexOf("\r\n");
		int len = Integer.parseInt(str.substring(0, firstEnter));
		String value = str.substring(firstEnter + 2, firstEnter + len + 2);
		out.add(value);
	}

}
