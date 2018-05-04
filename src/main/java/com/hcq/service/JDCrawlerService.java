package com.hcq.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.hcq.bean.Product;
import com.hcq.util.DataCapture;
import com.hcq.util.JsonToClass;
import com.hcq.util.JsonToStr;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class JDCrawlerService  extends Thread implements Runnable  {
	public void run(){
		List<Product> list;
		try {
			list = getMsg("https://auction.jd.com/getJudicatureList.html?childrenCateId=12728&provinceId=2");
			for (int i = 0; i < list.size(); i++) {
				findProduct(list.get(i));
			}
//			add(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<Product> getMsg(String url) throws Exception{
		JsonToStr t = new JsonToStr();
		JsonToClass jtc = new JsonToClass();
		List<Product> list = new ArrayList<Product>();
		//配置文件路径
		String basePath = getClass().getResource("/").getPath()+"/format.properties";
		InputStream in = new BufferedInputStream(new FileInputStream(new File(basePath)));  
        Properties prop = new Properties(); 
        //解决中文乱码
        prop.load(new InputStreamReader(in, "utf-8"));
        DataCapture dc = new DataCapture();
        int total_page = 2;
		// 采集日
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String collection_date = sdf.format(date);
		Document doc = dc.getDoc(url);
		
		//页数
		for (int page=1; page <= 10; page++) {
		String new_url = url + "&Page=" + page;
		doc = dc.getDoc(new_url);
		  
        JSONArray jsonArray = jtc
			.getProductClass(new_url,"ls");
        //计算总页数
        JSONArray productTotal = jtc.getProductTotal(new_url);
        double pro_total = (Double)(productTotal.get(0));
        total_page = (int) Math.ceil(pro_total/40);

        String url_0 = "http://paimai.jd.com/";
		for (int i = 0; i < jsonArray.size(); i++) {
			Object o = jsonArray.get(i);
			JSONObject jsonObject = JSONObject.fromObject(o);
			// 获得商品
			Product pro = (Product) JSONObject
					.toBean(jsonObject, Product.class);
			
			// 采集日
			pro.setCollection_date(collection_date);
			// 进入商品详情
			// 商品详情url
			String pro_href = url_0 + pro.getId();
			// 房源详情
			pro.setHouse_detail(pro_href);
			// 获得参数1的接口
			String url_getAlbumId = url_0
					+ "json/current/initBreadCrumb.html?paimaiId="
					+ pro.getId();

			// 获取商品拍卖状态
//			String url_getType = "https://paimai.jd.com/services/currentList.action?paimaiIds="+ pro.getId()+"&callback=a&_=2" ;
//			String products = t.getJson(url_getType);
//			String replace1 = products.replace("a([", "");
//			String replace2 = replace1.replace("])", "");
//			JSONObject js = JSONObject.fromObject(replace2);
//			String auctionType = js.getString("auctionStatus");
			String url_getType = "https://paimai.jd.com/json/current/englishquery?paimaiId="+pro.getId()+"&start=0&end=9";
			JSONObject typeobj = jtc.getJSONObj(url_getType);
			String orderStatus = typeobj.getString("orderStatus");
			String currentPriceStr = typeobj.getString("currentPriceStr");
			
			String displayStatus = jsonObject.getString("displayStatus");
			
			if(displayStatus=="6.0"){
				pro.setResult("缓拍");
			}else if(orderStatus=="-1"){
				pro.setResult("流拍");
			}else if(currentPriceStr!=null){
				currentPriceStr = currentPriceStr.replace(",", "");
				currentPriceStr = currentPriceStr.substring(0,currentPriceStr.length()-4);
				pro.setResult(currentPriceStr+"万");
			}
			
			
			// 获得商品详情页面
			Document doc1 = Jsoup.connect(pro_href).get();
			String html_li = doc1.getElementsByTag("li").text();
			//加价幅度
//			String premium_0 = dc.reg(html_li, prop.getProperty("premium_jd"));
//			int premium = -1;
//			if(premium_0!=null){
//				premium = Integer.valueOf(premium_0.substring(7,premium_0.length()-5));
//				pro.setPremium(premium);
//			}
//			int cprice = Integer.valueOf(pro.getCurrentPriceCN());
//			//溢价率
//			double premium_rate = -1;
//			if( cprice != -1 && premium != -1){
//				premium_rate = premium/(double)cprice;
//				DecimalFormat df = new DecimalFormat("0.0000");
//				pro.setPremium_rate(df.format(premium_rate));
//			}
			// 地址
			String addr = doc1.getElementById("paimaiAddress").text();
			pro.setAddr(addr);

			// 拍卖次数
			String title = doc1.getElementsByClass("pm-status-no-sign").text();
			String currentNum = dc.reg(title, "【.*?】");
			String cn_new = null;
			if (null != currentNum){
				currentNum = currentNum.substring(1, currentNum.length() - 1);
				cn_new = dc.reg(currentNum, prop.getProperty("countNum_new"));
				if(cn_new!=null){
					pro.setCountNum(cn_new.substring(1,cn_new.length()-1)+"拍");
				}else{
					pro.setCountNum(currentNum);
				}
				title = title.substring(currentNum.length()+2,title.length());
				title = dc.reg(title, prop.getProperty("title"));
				if(title.indexOf("上海")!=-1){
    				title = title.substring(title.indexOf("上海"),title.length());
    			}
				pro.setTitle(title);
			}else{
				if(title.indexOf("上海")!=-1){
    				title = title.substring(title.indexOf("上海"),title.length());
    			}
				pro.setTitle(title);
			}

			String _addr = dc.reg(title, prop.getProperty("addr_test"));
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
				if(_addr.length()>30){
					addr1 = _addr.substring(0,30);
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
			
			//区域
			String city = pro.getCity();
			if(city!=null){
				if(city.length()==3){
					pro.setCity(city.substring(0,city.length()-1));
				}else{
					pro.setCity(city.substring(0,city.length()-2));
				}
			}else{
				if(district!=null){
    				pro.setCity(district.substring(0,2));
    			}
			}
			// albumId赋值
			Map<String, Object> alb_map = jtc.getMsg(url_getAlbumId);
			pro.setAlbumId((Double) alb_map.get("albumId"));
			DecimalFormat decimalFormat = new DecimalFormat(
					"###################.###########");
			String albumId = decimalFormat.format(pro.getAlbumId());

			// vendorId赋值
			pro.setVendorId((doc1.getElementById("vendorId").val()));

			// 获得法院
			String url_getShop = url_0
					+ "/json/current/queryVendorInfo.html?vendorId="
					+ pro.getVendorId() + "&albumId=" + albumId + "&paimaiId="
					+ pro.getId();
			Map<String, Object> shop_map = jtc.getMsg(url_getShop);
			
			// 获得拍卖公告
			String url_getHtml = "http://paimai.jd.com/json/current/queryAlbumAnnouncement?albumId="
					+ albumId;
			Map<String, Object> html_map = jtc.getMsg(url_getHtml);
			Document $notice_html = Jsoup.parseBodyFragment((String) html_map.get("content"));
			
			// 获得拍卖标的（获取住房类型）
			Elements spans = $notice_html.getElementsByTag("p");
			String html = spans.text();
			// 根据住房类型字段表进行匹配
			pro.setType(dc.reg(html, prop.getProperty("type")));
			String build_area = dc.reg(html, prop.getProperty("build_area"));
			if (null != build_area)
				pro.setBuild_area(build_area.substring(5,
						build_area.length()));
			String bond = dc.reg(html, prop.getProperty("bond"));
			if (null != bond)
				pro.setBond(bond.substring(4, bond.length()));
			pro.setStructure(dc.reg(html, prop.getProperty("structure")));
			String tel = dc.reg(html, prop.getProperty("tel"));
			if (null != tel)
				pro.setTel(tel.substring(5, tel.length() - 1));
			pro.setShopName((String) shop_map.get("shopName"));
			pro.setIntermediary("京东");
			System.out.println(pro);
			list.add(pro);
		}
		}
        
		return list;  
	}
	
	public boolean empty(){
		Db.update("delete from product_data");
		return true;
	}
	
	public boolean add(List<Product> list){
		for (int i = 0; i < list.size(); i++) {
			Record product = new Record()
			.set("city", list.get(i).getCity())
			.set("addr", list.get(i).getTitle())
			.set("community_name", list.get(i).getCommunity_name())
			.set("plate", list.get(i).getPlate())
			.set("house_type",list.get(i).getHouse_type())
			.set("type", list.get(i).getType())
			.set("structure", list.get(i).getStructure())
			.set("floor", list.get(i).getFloor())
			.set("build_area", list.get(i).getBuild_area())
			.set("completionDate", list.get(i).getCompletionDate())
			.set("degree", list.get(i).getDegree())
			.set("currentPriceCN", list.get(i).getCurrentPriceCN())
			.set("assessmentPriceCN", list.get(i).getAssessmentPriceCN())
			.set("bond", list.get(i).getBond())
			.set("marketPrice", list.get(i).getMarketPrice())
			.set("section", list.get(i).getSection())
			.set("startDate", list.get(i).getStartDate())
			.set("endDate", list.get(i).getEndDate())
			.set("collection_date", list.get(i).getCollection_date())
			.set("lease", list.get(i).getLease())
			.set("taxation", list.get(i).getTaxation())
			.set("shopName", list.get(i).getShopName())
			.set("intermediary", list.get(i).getIntermediary())
			.set("countNum", list.get(i).getCountNum())
			.set("tel", list.get(i).getTel())
			.set("around", list.get(i).getAround())
			.set("list_pic", list.get(i).getList_pic())
			.set("house_pic", list.get(i).getHouse_pic())
			.set("taxation_num", list.get(i).getTaxation_num())
			.set("commission", list.get(i).getCommission())
			.set("cost", list.get(i).getCost())
			.set("result", list.get(i).getResult())
			.set("is_choice", list.get(i).getIs_choice())
			.set("house_detail", list.get(i).getHouse_detail())
			.set("house_theme", list.get(i).getHouse_theme())
			.set("bidCount", list.get(i).getBidCount())
			.set("premium", list.get(i).getPremium())
			.set("premium_rate", list.get(i).getPremium_rate())
			;
			Db.save("product_data","id", product);
		}
		System.out.println("京东完成");
		return true;
	}

	public boolean addPro(Product p){
			Record product = new Record()
			.set("city", p.getCity())
			.set("addr", p.getTitle())
			.set("community_name", p.getCommunity_name())
			.set("plate", p.getPlate())
			.set("house_type",p.getHouse_type())
			.set("type", p.getType())
			.set("structure", p.getStructure())
			.set("floor", p.getFloor())
			.set("build_area", p.getBuild_area())
			.set("completionDate", p.getCompletionDate())
			.set("degree", p.getDegree())
			.set("currentPriceCN", p.getCurrentPriceCN())
			.set("assessmentPriceCN", p.getAssessmentPriceCN())
			.set("bond", p.getBond())
			.set("marketPrice", p.getMarketPrice())
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
			.set("house_detail", p.getHouse_detail())
			.set("house_theme", p.getHouse_theme())
			.set("bidCount", p.getBidCount())
			.set("premium", p.getPremium())
			.set("premium_rate", p.getPremium_rate())
			;
			Db.save("product_data","id", product);
		return true;
	}
	
	public void findProduct(Product p){
		List<Record> pro_list = Db.find("select count(*) from product_data where house_detail = ? and startDate = ? and endDate = ?",p.getHouse_detail(),p.getStartDate(),p.getEndDate());
		if(pro_list.size()!=0){
			Db.update("update product_data set result = ? where house_detail = ? and startDate = ? and endDate = ?",p.getResult(),p.getHouse_detail(),p.getStartDate(),p.getEndDate());
		}else{
			addPro(p);
		}
	}
}
