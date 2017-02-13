package org.throwable.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.throwable.utils.MarshallingCodeCFactory;

import java.util.List;
import java.util.Map;

/**
 * @author zjc
 * @version 2017/2/12 22:23
 * @description
 */
public final class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {


	private MarshallingEncoderAdapter marshallingEncoderAdapter;

	public NettyMessageEncoder() {
		marshallingEncoderAdapter = MarshallingCodeCFactory.buildMarshallingEncoder();
	}

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage, List<Object> list) throws Exception {
		if (null == nettyMessage || null == nettyMessage.getNettyHeader()) {
			throw new RuntimeException("The encode message is null");
		}
		ByteBuf sendBuf = Unpooled.buffer();
		sendBuf.writeInt(nettyMessage.getNettyHeader().getCrcCode());
		sendBuf.writeInt(nettyMessage.getNettyHeader().getLength());
		sendBuf.writeLong(nettyMessage.getNettyHeader().getSessionID());
		sendBuf.writeByte(nettyMessage.getNettyHeader().getType());
		sendBuf.writeByte(nettyMessage.getNettyHeader().getPriority());
		sendBuf.writeInt(nettyMessage.getNettyHeader().getAttachment().size());
		String key;
		byte[] keyArray;
		Object value;
		for (Map.Entry<String, Object> entry : nettyMessage.getNettyHeader().getAttachment().entrySet()) {
			key = entry.getKey();
			keyArray = key.getBytes();
			sendBuf.writeBytes(keyArray);
			value = entry.getValue();
			marshallingEncoderAdapter.encode(channelHandlerContext, value, sendBuf);
		}

		if (null != nettyMessage.getBody()) {
			marshallingEncoderAdapter.encode(channelHandlerContext, nettyMessage.getBody(), sendBuf);
		} else {
			sendBuf.writeInt(0);
			sendBuf.setInt(4, sendBuf.readableBytes());
		}

	}
}
