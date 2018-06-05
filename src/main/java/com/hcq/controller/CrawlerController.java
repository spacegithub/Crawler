package com.hcq.controller;

import it.sauronsoftware.cron4j.Scheduler;

import java.util.List;





import com.jfinal.core.Controller;
import com.hcq.bean.Product;
import com.hcq.bean.Product_tb;
import com.hcq.service.GPCrawlerService;
import com.hcq.service.JDCrawlerService;
import com.hcq.service.TBCrawlerService;

public class CrawlerController extends Controller implements Runnable{
	private GPCrawlerService gpcs = new GPCrawlerService();
	private JDCrawlerService jdcs = new JDCrawlerService();
	private TBCrawlerService tbcs = new TBCrawlerService();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		gpcs.start();
		jdcs.start();
		tbcs.start();
	}

}
