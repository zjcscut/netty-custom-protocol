package org.throwable.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.throwable.protocol.MessageType;
import org.throwable.protocol.NettyHeader;
import org.throwable.protocol.NettyMessage;

/**
 * @author zjc
 * @version 2017/2/13 23:28
 * @description 服务端心跳发送handler
 */
public class HeartBeatResponseHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(HeartBeatResponseHandler.class);
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		NettyMessage message = (NettyMessage) msg;
		// 返回心跳应答消息
		if (message.getNettyHeader() != null
				&& message.getNettyHeader().getType() == MessageType.HEARTBEAT_REQUEST) {
			LOG.info("Receive client heart beat message : ---> "
					+ message);
			NettyMessage heartBeat = buildHeatBeat();
			LOG.info("Send heart beat response message to client : ---> "
					+ heartBeat);
			ctx.writeAndFlush(heartBeat);
		} else
			ctx.fireChannelRead(msg);
	}

	private NettyMessage buildHeatBeat() {
		NettyMessage message = new NettyMessage();
		NettyHeader header = new NettyHeader();
		header.setType(MessageType.HEARTBEAT_RESPONSE);
		message.setNettyHeader(header);
		return message;
	}
}
