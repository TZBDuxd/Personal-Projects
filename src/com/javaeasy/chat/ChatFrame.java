package com.javaeasy.chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.JobAttributes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.prefs.BackingStoreException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.javaeasy.communication.MessageHandler;
import com.javaeasy.communication.Messenger;
import com.javaeasy.communication.UDPMessager;
import com.javaeasy.utils.ChatUtils;

public class ChatFrame implements MessageHandler {
	private JFrame frame;
	private JTextArea recvMsg;
	private JTextArea enterMsg;
	private JList userList;
	private JButton addUserBtn;
	private JButton sendBtn;
	private DefaultListModel userListModel;
	private String name;
	private int port;
	private Messenger messenger;
	private static final String ECHO_STRING = "echo";

	public ChatFrame() {
		buildGUI();
		init();
	}

	private void buildGUI() {
		frame = new JFrame();
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ChatUtils.locateFrameCenter(frame);

		recvMsg = new JTextArea();
		recvMsg.setRows(1);
		recvMsg.setColumns(10);
		JScrollPane scrollRecv = new JScrollPane(recvMsg);
		recvMsg.setEditable(false);

		enterMsg = new JTextArea();
		JScrollPane scrollEnter = new JScrollPane(enterMsg);
		scrollEnter.setMinimumSize(new Dimension(100, 80));
		scrollEnter.setPreferredSize(new Dimension(100, 80));

		userListModel = new DefaultListModel();
		userList = new JList(userListModel);
		addUserBtn = new JButton("添加好友");
		sendBtn = new JButton("发送消息");

		JPanel sendPanel = new JPanel();
		sendPanel.setLayout(new BorderLayout(7, 0));
		sendPanel.add(scrollEnter, BorderLayout.CENTER);
		sendPanel.add(sendBtn, BorderLayout.EAST);

		JPanel userListPanel = new JPanel();
		JScrollPane scrollUserList = new JScrollPane(userList);
		scrollUserList.setMinimumSize(new Dimension(80, 80));
		scrollUserList.setPreferredSize(new Dimension(80, 80));
		userListPanel.setLayout(new BorderLayout(0, 3));
		userListPanel.add(scrollUserList, BorderLayout.CENTER);
		userListPanel.add(addUserBtn, BorderLayout.SOUTH);

		Container container = frame.getContentPane();
		container.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(7, 7, 7, 7);
		container.add(scrollRecv, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(7, 0, 7, 7);
		container.add(userListPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
//		gbc.weighty=1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 7, 7, 7);
		container.add(sendPanel, gbc);
	}

	private void init() {
		while (true) {
			String[][] initValue = new String[][] { { "用户名：", "深夜两点" }, { "端口：", "9999" } };
			InputMessageDialog initDialog = new InputMessageDialog(frame, "请输入用户名和端口", true, initValue);
			initDialog.setVisible(true);
			String[] nameAndPort = initDialog.getValue();
			if (nameAndPort == null) {
				continue;
			}
			this.name = nameAndPort[0];
			if (name.indexOf(ChatUtils.SEPARATOR) != -1) {
				String errMsg = "用户名中不能包含：" + ChatUtils.SEPARATOR;
				JOptionPane.showMessageDialog(frame, errMsg, "错误的用户名", JOptionPane.ERROR_MESSAGE);
				continue;
			}
			try {
				this.port = Integer.valueOf(nameAndPort[1]);
				if (port <= 0 || port > 65536) {
					String errMsg = "错误的端口号" + nameAndPort[1] + "。端口号必须在0和65536之间";
					JOptionPane.showMessageDialog(frame, errMsg, "错误的端口", JOptionPane.ERROR_MESSAGE);
					continue;
				}
			} catch (NumberFormatException ex) {
				// TODO: handle exception
				JOptionPane.showMessageDialog(frame, "输入的端口号不是数字", "错误", JOptionPane.ERROR_MESSAGE);
				continue;
			}
			break;
		}
		try {
			messenger = new UDPMessager(port);
			messenger.setMessageHandle(this);
			messenger.startMessenger();
		} catch (SocketException e) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(frame, "端口号已被占用。程序将退出", "端口号已被占用", JOptionPane.ERROR_MESSAGE);
			System.exit(1);

		}
		addUserBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				addUser();

			}
		});
		sendBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sendChatMessage();
			}
		});
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				Object selection = userList.getSelectedValue();
				if (selection == null || !(selection instanceof UserModel)) {
					return;
				}
				UserModel user = (UserModel) selection;
				recvMsg.setText(user.getMessageHistory().toString());
				updateTitle();
			}
		});
		updateTitle();

	}

	private void updateTitle() {
		String title = name + "的聊天窗口，通讯端口为" + port;
		Object selection = userList.getSelectedValue();
		if (selection != null && (selection instanceof UserModel)) {
			title = title + "。对方为" + ((UserModel) selection).getName();
		}
		this.frame.setTitle(title);
	}

	private void sendChatMessage() {
		Object selection = userList.getSelectedValue();
		if (selection == null || !(selection instanceof UserModel)) {
			JOptionPane.showMessageDialog(frame, "请先选择好友，再发送消息", "请选择好友", JOptionPane.ERROR_MESSAGE);
			return;
		}
		UserModel user = (UserModel) selection;
		String content = enterMsg.getText();
		sendMessage(content, user.getAddr());
		if (content.length() == 0) {
			return;
		}
		enterMsg.setText("");
		user.getMessageHistory().append(name + "说：\r\n" + content + "\r\n");
		recvMsg.setText(user.getMessageHistory().toString());
	}

	private void addUser() {
		String[][] defaultUser = new String[][] { { "IP地址：", "127.0.0.1" }, { "端口号", "7777" } };
		InputMessageDialog userProp = new InputMessageDialog(frame, "请输入好友信息", true, defaultUser);
		userProp.setVisible(true);
		String[] value = userProp.getValue();
		if (value == null) {
			return;
		}
		InetSocketAddress sockerAddr = ChatUtils.createSocketAddrFromStr(value[0], value[1]);
		if (sockerAddr == null) {
			JOptionPane.showMessageDialog(frame, "输入的信息错误，无法识别的IP地址和端口", "信息错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		sendMessage(ECHO_STRING, sockerAddr);
	}

	private void sendMessage(String content, SocketAddress addr) {
		byte[] data = ChatUtils.buildMesasage(name, content);
		messenger.sendData(data, addr);
	}

	public void showFrame() {
		this.frame.setVisible(true);
	}

	public void handlMessage(byte[] data, SocketAddress addr) {
		String[] content = ChatUtils.parseMessage(data);
		String userName = content[0];
		String message = content[1];
		UserModel newUser = new UserModel(userName, addr);
		UserModel user = findUser(newUser);
		if (user == null) {
			userListModel.addElement(newUser);
			user = newUser;
		}
		if (message.equals(ECHO_STRING)) {
			this.sendMessage("", addr);
			return;
		}
		if (message.length() > 0) {
			user.getMessageHistory().append(userName + "说：\r\n" + message + "\r\n");
		}
		userList.setSelectedValue(user, true);
		updateChatHistory(user.getMessageHistory().toString());
	}

	private void updateChatHistory(String history) {
		recvMsg.setText(history);
		int lastLineStart = history.lastIndexOf('\n');
		if (lastLineStart == -1) {
			return;
		}
		recvMsg.setCaretPosition(lastLineStart);
	}

	private UserModel findUser(UserModel model) {
		int n = userListModel.getSize();
		for (int i = 0; i < n; i++) {
			UserModel user = (UserModel) userListModel.get(i);
			if (user.equals(model)) {
				return user;
			}
		}
		return null;
	}

}

class UserModel {
	private String name;
	private SocketAddress addr;
	private StringBuffer messageHistory;

	public UserModel(String name, SocketAddress addr) {
		this.name = name;
		this.addr = addr;
		messageHistory = new StringBuffer();
	}

	public StringBuffer getMessageHistory() {
		return messageHistory;
	}

	public void setMessageHistory(StringBuffer messageHistory) {
		this.messageHistory = messageHistory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SocketAddress getAddr() {
		return addr;
	}

	public void setAddr(SocketAddress addr) {
		this.addr = addr;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addr == null) ? 0 : addr.hashCode());
		return result;
	}

	public String toString() {
		return name;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserModel other = (UserModel) obj;
		if (addr == null) {
			if (other.addr != null)
				return false;
		} else if (!addr.equals(other.addr))
			return false;
		return true;
	}

}
