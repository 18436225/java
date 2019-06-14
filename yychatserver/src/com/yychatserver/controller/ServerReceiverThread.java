package com.yychatserver.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;




import com.yychat.model.Message;

public class ServerReceiverThread extends Thread{
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Message mess;
	String sender;
	String receiver;
	
	Socket s;
	public ServerReceiverThread(Socket s) {
		this.s=s;
		
	}
	
	public void run(){
		while(true){
		try {
			ois= new ObjectInputStream(s.getInputStream());
			mess=(Message)ois.readObject();
			sender=mess.getSender();
			receiver=mess.getReceiver();
			System.out.println(mess.getSender()+"对"+mess.getReceiver()+"说："+mess.getContent());
			
			if(mess.getMessageType().equals(Message.message_AddFriend)){
				String addFriendName=mess.getContent();
				System.out.println("需要添加新好友的名字："+addFriendName);
				if(!YychatDbUtil.seekUser(addFriendName)){
					mess.setMessageType(Message.message_AddFriendFailure_NoUser);
				}else{
						String relationType="1";
						if(YychatDbUtil.seekRelation(sender,addFriendName,relationType)){
						mess.setMessageType(Message.message_AddFriendFailure_AlreadyFriend);
					}else{
						int count=YychatDbUtil.addRelation(sender, addFriendName, relationType);
						if(count!=0){
							mess.setMessageType(Message.message_AddFriendSuccess);
							String allFriendName=YychatDbUtil.getFriendString(sender);
							mess.setContent(allFriendName);
						}
					}
				}
				sendMessage(s,mess);
			}
			
			
			
			if(mess.getMessageType().equals(Message.message_Common)){
				String content=mess.getContent();
				YychatDbUtil.addMessage(sender,receiver,content);//保存聊天信息
				mess.setSendTime(new Date());
				Socket s1=(Socket)StartServer.hmSocket.get(mess.getReceiver());
				sendMessage(s1,mess);
			}
			
			if(mess.getMessageType().equals(Message.message_RequestOnlineFriend));{
				Set friendSet=StartServer.hmSocket.keySet();
				Iterator it=friendSet.iterator();
				String friendName;
				String friendString=" ";
				while(it.hasNext()){
					friendName=(String)it.next();
					if(!friendName.equals(mess.getSender()))
						friendString=friendName+" "+friendString;
				}
					System.out.println("全部好友的名字;"+friendString);
				    mess.setContent(friendString);
				    mess.setMessageType(Message.message_OnlineFriend);
				    mess.setSender("Server");
				    mess.setReceiver(sender);
				    sendMessage(s,mess);
					
				}
			
			
		}catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
	}

	public void sendMessage(Socket s,Message mess) throws IOException {
		oos =new ObjectOutputStream(s.getOutputStream());
		oos.writeObject(mess);
	}
}
