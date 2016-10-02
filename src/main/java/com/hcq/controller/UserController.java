package com.hcq.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.hcq.interceptor.AuthInterceptor;
import com.hcq.service.UserService;
import com.hcq.interceptor.LoginValidator;

public class UserController extends Controller{
	private UserService userService = new UserService();
	public void index(){}
	@Before(LoginValidator.class)
	public void login(){
		//getPara即 request.getParameter
		String username = getPara("username");
		String password = getPara("password");
		boolean loginCheck = userService.login(username,password);
		if(loginCheck){
			renderJson("10000");
			getSession().setAttribute("flag",true);
		}
		else
			renderJson("10001");
	}
	public void register(){
		String username = getPara("username");
		String password = getPara("password");
		boolean result = userService.add(username,password);
		if(result)
			renderJson("10010");
		else
			renderJson("10011");
	}
	@Before(AuthInterceptor.class)
	public void show(){
		renderJsp("../user.jsp");
	}
	public void image(){
		try{
			getFile(getPara("img"),"UTF-8");
			renderJson("20010");
		} catch (Exception e){
			renderJson("20012");
		}
	}
}
