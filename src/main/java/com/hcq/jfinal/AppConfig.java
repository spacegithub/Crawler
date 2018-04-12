package com.hcq.jfinal;

import com.hcq.bean.Prodoct_jd;
import com.hcq.bean.User;
import com.hcq.controller.IndexController;
import com.hcq.controller.UserController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;

public class AppConfig extends JFinalConfig{

	
	//配置 JFinal 常量值
	@Override
	public void configConstant(Constants constants) {
		//默认编码
		constants.setEncoding("UTF-8");
		//启用开发模式
		constants.setDevMode(true);
		//视图类型，支持 JSP、FreeMarker、Velocity 三种常用视图
		constants.setViewType(ViewType.JSP);
	}

	@Override
	public void configHandler(Handlers handlers) {
		
	}

	@Override
	public void configInterceptor(Interceptors interceptors) {
		//这里用于配置全局的拦截器，对所有请求进行拦截
		// 添加控制层全局拦截器
		//interceptors.addGlobalActionInterceptor(new GlobalActionInterceptor());
		// 添加业务层全局拦截器
		//interceptors.addGlobalServiceInterceptor(new GlobalServiceInterceptor());
	}

	@Override
	public void configPlugin(Plugins plugins) {
		//这里启用Jfinal插件
		//PropKit 工具类用来操作外部配置文件。PropKit 可以极度方便地在系统任意时空使用
		 PropKit.use("Jdbc.properties");
			final String URL =PropKit.get("jdbcUrl");
			final String USERNAME = PropKit.get("user");
			final String PASSWORD =PropKit.get("password");
			final Integer INITIALSIZE = PropKit.getInt("initialSize");
			final Integer MIDIDLE = PropKit.getInt("minIdle");
			final Integer MAXACTIVEE = PropKit.getInt("maxActivee");
			//淘宝的数据库连接池
			DruidPlugin druidPlugin = new DruidPlugin(URL,USERNAME,PASSWORD);
			druidPlugin.set(INITIALSIZE,MIDIDLE,MAXACTIVEE);
			druidPlugin.setFilters("stat,wall");
			plugins.add(druidPlugin);
			
			//添加Model类和数据库表的映射。user指的是表名，userid指的是主键
			ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(druidPlugin);
			activeRecordPlugin.addMapping("user","userid", User.class);
			plugins.add(activeRecordPlugin);
	}

	
	//配置 JFinal 访问路由
	//如下 将“/user”映射到UserController这只控制器
	//通 过 以 下 的 配 置 ，http://localhost/项目名/user 将 访 问 HelloController.index() 方法，
	//而http://localhost/项目名/user/methodName 将访问到 HelloController.methodName()方法。
	//restful风格
	@Override
	public void configRoute(Routes routes) {
		routes.add("/", IndexController.class);
		routes.add("/user", UserController.class);
	}
}
