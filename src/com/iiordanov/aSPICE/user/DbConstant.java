package com.iiordanov.aSPICE.user;

public class DbConstant {
  public static final String createusertable="create table IF NOT EXISTS usermsg(username varchar(30) primary key,password varchar(30),hostip varchar(30), managerip varchar(30),port varchar(30),updatetime INTEGER)";
  public static final String insert(String username,String password,String kdpPort,String HostIp){
	  return String.format("insert into userDatabase(username,password,hostip,port,updatetime)", username,password,kdpPort,HostIp,System.currentTimeMillis());
  }
  public static String update(String username,String password,String kdpPort,String hostIp){
	  return String.format("update userDatabase set password=%s,hostip=%s,port=%s,updatetime=%s where username=%s", password,hostIp,kdpPort,System.currentTimeMillis(),hostIp);
  }
}
