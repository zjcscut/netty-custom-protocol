package org.throwable.service;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.throwable.protocol.NettyConstant;

import java.util.concurrent.TimeUnit;

/**
 * @author zjc
 * @version 2017/2/14 23:23
 * @description 使用spring bean初始化时初始化netty服务端和客户端
 */
@Component
public class SpringStarter implements InitializingBean, DisposableBean {

	private NettyServer server;
	private NettyClient client;

	@Override
	public void afterPropertiesSet() throws Exception {
		server = new NettyServer(NettyConstant.REMOTE_HOST, NettyConstant.REMOTE_PORT);
		server.start();
		TimeUnit.SECONDS.sleep(5);
		client = new NettyClient(NettyConstant.REMOTE_HOST, NettyConstant.REMOTE_PORT,
				NettyConstant.CLIENT_HOST, NettyConstant.CLIENT_PORT);
		client.start();
	}

	@Override
	public void destroy() throws Exception {
		if (null != server) {
			server.shutdown();
		}
		if (null != client) {
			client.shutdown();
		}

	}
}
