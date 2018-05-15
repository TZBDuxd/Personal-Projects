package com.javaeasy.communication;

import java.net.SocketAddress;

public interface Messenger {
	public void setMessageHandle(MessageHandler handler);

	public void sendData(byte[] data, SocketAddress addr);

	public void startMessenger();
}
