package com.yychatclient.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import com.yychat.model.Message;
import com.yychatclient.view.ClientLogin;
import com.yychatclient.view.FriendChat1;
import com.yychatclient.view.FriendList;

public class ClientRecieverThread extends Thread{
	
	private Socket s;
	
	public ClientRecieverThread(Socket s){
		this.s=s;
	}
	public void run(){
		ObjectInputStream ois;
		while(true){
		try {
			ois = new ObjectInputStream(s.getInputStream());
			Message mess = (Message)ois.readObject();
			String showMessage=mess.getSender()+"对"+mess.getReceiver()+"说："+mess.getContent();
			System.out.println(showMessage);
			
			if(mess.getMessageType().equals(Message.message_AddFriendFailure_NoUser)){
				JOptionPane.showMessageDialog(null, "添加好友失败，用户不存在！");
			}
			if(mess.getMessageType().equals(Message.message_AddFriendFailure_AlreadyFriend)){
				JOptionPane.showMessageDialog(null, "不能重复添加好友!");
			}
			if(mess.getMessageType().equals(Message.message_AddFriendSuccess)){
				JOptionPane.showMessageDialog(null, "添加好友成功!");
				String allFriendName=mess.getContent();
				FriendList friendList=(FriendList)ClientLogin.hmFirendlist.get(mess.getSender());
				friendList.updateFriendIcon(allFriendName);
				friendList.revalidate();
			}
			
			//jta.append(showMessage+"\r\n");
			if(mess.getMessageType().equals(Message.message_Common)){
			FriendChat1 friendChat1=(FriendChat1)FriendList.hmFriendChat1.get(mess.getReceiver()+"to"+mess.getSender());
			
			friendChat1.appendJta(showMessage);
			}
			if(mess.getMessageType().equals((Message.message_OnlineFriend))){
                System.out.println("在线好友"+mess.getContent());
				
				//
				FriendList friendList=(FriendList)ClientLogin.hmFirendlist.get(mess.getReceiver());
				//
				friendList.setEnableFriendIcon(mess.getContent());
			}
			//接受信息，激活图标
			if(mess.getMessageType().equals(Message.message_NewOnlineFriend)){
			System.out.println("新用户上线了，用户名："+mess.getContent());
			FriendList friendList=(FriendList)ClientLogin.hmFirendlist.get(mess.getReceiver());
			friendList.setEnableFriendIcon(mess.getContent());
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
				
	}
		
	
	
	}


