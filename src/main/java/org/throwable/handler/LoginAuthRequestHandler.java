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
 * @version 2017/2/13 23:26
 * @description 客户端握手验证handler
 */
public class LoginAuthRequestHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(LoginAuthRequestHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(buildLoginReq());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		NettyMessage message = (NettyMessage) msg;

		// 如果是握手应答消息，需要判断是否认证成功
		if (message.getNettyHeader() != null
				&& message.getNettyHeader().getType() == MessageType.LOGIN_RESPONSE) {
			byte loginResult = (byte) message.getBody();
			if (loginResult != (byte) 0) {
				// 握手失败，关闭连接
				ctx.close();
			} else {
				LOG.info("Login is ok : " + message);
				ctx.fireChannelRead(msg);
			}
		} else
			//调用下一个channel链..
			ctx.fireChannelRead(msg);
	}

	/**
	 * 构建登录请求
	 */
	private NettyMessage buildLoginReq() {
		NettyMessage message = new NettyMessage();
		NettyHeader header = new NettyHeader();
		header.setType(MessageType.LOGIN_REQUEST);
		message.setNettyHeader(header);
		return message;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.fireExceptionCaught(cause);
	}
}
