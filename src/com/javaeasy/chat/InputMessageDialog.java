package com.javaeasy.chat;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.javaeasy.utils.ChatUtils;
public class InputMessageDialog extends JDialog{
	private String[][] items;
	private JTextField[] values;
	private String[] retValues;
	public InputMessageDialog(Frame owner,String title,boolean modal,String[][] items){
		super(owner,title,modal);
		this.items=items;
		init();
	}
	private void init(){
		Container container =this.getContentPane();
		container.setLayout(new GridBagLayout());
		GridBagConstraints gbc= new GridBagConstraints();
		int count = items.length;	
		gbc.gridx=0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.fill=GridBagConstraints.NONE;
		gbc.insets=new Insets(3,3,3,3);//容器上下左右留的宽度
		for(int i=0;i<count;i++){
			gbc.gridy=i;
			container.add(new JLabel(items[i][0]), gbc );
		}
		//添加标签，也就是用户名和端口号
		values =new JTextField[count];
		gbc.gridx=1;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=1.0;
		gbc.weighty=1.0;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		for(int i=0;i<count;i++){
			gbc.gridy=i;
			values[i]=new JTextField(items[i][1]);
			container.add(values[i], gbc);
		}
		//添加两个文本框
		gbc.gridx=0;
		gbc.gridy=count;
		gbc.gridwidth=2;
		gbc.gridheight=1;
		gbc.weightx=1.0;
		gbc.weighty=1.0;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.anchor=GridBagConstraints.SOUTH;
		container.add(new JSeparator(),gbc);
		//水平方向，在布局中分隔两个面板
		gbc.gridy=count+1;
		gbc.weightx=1.0;
		gbc.weighty=0;
		gbc.fill=GridBagConstraints.NONE;
		gbc.anchor=GridBagConstraints.SOUTHEAST;
		gbc.insets=new Insets(7, 7, 7, 7);
		JButton btn = new JButton("确定");
		container.add(btn, gbc);
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				retValues =new String[items.length];
				for(int i=0;i<retValues.length;i++){
					retValues[i]=values[i].getText();
				}
				InputMessageDialog.this.dispose();
			}
		});
		this.setSize(350,200);
		ChatUtils.locateDialogCenter(this);
	}
	public String[] getValue(){
		return retValues;
	}

	
}
