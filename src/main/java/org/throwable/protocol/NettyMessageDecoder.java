package org.throwable.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.throwable.utils.MarshallingCodeCFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zjc
 * @version 2017/2/12 22:23
 * @description
 */
public final class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {


	private MarshallingDecoderAdapter marshallingDecoderAdapter;

	public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
							   int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
		this.marshallingDecoderAdapter = MarshallingCodeCFactory.buildMarshallingDecoder();
	}

	@Override
	public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (null == frame) {
			return null;
		}
		NettyMessage nettyMessage = new NettyMessage();
		NettyHeader nettyHeader = new NettyHeader();
		nettyHeader.setCrcCode(frame.readInt());
		nettyHeader.setLength(frame.readInt());
		nettyHeader.setSessionID(frame.readLong());
		nettyHeader.setType(frame.readByte());
		nettyHeader.setPriority(frame.readByte());
		int size = frame.readInt();
		if (size > 0) {
			Map<String, Object> attch = new HashMap<>(size);
			int keySize;
			byte[] keyArray;
			String key;
			for (int i = 0; i < size; i++) {
				keySize = frame.readInt();
				keyArray = new byte[keySize];
				frame.readBytes(keyArray);
				key = new String(keyArray, "UTF-8");
				attch.put(key, marshallingDecoderAdapter.decode(ctx, frame));
			}
			nettyHeader.setAttachment(attch);
		}
		if (frame.readableBytes() > 0) {
			nettyMessage.setBody(marshallingDecoderAdapter.decode(ctx, frame));
		}
		nettyMessage.setNettyHeader(nettyHeader);
		return nettyMessage;
	}
}
