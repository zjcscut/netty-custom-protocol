package org.throwable.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.throwable.handler.HeartBeatRequestHandler;
import org.throwable.handler.LoginAuthRequestHandler;
import org.throwable.protocol.NettyMessageDecoder;
import org.throwable.protocol.NettyMessageEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zjc
 * @version 2017/2/14 23:25
 * @description
 */
public class NettyClient {

	private static final Logger LOG = LoggerFactory.getLogger(NettyClient.class);

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	private EventLoopGroup group;

	private String remoteHost;
	private int remotePort;
	private String clientHost;
	private int clientPort;

	public NettyClient(String remoteHost, int remotePort, String clientHost, int clientPort) {
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.clientHost = clientHost;
		this.clientPort = clientPort;
		group = new NioEventLoopGroup();
	}

	public void start() throws Exception {
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4, -8, 0));
							ch.pipeline().addLast(new NettyMessageEncoder());
							ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
							ch.pipeline().addLast("loginAuthRequestHandler", new LoginAuthRequestHandler());
							ch.pipeline().addLast("heartBeatRequestHandler", new HeartBeatRequestHandler());
						}
					});
			ChannelFuture future = bootstrap.connect(
					new InetSocketAddress(remoteHost, remotePort),
					new InetSocketAddress(clientHost, clientPort)
			).sync();
			future.channel().closeFuture().sync();
		} finally {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(1);
						try {
							start();  //重新发起连接
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public void shutdown() throws Exception {
		if (null != group) {
			group.shutdownGracefully();
		}
	}
}
