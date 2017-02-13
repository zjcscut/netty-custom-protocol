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
		nettyHeader.setCrcCode(in.readInt());
		nettyHeader.setLength(in.readInt());
		nettyHeader.setSessionID(in.readLong());
		nettyHeader.setType(in.readByte());
		nettyHeader.setPriority(in.readByte());
		int size = in.readInt();
		if (size > 0) {
			Map<String, Object> attch = new HashMap<>(size);
			int keySize;
			byte[] keyArray;
			String key;
			for (int i = 0; i < size; i++) {
				keySize = in.readInt();
				keyArray = new byte[keySize];
				in.readBytes(keyArray);
				key = new String(keyArray, "UTF-8");
				attch.put(key, marshallingDecoderAdapter.decode(ctx, in));
			}
			nettyHeader.setAttachment(attch);
		}
		if (in.readableBytes() > 0) {
			nettyMessage.setBody(marshallingDecoderAdapter.decode(ctx, in));
		}
		nettyMessage.setNettyHeader(nettyHeader);
		return nettyMessage;
	}
}
