package com.javaeasy.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class UDPMessager implements Messenger{
	public static int DEFAULT_PORT=9999;
	private DatagramSocket UDPWorker;
	private MessageHandler handler;
	private List<DatagramPacket> messageList;
	public UDPMessager() throws SocketException{
		this(DEFAULT_PORT);
	}
	public UDPMessager(int port) throws SocketException{
		// TODO Auto-generated constructor stub
	    UDPWorker =new DatagramSocket(port);
	    messageList=new ArrayList<DatagramPacket>();
	}
	public void setMessageHandle(MessageHandler handler){
		this.handler=handler;
	}
	public void startMessenger(){
		Thread recvThread =new Thread(new MessageReceiver());
		recvThread.start();
		Thread sendThread =new Thread(new MessageSender());
		sendThread.start();
	}
	class MessageReceiver implements Runnable{
		public void run(){
			byte[] data = new byte[1024];
			while(!Thread.currentThread().isInterrupted()){
				DatagramPacket msg =new DatagramPacket(data, data.length);
				try{
					UDPWorker.receive(msg);
				}catch(IOException e){
					e.printStackTrace();
				}
				byte[] recvData=msg.getData();
				byte[] realData=new byte[msg.getLength()];
				System.arraycopy(recvData, 0, realData, 0, msg.getLength());
				handler.handlMessage(realData, msg.getSocketAddress());
			}
		}
	}
	class MessageSender implements Runnable{
		public void run(){
			while(!Thread.currentThread().isInterrupted()){
				DatagramPacket msg= getData();
				try{
					UDPWorker.send(msg);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	private DatagramPacket getData(){
		synchronized (messageList) {
			while(messageList.size()==0){
				try{
					messageList.wait();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			DatagramPacket data=messageList.get(messageList.size()-1);
			messageList.remove(messageList.size()-1);
			return data;
		}
	}
	public void sendData(byte[] data,SocketAddress addr){
		synchronized (messageList) {
			DatagramPacket msg=null;
			msg=new DatagramPacket(data, data.length);
			msg.setSocketAddress(addr);
			messageList.add(msg);
			messageList.notify();
		}
		
	}
}
