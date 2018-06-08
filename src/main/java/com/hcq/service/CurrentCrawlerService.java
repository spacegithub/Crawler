package com.hcq.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.google.gson.Gson;
import com.hcq.bean.Product;
import com.hcq.bean.Product_tb;
import com.hcq.util.DataCapture;
import com.hcq.util.JsonToClass;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class CurrentCrawlerService extends Thread implements Runnable {
	public void run(){
		try {
			List<Record> list = Db.find("select * from product_data where to_days(startDate) = to_days(now());");
			if(list.size()!=0){
				for (Record record : list) {
					System.out.println(record);
					getMsg(record);
				}
				System.out.println("淘宝完成");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getMsg(Record record) throws Exception {
		// 配置文件路径
		String basePath = getClass().getResource("/").getPath()
				+ "/format.properties";
		InputStream in = new BufferedInputStream(new FileInputStream(new File(
				basePath)));
		Properties prop = new Properties();
		// 解决中文乱码
		prop.load(new InputStreamReader(in, "utf-8"));
		String url = record.getStr("house_detail");

		String reg_jd = "/auction.jd.com/";
        String reg_tb = "/sf.taobao.com/";
        String reg_gp = "/www.gpai.net/";
        Pattern pat = Pattern.compile(reg_jd);
        Pattern pat1 = Pattern.compile(reg_tb);
        Pattern pat2 = Pattern.compile(reg_gp);
        Matcher mat = pat.matcher(url);
        Matcher mat1 = pat1.matcher(url);
		Matcher mat2 = pat2.matcher(url);
		boolean rs_jd = mat.find();
		boolean rs_tb = mat1.find();
		boolean rs_gp = mat2.find();
		
		if(rs_tb){
			getTBCurrent(prop,record);
		}else if(rs_jd){
			getJDCurrent(prop,record);
		}else if(rs_gp){
			getGPCurrent(prop,record);
		}
	}
	
	/**
	 * 公拍实时采集
	 * @throws IOException 
	 */
	public void getGPCurrent(Properties prop,Record record) throws IOException{
		String url = record.getStr("house_detail");
		DataCapture dc = new DataCapture();
		Product p = new Product();
		String href = url;
		//详情页面
		Document detail_doc = Jsoup.connect(href).timeout(120000).ignoreHttpErrors(true)  
		        .ignoreContentType(true).get();
		//当前价
		Elements cur_ele = detail_doc.select("#ItemPriceLine");
		String cprice = null;
		//如果有出价记录
		if(cur_ele.size()!=0){
			Elements b_ele = detail_doc.select(".red1");
			if(b_ele.size()!=0){
				cprice = b_ele.get(0).text();
				p.setCurrprice(cprice);
			}
		}else{
			// 如果没有记录 起拍价就为当前价
			String start_price = record.getStr("currentPriceCN");
			cprice = start_price;
			p.setCurrprice(cprice);
		}
		System.out.println("当前价:"+cprice);
		//竞拍次数
		Elements shu = detail_doc.select("span#html_Bid_Shu");
		String a = null;
		int count = 0;
		if(shu.size()!=0){
			a =shu.get(0).text();
			if(a!=null && a!="" && !a.equals(""))
			count = Integer.valueOf(a);
		}
		
		System.out.println("出价次数"+count);
		p.setBidCount(count);
		
//		Db.update("update product_data set result = ? where house_detail = ?",p.getResult(),p.getHouse_detail());
		System.out.println(p);
	}
	
	/**
	 * 京东实时采集
	 */
	public void getJDCurrent(Properties prop,Record record) throws IOException{
		String url = record.getStr("house_detail");
		DataCapture dc = new DataCapture();
		Product p = new Product();
	}
	
	/**
	 * 淘宝实时每日采集
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 */
	public void getTBCurrent(Properties prop,Record record) throws IOException{
		String url = record.getStr("house_detail");
		DataCapture dc = new DataCapture();
		// 获得商品
		Product_tb pro = new Product_tb();
		// url
		String url2 = url;
		Document doc3 = dc.getDoc(url2);
		Elements cp_ele = doc3.select(".pm-current-price");
		String ip = null;
		if(cp_ele.size()!=0)
		ip = cp_ele.get(0).text();
		
		// 当前价
		String nip = "";
		double cprice = -1;
		String substring = "";
		System.out.println("当前价:"+ip);
		if(ip!=null){
			ip=ip.replace(",", "");
			cprice = Integer.valueOf(ip)/10000;
			BigDecimal b = new BigDecimal(cprice);  
			cprice = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
			System.out.println(cprice+"万");
		}
		
		// 竞拍次数
		Elements shu = doc3.select("span.J_Record");
		String a = null;
		int count = 0;
		if(shu.size()!=0){
			a =shu.get(0).text();
			if(a!=null && a!="" && !a.equals(""))
			count = Integer.valueOf(a);
		}
		System.out.println("出价次数"+count);
		pro.setBidCount(count);
//		Db.update("update product_data set result = ? where house_detail = ?",pro.getResult(),pro.getItemUrl());
		System.out.println(pro);
		
		
	}

}
