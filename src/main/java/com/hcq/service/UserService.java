package com.hcq.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class UserService {
	public boolean add(String username,String password){
		String SQL = "SELECT userid FROM user WHERE username =?";
		Integer result = Db.queryFirst(SQL, username);
		if(result==null){
			Record user = new Record().set("username", username).set("password", password);
			Db.save("user", user);
			return true;
		}
		return false;
	}
	public boolean login(String username,String password){
		String SQL = "SELECT userid FROM user WHERE username =? and password=?";
		Integer result = Db.queryFirst(SQL, username, password);
		if(result!=null)
			return true;
		else return false;
	}
}
