package com.demo.rpush.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

import com.demo.rpush.bootstrap.exception.RedisQueueTypeException;

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

	/**
	 * 一个字节一个字节的去处理数据，因为数据会被挤压到bytebuf中，按照规律一个自己一个字节的去处理，不会出现粘包/读半包的问题
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		if (in.readableBytes() < 2) {
			return;
		}
		byte first = in.readByte();
		if (first == '$') {//如果是批量恢复数据
			String v = "";
			while (in.readableBytes() > 0) {
				byte enter = in.readByte();
				if (enter == '\r') {
					in.skipBytes(1);
					break;
				}
				v += (char) enter;
			}
			int length = Integer.parseInt(v);
			if (length == -1) {
				out.add("0");
				return;
			}
			byte[] bb = new byte[length];
			in.readBytes(bb, 0, length);
			in.skipBytes(2);
			String value = new String(bb, Charset.defaultCharset());
			out.add(value);
		} else if (first == '-') {//如果出现错误
			String result = "";
			while (in.readableBytes() > 0) {
				byte enter = in.readByte();
				if (enter == '\r') {
					in.skipBytes(1);
					break;
				}
				result += (char) enter;
			}
			throw new RedisQueueTypeException("redis server返回错误信息:" + result);
		}
	}
}
