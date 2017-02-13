package org.throwable.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingEncoder;

/**
 * @author zjc
 * @version 2017/2/13 23:04
 * @description
 */
public class MarshallingEncoderAdapter extends MarshallingEncoder {

	public MarshallingEncoderAdapter(MarshallerProvider provider) {
		super(provider);
	}

	@Override
	public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		super.encode(ctx, msg, out);
	}
}
