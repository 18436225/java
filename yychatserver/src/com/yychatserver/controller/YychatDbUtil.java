package com.yychatserver.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class YychatDbUtil {
			public static final String MYSQLDRIVER="com.mysql.jdbc.Driver";
			public static final String URL="jdbc:mysql://127.0.0.1:3306/yychat";
			public static final String DBUSER="root";
			public static final String DBPASS="";
			
			
			//1、加载驱动程序
		public static void loadDriver(){
				try {
					Class.forName(MYSQLDRIVER);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			//2、连接数据库
		public static Connection getConnection(){
				loadDriver();
				Connection conn=null;
				try {
					conn=DriverManager.getConnection(URL,DBUSER,DBPASS);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return conn;
			}
		public static boolean seekUser(String userName){
			boolean seekUserResult=false;
			Connection conn=getConnection();
			String user_Register_Sql="select * from user where username=?";
			PreparedStatement ptmt=null;
			ResultSet rs=null;
			try {
				ptmt=conn.prepareStatement(user_Register_Sql);
				ptmt.setString(1, userName);
				rs=ptmt.executeQuery();
				seekUserResult=rs.next();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				closeDB(conn,ptmt,rs);
			}
			return seekUserResult;	
		}
		
		public static void addMessage(String sender,String receiver,String content){
			Connection conn=getConnection();
			//创建PreparedStatement对象，用来执行SQL语句,标准
			String message_Add_Sql="insert into message(sender,receiver,content,timestamp) values(?,?,?,?)";
			PreparedStatement ptmt=null;	
			ResultSet rs=null;
			try {
				ptmt = conn.prepareStatement(message_Add_Sql);
				ptmt.setString(1, sender);
				ptmt.setString(2, receiver);
				ptmt.setString(3, content);
				
				Date date=new Date();
				java.sql.Timestamp timestamp=new java.sql.Timestamp(date.getTime());			
				ptmt.setTimestamp(4, timestamp);
				//执行查询，返回结果集
				int count=ptmt.executeUpdate();//插入记录的条数
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeDB(conn,ptmt,rs);
			}			
		}
		
		public static int addRelation(String majorUser,String slaveUser,String relationType){
			int count=0;
			Connection conn=getConnection();
			String relation_Add_sql="insert into relation(majoruser,slaveuser,relationtype) values(?,?,?)";
			PreparedStatement ptmt=null;
			ResultSet rs=null;
			try {
				ptmt = conn.prepareStatement(relation_Add_sql);
				ptmt.setString(1, majorUser);
				ptmt.setString(2, slaveUser);
				ptmt.setString(3, relationType);
				
				count=ptmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				closeDB(conn, ptmt, rs);
			}
			return count;
		}
		
		public static void addUser(String userName,String passWord){
			Connection conn=getConnection();
			String user_add_Sql="insert into user(username,password,registertimestamp) values(?,?,?)";
			PreparedStatement ptmt=null;
			ResultSet rs=null;
			try {
				ptmt = conn.prepareStatement(user_add_Sql);
				ptmt.setString(1, userName);
				ptmt.setString(2, passWord);
				//java.util.Date date=new java.util.Date();
				Date date =new Date();
				java.sql.Timestamp timestamp=new java.sql.Timestamp(date.getTime());
				ptmt.setTimestamp(3,timestamp);
				//4、执行查询，返回结果集
				int count=ptmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				closeDB(conn, ptmt, rs);
			}
		}
		public static boolean loginValidate(String userName,String passWord){
				boolean loginSuccess=false;
				Connection conn=getConnection();
				//3、创建PreparedStatement对象，用来执行SQL语句
				String user_Login_Sql="select * from user where username=? and password=?";
				PreparedStatement ptmt=null;
				ResultSet rs=null;
				try {
					ptmt=conn.prepareStatement(user_Login_Sql);
					ptmt.setString(1, userName);
					ptmt.setString(2, passWord);
					//4、执行查询，返回结果集
					rs=ptmt.executeQuery();
					
					//5、根据结果集来判断是否能登录
					loginSuccess=rs.next();	
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					closeDB(conn,ptmt,rs);
				}
				System.out.println("loginSuccess为;"+loginSuccess);
				return loginSuccess;
		
		}
		
		public static boolean seekRelation(String majorUser,String slaveUser,String relationType){
			boolean seekRelationResult=false;
			Connection conn=getConnection();
			String seek_Relation_Sql="select * from relation where majoruser=? and slaveuser=? and relationtype=?";
			PreparedStatement ptmt=null;
			ResultSet rs=null;
			try {
				ptmt=conn.prepareStatement(seek_Relation_Sql);
				ptmt.setString(1, majorUser);
				ptmt.setString(2, slaveUser);
				ptmt.setString(3, relationType);
				rs=ptmt.executeQuery();
				seekRelationResult=rs.next();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				closeDB(conn,ptmt,rs);
			}
			
			return seekRelationResult;
			
		}
		
		public static String getFriendString(String userName){
			Connection conn=getConnection();
			String friend_Relation_Sql="select * from relation where majoruser=? and relationtype='1'";
			PreparedStatement ptmt=null;
			ResultSet rs=null;
			String friendString="";
			try {
				ptmt=conn.prepareStatement(friend_Relation_Sql);
				ptmt.setString(1, userName);
				rs=ptmt.executeQuery();
				while(rs.next()){                                 
					friendString=friendString+rs.getString("slaveuser")+" ";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				closeDB(conn,ptmt,rs);
			}
			return friendString;

		}
		public static void closeDB(Connection conn,PreparedStatement ptmt,ResultSet rs){
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ptmt!=null){
				try {
					ptmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
}