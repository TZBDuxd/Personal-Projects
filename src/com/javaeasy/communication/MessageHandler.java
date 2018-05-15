package com.javaeasy.communication;

import java.net.SocketAddress;

public interface MessageHandler {
	void handlMessage(byte[] data, SocketAddress addr);
}
