package org.throwable.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.throwable.handler.HeartBeatResponseHandler;
import org.throwable.handler.LoginAuthResponseHandler;
import org.throwable.protocol.NettyMessageDecoder;
import org.throwable.protocol.NettyMessageEncoder;

/**
 * @author zjc
 * @version 2017/2/14 23:25
 * @description
 */
public class NettyServer {
	private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private ServerBootstrap serverBootstrap = new ServerBootstrap();

	private String host;
	private int port;

	public NettyServer(String host, int port) {
		this.host = host;
		this.port = port;
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
	}

	public void start() throws Exception {
		serverBootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new NettyMessageDecoder(1024*1024,  4, 4, -8, 0));
						ch.pipeline().addLast(new NettyMessageEncoder());
						ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
						ch.pipeline().addLast("loginAuthResponseHandler", new LoginAuthResponseHandler());
						ch.pipeline().addLast("heartBeatResponseHandler", new HeartBeatResponseHandler());
					}
				});
		serverBootstrap.bind(host, port).sync();
		LOG.info("Netty server start ok : "
				+ (host + " : " + port));
	}

	public void shutdown() throws Exception {
		if (null != bossGroup) {
			bossGroup.shutdownGracefully();
		}
		if (null != workerGroup) {
			workerGroup.shutdownGracefully();
		}
	}
}
