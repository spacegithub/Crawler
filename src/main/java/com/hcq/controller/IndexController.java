package com.hcq.controller;

import com.jfinal.core.Controller;

public class IndexController extends Controller{
	public void index(){
		renderJsp("index.jsp");
	}
	public void a(){
		System.out.println("2222");
	}
}
