package com.hcq.jfinal;



import com.hcq.bean.Product;
import com.hcq.bean.Product_tb;
import com.hcq.controller.CrawlerController;
import com.hcq.controller.IndexController;
import com.hcq.controller.ProductController;
import com.hcq.service.GPCrawlerService;
import com.hcq.service.JDCrawlerService;
import com.hcq.service.TBCrawlerService;
import com.hcq.service.CurrentCrawlerService;
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

	
	@Override
	public void configConstant(Constants constants) {
		constants.setEncoding("UTF-8");
		constants.setDevMode(true);
		constants.setViewType(ViewType.JSP);
	}

	@Override
	public void configHandler(Handlers handlers) {
		
	}

	@Override
	public void configInterceptor(Interceptors interceptors) {
	}

	@Override
	public void configPlugin(Plugins plugins) {
		 PropKit.use("Jdbc.properties");
			final String URL =PropKit.get("jdbcUrl");
			final String USERNAME = PropKit.get("user");
			final String PASSWORD =PropKit.get("password");
			final Integer INITIALSIZE = PropKit.getInt("initialSize");
			final Integer MIDIDLE = PropKit.getInt("minIdle");
			final Integer MAXACTIVEE = PropKit.getInt("maxActivee");
			DruidPlugin druidPlugin = new DruidPlugin(URL,USERNAME,PASSWORD);
			druidPlugin.set(INITIALSIZE,MIDIDLE,MAXACTIVEE);
			druidPlugin.setFilters("stat,wall");
			plugins.add(druidPlugin);
			
			ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(druidPlugin);
			activeRecordPlugin.addMapping("product_data","id", Product.class);
			activeRecordPlugin.addMapping("product_data","id", Product_tb.class);
			plugins.add(activeRecordPlugin);
//			SchedulerPlugin inventoryQuartzTask = new SchedulerPlugin("quartz.properties");
//			plugins.add(inventoryQuartzTask);
			
//			 QuartzPlugin quartz = new QuartzPlugin();
//		     quartz.setJobs("quartzJob.properties");
//		     plugins.add(quartz);
			
			  Cron4jPlugin cp = new Cron4jPlugin(); 
//			  cp.addTask("00 20 * * *", new JDCrawlerService()); 
//			  cp.addTask("01 20 * * *", new GPCrawlerService()); 
//			  cp.addTask("02 20 * * *", new TBCrawlerService()); 
//			  cp.addTask("20 10 * * *", new CrawlerController());
			  cp.addTask("30 18 * * *", new CurrentCrawlerService());
			  plugins.add(cp); 
			
	}

	
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
