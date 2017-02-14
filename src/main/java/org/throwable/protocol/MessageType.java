package org.throwable.protocol;

/**
 * @author zjc
 * @version 2017/2/14 23:13
 * @description
 */
public interface MessageType {

	byte LOGIN_REQUEST = 127;
	byte LOGIN_RESPONSE = 126;
	byte HEARTBEAT_REQUEST = 111;
	byte HEARTBEAT_RESPONSE = 112;
}
