package org.throwable.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

/**
 * @author zjc
 * @version 2017/2/13 23:03
 * @description
 */
public class MarshallingDecoderAdapter extends MarshallingDecoder {

	public MarshallingDecoderAdapter(UnmarshallerProvider provider) {
		super(provider);
	}

	public MarshallingDecoderAdapter(UnmarshallerProvider provider, int maxObjectSize) {
		super(provider, maxObjectSize);
	}

	@Override
	public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		return super.decode(ctx, in);
	}
}
