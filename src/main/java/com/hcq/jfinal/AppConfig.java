package com.hcq.jfinal;

import cn.dreampie.quartz.QuartzPlugin;

import com.hcq.bean.Product;
import com.hcq.bean.Product_tb;
import com.hcq.controller.CrawlerController;
import com.hcq.controller.IndexController;
import com.hcq.controller.ProductController;
import com.hcq.service.GPCrawlerService;
import com.hcq.service.JDCrawlerService;
import com.hcq.service.TBCrawlerService;
import com.hcq.util.SchedulerPlugin;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;

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
			
			//添加Model类和数据库表的映射。product_data指的是表名，id指的是主键
			ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(druidPlugin);
			activeRecordPlugin.addMapping("product_data","id", Product.class);
			activeRecordPlugin.addMapping("product_data","id", Product_tb.class);
			plugins.add(activeRecordPlugin);
			/*配置定时任务*/  
//			SchedulerPlugin inventoryQuartzTask = new SchedulerPlugin("quartz.properties");
//			plugins.add(inventoryQuartzTask);
			
//			 QuartzPlugin quartz = new QuartzPlugin();
//		     quartz.setJobs("quartzJob.properties");
//		     plugins.add(quartz);
			
			 //配置任务调度插件 
			  Cron4jPlugin cp = new Cron4jPlugin(); 
			  cp.addTask("00 20 * * *", new JDCrawlerService()); 
			  cp.addTask("00 20 * * *", new GPCrawlerService()); 
			  cp.addTask("00 20 * * *", new TBCrawlerService()); 
//			  cp.addTask("20 10 * * *", new CrawlerController()); 
			  plugins.add(cp); 
			
	}

	
	//配置 JFinal 访问路由
	//如下 将“/user”映射到UserController这只控制器
	//通 过 以 下 的 配 置 ，http://localhost/项目名/user 将 访 问 HelloController.index() 方法，
	//而http://localhost/项目名/user/methodName 将访问到 HelloController.methodName()方法。
	//restful风格
	@Override
	public void configRoute(Routes routes) {
		routes.add("/", IndexController.class);
		routes.add("/product", ProductController.class);
	}

	@Override
	public void configEngine(Engine me) {
		// TODO Auto-generated method stub
		
	}
}
