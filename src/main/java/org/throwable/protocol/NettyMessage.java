package org.throwable.protocol;

/**
 * @author zjc
 * @version 2017/2/12 22:20
 * @description
 */
public class NettyMessage {

	private NettyHeader nettyHeader;
	private Object body;

	public NettyHeader getNettyHeader() {
		return nettyHeader;
	}

	public void setNettyHeader(NettyHeader nettyHeader) {
		this.nettyHeader = nettyHeader;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
}
