package com.demo.rpush.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * 
 *状态回复（status reply）的第一个字节是 "+"  		eg.+OK
 *错误回复（error reply）的第一个字节是 "-"			eg.-ERR unknown command 'foobar'
 *整数回复（integer reply）的第一个字节是 ":" 		eg. :1000\r\n
 *批量回复（bulk reply）的第一个字节是 "$"    		eg. $6\r\nfoobar\r\n
 *多条批量回复（multi bulk reply）的第一个字节是 "*" 	eg.*3\r\n$3\r\nSET\r\n$5\r\nmykey\r\n$7\r\nmyvalue\r\n
 * 
 * 注意:
 * 我们每次轮询都是一个一个的拉取数据，所以在编解码的时候，考虑粘包的时候，不需要考虑多条批量恢复"*"
 * @author kyrin
 *
 */
public class RedisProtocolDecoder extends ByteToMessageDecoder {

	private byte[] bbuf = new byte[1024];

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		/*byte[] bb = new byte[in.readableBytes()];
		in.readBytes(bb, 0, in.readableBytes());
		String str = new String(bb, Charset.defaultCharset());

		if (str.toLowerCase().equals("$-1")) {
			out.add("0");
			return;
		}
		if (!str.toLowerCase().startsWith("$")) {
			out.add(str);
		}*/

		if (!Arrays.asList(bbuf).isEmpty()) {
			ByteBuf gg = in.copy();
			in.readBytes(bbuf, in.readerIndex(), in.readableBytes());
			String str = new String(bbuf, Charset.defaultCharset());
			in = gg.copy();
			bbuf = new byte[1024];
		}
		while (true) {
			ByteBuf bbb = de(out, in);
			if (bbb == null) {
				return;
			} else {
				in = bbb;
			}
		}
	}

	public ByteBuf de(List<Object> out, ByteBuf in) {
		byte key = in.readByte();
		String num = "";
		if (key == '$') {
			while (in.readableBytes() > 0) {
				byte b = in.readByte();
				if (b == '\r') {
					in.skipBytes(1);
					break;
				}
				num += String.valueOf((char) b);
			}
			int value = Integer.parseInt(num);
			if (value == -1) {
				out.add(0);
				in.skipBytes(2);
			} else {
				in.skipBytes(2);
				ByteBuf buf = in.readBytes(value);
				in.skipBytes(2);
				byte[] bb = new byte[buf.readableBytes()];
				buf.readBytes(bb, 0, buf.readableBytes());
				String str = new String(bb, Charset.defaultCharset());
				out.add(str);
			}
		} else {
			bbuf[0] = key;
			in.readBytes(bbuf, in.readerIndex(), in.readableBytes());
			return null;
		}
		return in;
	}
}
