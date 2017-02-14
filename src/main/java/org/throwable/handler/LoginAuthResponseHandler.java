package org.throwable.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.throwable.protocol.MessageType;
import org.throwable.protocol.NettyHeader;
import org.throwable.protocol.NettyMessage;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zjc
 * @version 2017/2/13 23:26
 * @description 服务端握手验证handler
 */
public class LoginAuthResponseHandler extends ChannelInboundHandlerAdapter {


	private static final Logger LOG = LoggerFactory.getLogger(LoginAuthResponseHandler.class);
	/**
	 * 本地缓存
	 */
	private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();
	private String[] whitekList = {"127.0.0.1", "192.168.1.104"};

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		NettyMessage message = (NettyMessage) msg;
		// 如果是握手请求消息，处理，其它消息透传
		if (message.getNettyHeader() != null
				&& message.getNettyHeader().getType() == MessageType.LOGIN_REQUEST) {
			String nodeIndex = ctx.channel().remoteAddress().toString();
			NettyMessage loginResp = null;
			// 重复登陆，拒绝
			if (nodeCheck.containsKey(nodeIndex)) {
				loginResp = buildResponse((byte) -1);
			} else {
				InetSocketAddress address = (InetSocketAddress) ctx.channel()
						.remoteAddress();
				String ip = address.getAddress().getHostAddress();
				boolean isOK = false;
				for (String WIP : whitekList) {
					if (WIP.equals(ip)) {
						isOK = true;
						break;
					}
				}
				loginResp = isOK ? buildResponse((byte) 0) : buildResponse((byte) -1);
				if (isOK) {
					nodeCheck.put(nodeIndex, true);
				}
			}
			LOG.info("The login response is : " + loginResp
					+ " body [" + loginResp.getBody() + "]");
			ctx.writeAndFlush(loginResp);
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	private NettyMessage buildResponse(byte result) {
		NettyMessage message = new NettyMessage();
		NettyHeader header = new NettyHeader();
		header.setType(MessageType.LOGIN_RESPONSE);
		message.setNettyHeader(header);
		message.setBody(result);
		return message;
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		nodeCheck.remove(ctx.channel().remoteAddress().toString());// 删除缓存
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}
}
