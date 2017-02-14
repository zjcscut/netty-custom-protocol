package org.throwable.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.throwable.protocol.MessageType;
import org.throwable.protocol.NettyHeader;
import org.throwable.protocol.NettyMessage;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zjc
 * @version 2017/2/13 23:28
 * @description 客户端心跳发送handler
 */
public class HeartBeatRequestHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(HeartBeatRequestHandler.class);
	//使用定时任务发送
	private volatile ScheduledFuture<?> heartBeat;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		NettyMessage message = (NettyMessage) msg;
		// 当握手成功后，Login响应向下透传，主动发送心跳消息
		if (message.getNettyHeader() != null
				&& message.getNettyHeader().getType() == MessageType.LOGIN_RESPONSE) {
			//NioEventLoop是一个Schedule,因此支持定时器的执行，创建心跳计时器
			heartBeat = ctx.executor().scheduleAtFixedRate(
					new HeartBeatRequestHandler.HeartBeatTask(ctx), 0, 5000,
					TimeUnit.MILLISECONDS);
		} else if (message.getNettyHeader() != null
				&& message.getNettyHeader().getType() == MessageType.HEARTBEAT_RESPONSE) {
			LOG.info("Client receive server heart beat message : ---> "
					+ message);
		} else{
			ctx.fireChannelRead(msg);
		}
	}

	//Ping消息任务类
	private class HeartBeatTask implements Runnable {
		private final ChannelHandlerContext ctx;

		private HeartBeatTask(final ChannelHandlerContext ctx) {
			this.ctx = ctx;
		}

		@Override
		public void run() {
			NettyMessage heatBeat = buildHeatBeat();
			LOG.info("Client send heart beat messsage to server : ---> "
					+ heatBeat);
			ctx.writeAndFlush(heatBeat);
		}

		private NettyMessage buildHeatBeat() {
			NettyMessage message = new NettyMessage();
			NettyHeader header = new NettyHeader();
			header.setType(MessageType.HEARTBEAT_REQUEST);
			message.setNettyHeader(header);
			return message;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		if (heartBeat != null) {
			heartBeat.cancel(true);
			heartBeat = null;
		}
		ctx.fireExceptionCaught(cause);
	}
}
