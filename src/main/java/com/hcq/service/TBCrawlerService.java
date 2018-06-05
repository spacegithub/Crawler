package com.hcq.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.hcq.bean.Product;
import com.hcq.bean.Product_tb;
import com.hcq.util.DataCapture;
import com.hcq.util.JsonToClass;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class TBCrawlerService extends Thread implements Runnable {
	public void run(){
//		List<Product_tb> list;
		try {
			getMsg("https://sf.taobao.com/item_list.htm?category=50025969&city=%C9%CF%BA%A3");
//			for (int i = 0; i < list.size(); i++) {
//				findProduct(list.get(i));
//			}
//			add(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getMsg(String url) throws Exception {
		JsonToClass jtc = new JsonToClass();
		List<Product_tb> list = new ArrayList<Product_tb>();
		// 配置文件路径
		String basePath = getClass().getResource("/").getPath()
				+ "/format.properties";
		InputStream in = new BufferedInputStream(new FileInputStream(new File(
				basePath)));
		Properties prop = new Properties();
		// 解决中文乱码
		prop.load(new InputStreamReader(in, "utf-8"));
		DataCapture dc = new DataCapture();
		int total_page = 2;
		// 采集日
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String collection_date = sdf.format(date);
		Document doc = dc.getDoc(url);
		// 计算总页数
		total_page = Integer.valueOf(doc.getElementsByClass("page-total")
				.text());
		for (int page = 1; page <= total_page; page++) {
			String new_url = url + "&Page=" + page;
			doc = dc.getDoc(new_url);
			Gson gson = new Gson();

			Map<String, Object> map = new HashMap<String, Object>();
			Element script = doc.getElementById("sf-item-list-data");
			
			
			String data_0 = script.data().toString();
			System.out.println("data_00000000:" +data_0);
			String data = data_0.substring(15, data_0.length() - 11);
			System.out.println("data_11111111:" +data);
			map = gson.fromJson(data, map.getClass());
			
			JSONArray jsonArray = JSONArray.fromObject(map.get("data"));
			for (int i = 0; i < jsonArray.size(); i++) {
				Object o = jsonArray.get(i);
				JSONObject jsonObject = JSONObject.fromObject(o);
				// 获得商品
				Product_tb pro = (Product_tb) JSONObject.toBean(jsonObject,
						Product_tb.class);
				// url
				String url2 = pro.getItemUrl();
				String url3 = dc.reg(url2, prop.getProperty("url"));
				if(url3==null){
					url2 = "https:"+url2;
					pro.setItemUrl(url2);
				}else{
					pro.setItemUrl(url2);
				}
				System.out.println("url2:" + url2);
				Document doc3 = dc.getDoc(url2);
				Elements bid_f= doc3.select(".bid-fail");
				System.out.println("bid_f:" + bid_f.size());
				if(bid_f.size()!=0){
					System.out.println("re:"+bid_f.get(0).text());
					String first_re = bid_f.get(0).text();
					String re = dc.reg(bid_f.get(0).text(), prop.getProperty("re"));
					System.out.println(re);
					if(re!=null){
						pro.setResult(re);
					}else{
						if(first_re.indexOf("结束")!=-1){
							Elements sf_price = doc3.select("#sf-price");
							String res = sf_price.text();
							if(res.indexOf("元")!=-1){
								res = res.substring(3,res.indexOf("元"));
								res = res.replace(",", "");
								res = res.substring(0,res.length()-4);
								res = res + "万";
							}
						}
					}
				}
//				else{
//					Elements pp= doc3.select(".pm-current-price");
//					if(pp.size()!=0){
//						System.out.println("pp:"+pp.get(0).text());
//						if(pp.get(0).text()!=null){
//							String re_price = pp.get(0).text().replace(",", "");
//							pro.setResult(re_price.substring(0,re_price.length()-4)+"万");
//						}
//					}
//				}
				
				
				String ip = pro.getInitialPrice();
				String nip = "";
				int cprice = -1;
				String substring = "";
				if (ip != null) {
					if (ip.indexOf("E") > 0) {
						BigDecimal bd1 = new BigDecimal(ip);
						String nbd1 = bd1.toPlainString();
						if (nbd1.indexOf(".") > 0) {
							substring = nbd1.substring(0,nbd1.indexOf("."));
							cprice = Integer.valueOf(substring);
//							nip = substring.substring(0, substring.length() - 4);
							nip = substring.substring(0, substring.length() - 4)+"."+substring.substring( substring.length() - 4, substring.length());
						} else {
							cprice = Integer.valueOf(nbd1);
//							nip = nbd1.substring(0, nbd1.length() - 4);
							nip = nbd1.substring(0, nbd1.length() - 4)+"."+nbd1.substring(nbd1.length() - 4, nbd1.length());
						}
					} else {
						cprice = Integer.valueOf(ip.substring(0,
								ip.indexOf(".")));
//						nip = ip.substring(0, ip.length() - 6);
						nip = ip.substring(0, ip.length() - 6)+"."+ip.substring(ip.length() - 6, ip.length());
					}
//					pro.setInitialPrice(nip + "万");
					pro.setInitialPrice(nip);
				}
				String cp = pro.getConsultPrice();
				String ncp = "";
				if (cp != null) {
					if (cp.indexOf("E") > 0) {
						BigDecimal bd2 = new BigDecimal(cp);
						String nbd = bd2.toPlainString();
//						ncp = nbd.substring(0, nbd.length() - 4);
//						pro.setConsultPrice(ncp + "万");
						ncp = nbd.substring(0, nbd.length() - 4)+"."+nbd.substring(nbd.length() - 4, nbd.length());
						pro.setConsultPrice(ncp);
					} else {
						if(cp.length()>6){
//							ncp = cp.substring(0, cp.length() - 6);
//							pro.setConsultPrice(ncp + "万");
							ncp = cp.substring(0, cp.length() - 6)+"."+cp.substring(cp.length() - 6, cp.length());
							pro.setConsultPrice(ncp);
						}else if(cp=="0.0"){
							pro.setConsultPrice(cp);
						}
					}
				}
				pro.setIntermediary("淘宝");
				String details_url = pro.getItemUrl();
				Document detail_html = dc.getDoc(details_url);
				// 详情页面内元素
				String html = detail_html.getElementsByTag("td").text();
				String html_li = detail_html.getElementsByTag("li").text();
				String html_span = detail_html.getElementsByTag("span").text();
				String html_p = detail_html.getElementsByTag("p").text();
				String html_h1 = detail_html.getElementsByTag("h1").text();
				// 获取方法返回值
				Element div = detail_html.getElementById("J_ItemNotice");
				String dataFrom = div.attr("data-from");
				Document doc2 = dc.getDoc("https:" + dataFrom);
				String doc2_p = doc2.getElementsByTag("p").text();
				// 加价幅度
//				String premium_tb = dc
//						.reg(html, prop.getProperty("premium_tb"));
//				int premium = -1;
//				if (premium_tb != null) {
//					String ptb = premium_tb.substring(9,
//							premium_tb.length() - 2);
//					String ptb1 = ptb.replace(",", "");
//					premium = Integer.valueOf(ptb1);
//					pro.setPremium(premium);
//				}

//				// 溢价率
//				double premium_rate = -1;
//				if (cprice != -1 && premium != -1) {
//					premium_rate = premium / (double) cprice;
//					DecimalFormat df = new DecimalFormat("0.0000");
//					pro.setPremium_rate(df.format(premium_rate));
//				}

				// 获取保证金
				String bond = dc.reg(html, prop.getProperty("bond_tb"));
				String bond_1 = "";
				if (bond != null) {
					String bond_new = bond.substring(10, bond.length() - 2)
							.replace(",", "");
					if(bond_new.indexOf(".")>0){
						String bond_2 = bond_new
								.substring(0, bond_new.indexOf("."));
//						bond_1 = bond_2.substring(0,bond_2.length()-4);
						bond_1 = bond_2.substring(0,bond_2.length()-4)+"."+bond_2.substring(bond_2.length()-4,bond_2.length());
					}else{
//						bond_1 = bond_new
//								.substring(0, bond_new.length() - 4);
						bond_1 = bond_new.substring(0, bond_new.length() - 4)+"."+bond_new.substring(bond_new.length() - 4, bond_new.length());
					}
//					pro.setBond(bond_1 + "万");
					pro.setBond(bond_1);
				}
				String title = detail_html.title();
				if(title.indexOf("上海")!=-1){
					title = title.substring(title.indexOf("上海"),title.length());
				}
				pro.setTitle(title.substring(0,title.length()-13));
//    			String _addr = dc.reg(title.substring(7,title.length()), prop.getProperty("addr_test"));
    		
				// 拍卖次数
				String countNum = dc.reg(html_h1,
						prop.getProperty("countNum_tb"));
				String cn_new = null;
				if (countNum != null){
					countNum = countNum.substring(1,countNum.length() -1);
					cn_new = dc.reg(countNum, prop.getProperty("countNum_new"));
					if(cn_new!=null){
						pro.setCountNum(cn_new.substring(1,cn_new.length()-1)+"拍");
					}else{
						pro.setCountNum(countNum);
					}
				}
				// 执行法院
				String shopName = dc.reg(html_p, prop.getProperty("shopName"));
				if (shopName != null)
					pro.setShopName(shopName.substring(6, shopName.length()));
				// 咨询电话
				String tel = dc.reg(doc2_p, prop.getProperty("tel"));
				if (tel != null && tel.length() > 6)
					pro.setTel(tel.substring(5, tel.length() - 2));
				// 房屋类型
				String type = dc.reg(doc2_p, prop.getProperty("type"));
				pro.setType(type);
				pro.setType2(dc.reg(doc2_p, prop.getProperty("type2")));
				// 建筑面积
				String build_area = dc.reg(doc2_p,
						prop.getProperty("build_area"));
				if (build_area != null)
					pro.setBuild_area(build_area.substring(5,
							build_area.length()));
				// 房屋结构
				String structure = dc
						.reg(doc2_p, prop.getProperty("structure"));
				pro.setStructure(structure);
				// 采集日
				pro.setCollection_date(collection_date);
				int findProduct = findProduct(pro);
				if(findProduct!=0){
					Db.update("update product_data set result = ? where house_detail = ?",pro.getResult(),pro.getItemUrl());
				}else{
					getCityAAAA(title,pro, dc,prop,detail_html,jtc);
					addPro(pro);
				}
				
				System.out.println(pro);
//				list.add(pro);
			}
		}
		System.out.println("淘宝完成");
//		return list;
	}

	public boolean addPro(Product_tb p) {
			Record product = new Record().set("city", p.getCity())
					.set("addr", p.getTitle())
					.set("community_name", p.getCommunity_name())
					.set("plate", p.getPlate())
					.set("house_type", p.getHouse_type())
					.set("type", p.getType())
					.set("structure", p.getStructure())
					.set("floor", p.getFloor())
					.set("build_area", p.getBuild_area())
					.set("completionDate", p.getCompletionDate())
					.set("degree", p.getDegree())
					.set("currentPriceCN", p.getInitialPrice())
					.set("assessmentPriceCN", p.getConsultPrice())
					.set("bond", p.getBond())
					.set("marketPrice", "0")
					.set("section", p.getSection())
					.set("startDate", p.getStartDate())
					.set("endDate", p.getEndDate())
					.set("collection_date", p.getCollection_date())
					.set("lease", p.getLease())
					.set("taxation", p.getTaxation())
					.set("shopName", p.getShopName())
					.set("intermediary", p.getIntermediary())
					.set("countNum", p.getCountNum())
					.set("tel", p.getTel())
					.set("around", p.getAround())
					.set("list_pic", p.getList_pic())
					.set("house_pic", p.getHouse_pic())
					.set("taxation_num", p.getTaxation_num())
					.set("commission", p.getCommission())
					.set("cost", p.getCost())
					.set("result", p.getResult())
					.set("is_choice", p.getIs_choice())
					.set("house_detail", p.getItemUrl())
					.set("house_theme", p.getHouse_theme())
					.set("bidCount", p.getBidCount())
					.set("premium", p.getPremium())
					.set("premium_rate", p.getPremium_rate());
			Db.save("product_data", "id", product);
			System.out.println("添加成功");
		return true;
	}
	public void getCityAAAA(String title,Product_tb pro,DataCapture dc,Properties prop,Document detail_html,JsonToClass jtc){
		String[] _addrs = title.split("室");
		String _addr = "";
		if(_addrs.length!=1){
			_addr = _addrs[0];
		}else{
			if(_addrs[0].indexOf("、")!=-1){
				_addr = _addrs[0].substring(0,_addrs[0].indexOf("、"));
			}else{
				_addr = title.substring(0,title.length()-13);
			}
		}
		String get_Lng_Lat = "";
		if(_addr==null){
			String title1 = "";
			System.out.println("title="+title);
			if(title.length()>30){
				title1 = title.substring(0,30);
				get_Lng_Lat = "http://api.map.baidu.com/geocoder/v2/?address="+title1+"&output=json&ak=lYMpqrGu4iT9wWNGnzjAnGDTqHjkfCH2";	
			}else{
				get_Lng_Lat = "http://api.map.baidu.com/geocoder/v2/?address="+title+"&output=json&ak=lYMpqrGu4iT9wWNGnzjAnGDTqHjkfCH2";	
			}
		}else{
			String addr1 = "";
			System.out.println("_addr="+_addr);
			if(_addr.length()>20){
				addr1 = _addr.substring(0,20);
				addr1.replace("%", "");
				get_Lng_Lat = "http://api.map.baidu.com/geocoder/v2/?address="+addr1+"&output=json&ak=lYMpqrGu4iT9wWNGnzjAnGDTqHjkfCH2";
			}else{
				get_Lng_Lat = "http://api.map.baidu.com/geocoder/v2/?address="+_addr+"&output=json&ak=lYMpqrGu4iT9wWNGnzjAnGDTqHjkfCH2";
			}
		}
		JSONObject resultJSONObj = jtc.getJSONObj(get_Lng_Lat);
		//取出location元素
		String lat = "";
		String lng = "";
		if(resultJSONObj!=null){
			JSONObject resultJSON = resultJSONObj.getJSONObject("result");
			System.out.println(resultJSON);
		if(!resultJSON.isNullObject()){
			JSONObject locationObj = resultJSON.getJSONObject("location"); 
			if(locationObj!=null){
				//纬度
				lat = locationObj.getString("lat");
				//经度
				lng = locationObj.getString("lng");
			}
		}
		}
		String getAddrUrl = "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location="+lat+","+lng+"&output=json&pois=1&ak=lYMpqrGu4iT9wWNGnzjAnGDTqHjkfCH2";
		JSONObject addrJSONObj = jtc.getJSONObj(getAddrUrl);
		JSONObject addrJSON = addrJSONObj.getJSONObject("result");
		String district = null;
		if(!addrJSON.isNullObject()){
			JSONObject addressComponent = addrJSON.getJSONObject("addressComponent");
			district = addressComponent.getString("district");
			//所在小区
			JSONArray poiRegionsArray = addrJSON.getJSONArray("poiRegions");
			if(poiRegionsArray.size()!=0){
				String community_name1 = poiRegionsArray.getJSONObject(0).getString("name");
				pro.setCommunity_name(community_name1);
			}
		}
		
		
		// 区域
		String city = dc.reg(detail_html.title(),
				prop.getProperty("city"));
		if(city!=null){
			pro.setCity(city);
		}else{
			if(district!=null){
				pro.setCity(district.substring(0,2));
			}
		}
	}
	
	public int findProduct(Product_tb p){
		List<Record> pro_list = Db.find("select 1 from product_data where house_detail = ?",p.getItemUrl());
		return pro_list.size();
		
	}
}
