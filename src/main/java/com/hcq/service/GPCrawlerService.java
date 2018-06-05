package com.hcq.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hcq.bean.Product;
import com.hcq.util.DataCapture;
import com.hcq.util.JsonToClass;
import com.hcq.util.JsonToStr;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class GPCrawlerService extends Thread implements Runnable {
	
	public void run(){
		List<Product> list;
		try {
			getMsg("http://s.gpai.net/sf/search.do?cityNum=31&at=376");
//			for (int i = 0; i < list.size(); i++) {
//				findProduct(list.get(i));
//			}
//			add(list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public List<Product> getMsg(String url) throws IOException{
		JsonToClass jtc = new JsonToClass();
		//配置文件路径
		String basePath = getClass().getResource("/").getPath()+"/format.properties";
		InputStream in = new BufferedInputStream(new FileInputStream(new File(basePath)));  
        Properties prop = new Properties(); 
        //解决中文乱码
        prop.load(new InputStreamReader(in, "utf-8"));  
        List<Product> list = new ArrayList<Product>();
        String reg_jd = "/auction.jd.com/";
        String reg_tb = "/sf.taobao.com/";
        String reg_gp = "/s.gpai.net/";
        Pattern pat = Pattern.compile(reg_jd);
        Pattern pat1 = Pattern.compile(reg_tb);
        Pattern pat2 = Pattern.compile(reg_gp);
        Matcher mat = pat.matcher(url);
        Matcher mat1 = pat1.matcher(url);
		Matcher mat2 = pat2.matcher(url);
		boolean rs_jd = mat.find();
		boolean rs_tb = mat1.find();
		boolean rs_gp = mat2.find();
		int total_page = 2;
		// 采集日
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DataCapture dc = new DataCapture();
		String collection_date = sdf.format(date);
		Document doc = dc.getDoc(url);
		
		for (int page=1; page <= total_page; page++) {
			String new_url = url + "&Page=" + page;
			System.out.println(new_url);
			doc = dc.getDoc(new_url);
			//计算总页数
        	String doc_html_label = doc.getElementsByTag("label").text();
			String page_text = dc.reg(doc_html_label,prop.getProperty("page"));
			if(page_text!=null)
			total_page = Integer.valueOf(page_text.substring(1,page_text.length()-1));
			
        	Elements as = doc.getElementsByTag("a");
        	Elements lis = doc.select("ul.main-col-list > li");
        	List<String> bidArr = new ArrayList<String>();
        	List<String> startDateArr = new ArrayList<String>();
        	int k = 0;
        	int num = 0;
        	for (Element element1 : lis) {
        		String bidCount = dc.reg(element1.select("div.fl").text(), prop.getProperty("bidCount"));
        		Elements infos = element1.select("div.gpai-infos");
        		startDateArr.add(infos.get(0).child(4).text());
        		bidArr.add(bidCount.substring(0,bidCount.length()-4));
        	}
        	for (Element element : as) {
        		Product p = new Product();
        		p.setIntermediary("公拍");
        		String href = element.attr("href");
        		//房源详情
        		p.setHouse_detail(href);
        		pat = Pattern.compile("item.do");
        		pat2 = Pattern.compile("item2.do");
        		mat = pat.matcher(href);
        		mat2 = pat2.matcher(href);
        		boolean rs = mat.find();
        		boolean rs2 = mat2.find();
        		if(rs || rs2){
        			if(k==0){
        				String ids = dc.reg(href,prop.getProperty("id"));
        				ids=ids.substring(3,ids.length());
	        			long id = Long.parseLong(ids);
	        			//详情页面
	        			Document detail_doc = Jsoup.connect(href).timeout(120000).ignoreHttpErrors(true)  
	                            .ignoreContentType(true).get();
	        			
	        			Elements results = detail_doc.select("span#ItemSecondCount");
	        			
	        			//详情页面内元素
	        			String html = detail_doc.getElementsByTag("td").text();
	        			String html_li = detail_doc.getElementsByTag("li").text();
	        			String html_span = detail_doc.getElementsByTag("span").text();
	        			String html_p = detail_doc.getElementsByTag("p").text();
	        			//获取数据
	        			String assessmentPriceCN = dc.reg(html, prop.getProperty("assessmentPriceCN"));
	        			String countNum = dc.reg(html, prop.getProperty("countNum"));
	        			String bond = dc.reg(html, prop.getProperty("bond_gp"));
	        			String shopName = dc.reg(html, prop.getProperty("shopName"));
	        			String taxation = dc.reg(html,prop.getProperty("taxation"));
	        			if(results.size()!=0){
	        				System.out.println("aaaa:"+results.get(0).text());
	        				if(results.get(0).text()!=null){
	        					System.out.println(results.get(0).text());
	        					System.out.println(results.get(0).text().equals("成交"));
	        					if(results.get(0).text().equals("成交")){
	        						Elements de = detail_doc.getElementsByClass("d-m-price");
	        						if(de.size()!=0){
	        							String deprice = dc.reg(de.get(0).text(), prop.getProperty("deprice"));
	        							if(deprice!=null){
	        								deprice = deprice.substring(4,deprice.length()-1);
	        								p.setResult(deprice.substring(0,deprice.length()-4)+"万");
	        							}
	        						}
	        					}else{
	        						p.setResult(results.get(0).text());
	        					}
	        				}
	        			}else{
	        				String getresult_url = "http://www.gpai.net/sf/Item_Ajax.do?action=ITEMAREA&Web_Item_ID="+ids;
	        				Document result_doc = dc.getDoc(getresult_url);
	        				String re_html_span = result_doc.getElementsByTag("span").text();
	        				String result = dc.reg(re_html_span, prop.getProperty("result"));
	        				if(result!=null){
	        					System.out.println(result);
	        					p.setResult(result);
	        				}
	        			}
	        			//加价幅度
//	        			String premium_0 = dc.reg(html, prop.getProperty("premium"));
//	        			int premium = -1;
//	        			if(premium_0!=null){
//	        				premium = Integer.valueOf(premium_0.substring(5,premium_0.length()-1));
//	        				p.setPremium(premium);
//	        			}
	        			String startDate = "";
	        			//开始时间
	        			if(startDateArr.size()!=0) {
	        				if(startDateArr.get(num).length()>5)
	        				startDate = startDateArr.get(num).substring(5,startDateArr.get(num).length());
	        				if(startDate.indexOf(" ")!=-1)
	        				startDate = startDate.substring(0,startDate.indexOf(" "));
	        				startDate.replaceAll("-", "/");
	        				p.setStartDate(startDate);
	        			}
	        			//建筑面积
	        			String build_area = "";
	        			//竣工日期
	        			String completionDate = "";
	        			//总层数
	        			String floor_total = "";
	        			//当前楼层
	        			String floor_n = "";
	        			//楼层
	        			String floor = "";
	        			//楼盘名称
	        			String community_name = "";
	        			//房屋类型
	        			String type = "";
	        			//结束时间
	        			String endDate = "";
	        			//地址
	        			String addr = "";
	        			//租赁情况
	        			String lease = "";
	        			
	        			//标题
	        			String title = dc.reg(detail_doc.title(),prop.getProperty("title"));
	        			if(title.indexOf("上海")!=-1){
	        				title = title.substring(title.indexOf("上海"),title.length());
	        			}
	        			p.setTitle(title);
	        			
	        		
	        			//房屋结构
	        			String structure = dc.reg(detail_doc.html(),prop.getProperty("structure"));
	        			p.setStructure(structure);
	        			// 采集日
	        			p.setCollection_date(collection_date);
	        			//起拍价
	        			String currentPriceCN = detail_doc.getElementById("Price_Start").text();
	        			int cprice = -1;
	        			if(currentPriceCN != null){
	        				String replace = currentPriceCN.replace(",", "");
	        				if(replace.indexOf(".")!=-1){
	        					replace = replace.substring(0,replace.indexOf("."));
	        				}
	        				Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
	        				if( pattern.matcher(replace).matches()){
	        					cprice = Integer.valueOf(replace);
//	        					p.setCurrentPriceCN(replace.substring(0,replace.length()-4)+"万");
	        					p.setCurrentPriceCN(replace.substring(0,replace.length()-4)+"."+replace.substring(replace.length()-4,replace.length()));
	        				}
	        			}
	                		
	        			//溢价率
//	        			double premium_rate = -1;
//	        			if( cprice != -1 && premium != -1){
//	        				premium_rate = premium/(double)cprice;
//	        				DecimalFormat df = new DecimalFormat("0.0000");
//	        				p.setPremium_rate(df.format(premium_rate));
//	        			}
	        			p.setId(id);
	        			//咨询电话
	        			if(detail_doc.getElementsByClass("xq-cont202-tel").isEmpty()){
	        				Elements e = detail_doc.select("span.d-m-tel");
	        				if(e!=null)
	        				p.setTel(e.get(0).text().substring(7,e.get(0).text().length()));
	        			}else{
	        				p.setTel(detail_doc.getElementsByClass("xq-cont202-tel").text());
	        			}
	        			
	        			
	        			if(rs){//item.do
	        				//出价次数
		        			if(bidArr.size()!=0)
		        				p.setBidCount(Integer.valueOf(bidArr.get(num)));
	        				//拍卖结果
	        				String getresult_url = "http://www.gpai.net/sf/Item_Ajax.do?action=ITEMAREA&Web_Item_ID="+ids+"&r=0.48474475069989564";
	        				Document result_doc = dc.getDoc(getresult_url);
	        				 Elements xqs = result_doc.getElementsByClass("h33");
	        				 if(xqs.size()!=0){
	        					 String re_html_span = xqs.text();
	        					 p.setResult(re_html_span);
	        				 }
	        				
	        				//租赁情况
	        				lease = dc.reg(html,prop.getProperty("lease"));
	        				if(lease!=null)
	        					p.setLease(lease.substring(5,lease.length()-1));
	        				
	        				//建筑面积
	        				build_area = dc.reg(html_span,prop.getProperty("build_area"));
	        				System.out.println(build_area);
	        				if(build_area!=null){
	        					build_area = build_area.substring(4,build_area.length());
	        					if(build_area.indexOf("建筑面积")!=-1){
	        						if(build_area.indexOf("㎡")!=-1){
	        							p.setBuild_area(build_area.substring(5,build_area.indexOf("㎡")));
	        						}else if(build_area.indexOf("m2")!=-1){
	        							p.setBuild_area(build_area.substring(5,build_area.indexOf("m2")));
	        						}
	        					}
	        				}
	        					
	        				//竣工日期
	        				completionDate = dc.reg(html, prop.getProperty("completionDate"));
	        				if(completionDate!=null)
	        					p.setCompletionDate(completionDate.substring(6,completionDate.length()-1));
	        				
	        				//楼层
	        				floor_total = dc.reg(html, prop.getProperty("floor_total"));
	        				floor_n = dc.reg(html, prop.getProperty("floor_n"));
	        				if(floor_total!=null && floor_n!=null){
	        					floor = floor_n.substring(4,floor_n.length()-1) + "/" + floor_total.substring(5,floor_total.length()-1);
	        					p.setFloor(floor);
	        				}
	        				
	        				//楼盘名称
	        				community_name = dc.reg(html, prop.getProperty("community_name"));
	        				if(community_name!=null)
	        					p.setCommunity_name(community_name.substring(9,community_name.length()-1));
	        				
	        				//房屋类型
	        				type = dc.reg(html, prop.getProperty("type"));
		        			p.setType(type);
		        			p.setType2(dc.reg(html, prop.getProperty("type2")));
		        			
		        			
		        			
	        				//结束时间
	        				endDate = dc.reg(html_p,prop.getProperty("startDate1"));
	        				if(endDate!=null){
	        					endDate = dc.reg(endDate,prop.getProperty("startDate1_1"));
	        					if(endDate!=null){
		        					endDate = endDate.substring(1,endDate.length()-1);
		        					if(endDate.indexOf("日")!=-1){
		        						endDate = endDate.substring(0,endDate.indexOf("日")+1);
		        					}
	        					p.setEndDate(endDate);
	        					}
	        				}
	        				//地址
	        				addr = dc.reg(html, prop.getProperty("addr"));
	        				if(null!=addr){
	        					if(addr.indexOf("上海市")!=-1){
	        						addr.substring(addr.indexOf("上海市")+3,addr.length());
	        					  }
	        					p.setAddr(addr.substring(4,addr.length()));
	        				}
	        				
	        				//评估价
	        				assessmentPriceCN = dc.reg(html_li, prop.getProperty("assessmentPriceCN_gp_li"));
	        				if(null!=assessmentPriceCN){
	        					String new_s = assessmentPriceCN.substring(6, assessmentPriceCN.length()-2);
								String new_ss = new_s.replace(",", "");
								if(new_ss.length()>4)
//								p.setAssessmentPriceCN(new_ss.substring(0, new_ss.length()-4)+"万");	 
								p.setAssessmentPriceCN(new_ss.substring(0, new_ss.length()-4)+"."+new_ss.substring(new_ss.length()-4, new_ss.length()));
	        				}
	        				
	        				//拍卖次数
	        				countNum = dc.reg(html_li, prop.getProperty("countNum"));
	        				String cn_new = null;
	        				
	        				if(null!=countNum) {
								countNum = countNum.substring(5,countNum.length());
								cn_new = dc.reg(countNum, prop.getProperty("countNum_new"));
								if(cn_new!=null){
									p.setCountNum(cn_new.substring(1,cn_new.length()-1)+"拍");
								}else{
									p.setCountNum(countNum);
								}
							}
	        				
	        				//保证金
	        				bond = dc.reg(html_li, prop.getProperty("bond_gp_li"));
	        				if(null!=bond) {
	        					String bond1 = bond.substring(6, bond.length()-1).replace(" ", "");
								String newBond1 = bond1.replace(",", "");
								if(newBond1.length()>4)
									p.setBond(newBond1.substring(0, 3)+"."+newBond1.substring(3,newBond1.length()));
//								p.setBond(newBond1.substring(0, newBond1.length()-4)+"万");
							}
	        				//税费分担
		        			if(taxation!=null)
		        				p.setTaxation(taxation.substring(6,taxation.length()));
		        			
		        			//执行法院
		        			shopName = dc.reg(html_li, prop.getProperty("shopName"));
		        			if(shopName!=null){
		        				shopName = shopName.substring(5,shopName.length());		        				
		        				if(shopName.indexOf("法院")!=-1)
		        				p.setShopName(shopName.substring(0,shopName.indexOf("法院")+2));
		        			}
		        			
	        			}else{//item2.do
	        				//出价次数
	        				Elements shu = detail_doc.select("span#html_Bid_Shu");
	        				String a =shu.get(0).text();
	        				int count = Integer.valueOf(a);
	        				p.setBidCount(count);
	        				//租赁情况
	        				lease = dc.reg(html_span,prop.getProperty("lease2"));
	        				if(lease!=null)
	        					p.setLease(lease.substring(4,lease.length()-1));
	        				
	        				//建筑面积
	        				build_area = dc.reg(html_p,prop.getProperty("build_area2"));
	        				if(build_area!=null)
	        				p.setBuild_area(build_area.substring(5,build_area.length()));
	        				
	        				//竣工日期
	        				completionDate = dc.reg(html_p, prop.getProperty("completionDate2"));
	        				if(completionDate!=null)
	        				p.setCompletionDate(completionDate.substring(5,9));
	        				//结束时间
	        				endDate = dc.reg(html_p,prop.getProperty("startDate2"));
	        				if(endDate!=null){
	        					if(endDate.indexOf("日")!=-1){
	        						endDate = endDate.substring(1,endDate.indexOf("日")+1);
	        					}
	        					p.setEndDate(endDate);
	        				}
	        				//房屋类型
	        				type = dc.reg(html_span, prop.getProperty("type"));
		        			p.setType(type);
		        			p.setType2(dc.reg(html_span, prop.getProperty("type2")));
		        			//评估价
		        			if(null!=assessmentPriceCN){
		        				String new_s = assessmentPriceCN.substring(4, assessmentPriceCN.length()-2);
								String new_ss = new_s.replace(",", "");
								if(new_ss.length()>4)
//								p.setAssessmentPriceCN(new_ss.substring(0, new_ss.length()-4)+"万");
									p.setAssessmentPriceCN(new_ss.substring(0, new_ss.length()-4)+"."+new_ss.substring(new_ss.length()-4, new_ss.length()));
		        			}
		        			
		        			//拍卖次数
		        			String cn_new = null;
		        			if(null!=countNum){
		        				countNum = countNum.substring(5,countNum.length());
								cn_new = dc.reg(countNum, prop.getProperty("countNum_new"));
							if(cn_new!=null){
								p.setCountNum(cn_new.substring(1,cn_new.length()-1)+"拍");
							}else{
								p.setCountNum(countNum);
							}
		        			}
//		        			
		        			//保证金
		        			if(null!=bond) {
		        				bond = bond.replace(",", "");
		        				bond = bond.replace(" ", "");
//								p.setBond(bond.substring(4, bond.length()-5)+"万");
		        				bond = bond.substring(4, bond.length()-5)+"."+bond.substring(bond.length()-5, bond.length());
		        				if(bond.indexOf("元")!=-1){
		        					bond.replace("元", "");
		        				}
		        				p.setBond(bond);
							}
		        			
		        			//税费分担
//		        			taxation=reg(html,prop.getProperty("taxation_2"));
//		        			if(taxation!=null)
//		        				p.setTaxation(taxation.substring(5,taxation.length()-1));
		        			
		        			//执行法院
		        			if(null!=shopName){
		        				shopName = shopName.substring(5,shopName.length());
		        				if(shopName.indexOf("法院")!=-1){
		        					p.setShopName(shopName.substring(0,shopName.indexOf("法院")+2));
		        				}
		        			}
	        			}
	        			int psize = findProduct(p);
	        			if(psize!=0){
	        				Db.update("update product_data set result = ? where house_detail = ?",p.getResult(),p.getHouse_detail());
	        			}else{
	        				getCityAAAA(p, dc,prop,detail_doc,jtc);
	        				addPro(p);
	        			}
	        			System.out.println(p);
//	        			list.add(p);
	        			num++;
	        			k++;
        			}else if(k==2){
        				k=0;
        			}else{
        				k++;
        			}
        		}
			}
        }
		return list;
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
			.set("house_detail", p.getHouse_detail())
			.set("house_theme", p.getHouse_theme())
			.set("bidCount", p.getBidCount())
			.set("premium", p.getPremium())
			.set("premium_rate", p.getPremium_rate())
			;
			Db.save("product_data","id", product);
		return true;
	}
	public void getCityAAAA(Product p,DataCapture dc,Properties prop,Document detail_doc,JsonToClass jtc){
		String _addr = dc.reg(p.getTitle(), prop.getProperty("addr_test"));
		String get_Lng_Lat = "";
		if(_addr==null){
			String title1 = "";
			System.out.println("title="+p.getTitle());
			if(p.getTitle().length()>30){
				title1 = p.getTitle().substring(0,30);
				get_Lng_Lat = "http://api.map.baidu.com/geocoder/v2/?address="+title1+"&output=json&ak=lYMpqrGu4iT9wWNGnzjAnGDTqHjkfCH2";	
			}else{
				get_Lng_Lat = "http://api.map.baidu.com/geocoder/v2/?address="+p.getTitle()+"&output=json&ak=lYMpqrGu4iT9wWNGnzjAnGDTqHjkfCH2";	
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
				p.setCommunity_name(community_name1);
			}
		}
		
		
		String city = dc.reg(detail_doc.title(),prop.getProperty("city"));
		if(city!=null){
			p.setCity(city);
		}else{
			if(district!=null){
				p.setCity(district.substring(0,2));
			}
		}
	}
	
	public int findProduct(Product p){
		List<Record> pro_list = Db.find("select 1 from product_data where house_detail = ?",p.getHouse_detail());
		return pro_list.size();
		
	}
	
}
