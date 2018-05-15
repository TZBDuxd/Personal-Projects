package com.javaeasy.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class ChatUtils {
	public static final String SEPARATOR = "#";

	public static byte[] buildMesasage(String name, String content) {
		String message = name + SEPARATOR + content;
		return message.getBytes();
	}

	public static String[] parseMessage(byte[] data) {
		String message = new String(data);
		int pos = message.indexOf(SEPARATOR);
		if (pos == -1) {
			System.out.println("收到不符合格式的消息：" + message);
			return null;
		}
		String[] msg = new String[2];
		msg[0] = message.substring(0, pos);
		msg[1] = message.substring(pos + 1);
		return msg;
	}

	public static void locateFrameCenter(JFrame frame) {
		int frameWidth = frame.getWidth();
		int frameHeight = frame.getHeight();
		Toolkit toolkit = Toolkit.getDefaultToolkit();// 得到用户显示器分辨率
		Dimension screen = toolkit.getScreenSize();
		int screenWidth = screen.width;
		int screenHeight = screen.height;
		frame.setLocation((screenWidth - frameWidth) / 2, (screenHeight - frameHeight) / 2);
	}

	public static void locateDialogCenter(JDialog dialog) {
		// TODO Auto-generated method stub
		int frameWidth = dialog.getWidth();
		int frameHeight = dialog.getHeight();
		Toolkit toolkit = Toolkit.getDefaultToolkit();// 得到用户显示器分辨率
		Dimension screen = toolkit.getScreenSize();
		int screenWidth = screen.width;
		int screenHeight = screen.height;
		dialog.setLocation((screenWidth - frameWidth) / 2, (screenHeight - frameHeight) / 2);

	}

	public static InetSocketAddress createSocketAddrFromStr(String ipStr, String portStr) {
		try {
			String[] numberStrs = ipStr.split("\\.");
			int len = numberStrs.length;
			byte[] ipNumber = new byte[len];
			for(int i=0;i<len;i++){
				ipNumber[i]=Short.valueOf(numberStrs[i]).byteValue();
			}
			InetSocketAddress addr = new InetSocketAddress(InetAddress.getByAddress(ipNumber), Integer.valueOf(portStr));
			return addr;
		} catch (UnknownHostException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}

}
