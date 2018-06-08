package com.hcq.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

public class Product_tb extends Model<Product_tb>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String city;// 区域
	private String title;// 标题
	private String addr; // 地址
	private String community_name; //楼盘名称
	private long start;// 开始时间
	private String startDate;
	private long end;// 结束时间
	private String endDate;
	private String initialPrice;// 起拍价
	private String currentPrice;// 当前价
	private String consultPrice;// 评估价
	private double albumId; // 参数1
	private String vendorId; // 参数2
	private String shopName; // 法院
	private String countNum;// 拍卖次数
	private String intermediary; // 拍卖机构
	private String house_type; // 房型
	private String plate; //板块
	private String degree; //装修程度
	private String marketPrice; //市场参考价
	private String section; //价格区间
	private String around; //周边配套
	private String list_pic; //列表图片
	private String house_pic; //房型图片
	private String taxation_num; //税费
	private String commission; //佣金
	private String cost; //成本
	private String result; //拍卖结果
	private String is_choice; //是否为精选房源
	private String house_detail; //房源详情
	private String house_theme; //房源主题
	private int premium; //溢价额
	private String premium_rate; //议价率
	private String type;// 房屋类型
	private String build_area; // 建筑面积
	private String bond; //保证金
	private String collection_date;//采集日
	private String structure; //房屋结构
	private String tel; //咨询电话
	private int bidCount; //出价次数
	private String taxation; //税费分担
	private String floor; //楼层
	private String completionDate; //竣工日期
	private String lease; //租赁情况
	private String itemUrl; //淘宝所需商品链接
	private String type2;
	private int signCount; //报名人数
	private String currprice; //当前价
	public Product_tb() {
		super();
	}

	public Product_tb(long id, String city, String title) {
		super();
		this.id = id;
		this.city = city;
		this.title = title;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	public int getSignCount() {
		return signCount;
	}

	public void setSignCount(int signCount) {
		this.signCount = signCount;
	}

	public String getCurrprice() {
		return currprice;
	}

	public void setCurrprice(String currprice) {
		this.currprice = currprice;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
		this.startDate = transferLongToDate("yyyy/MM/dd", start);
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
		this.endDate = transferLongToDate("yyyy/MM/dd", end);
	}

	public double getAlbumId() {
		return albumId;
	}

	public void setAlbumId(double albumId) {
		this.albumId = albumId;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}


	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}


	public String getIntermediary() {
		return intermediary;
	}

	public void setIntermediary(String intermediary) {
		this.intermediary = intermediary;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBuild_area() {
		return build_area;
	}

	public void setBuild_area(String build_area) {
		this.build_area = build_area;
	}

	
	public String getBond() {
		return bond;
	}

	public void setBond(String bond) {
		this.bond = bond;
	}
	

	public String getHouse_type() {
		return house_type;
	}

	public void setHouse_type(String house_type) {
		this.house_type = house_type;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(String marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getAround() {
		return around;
	}

	public void setAround(String around) {
		this.around = around;
	}

	public String getList_pic() {
		return list_pic;
	}

	public void setList_pic(String list_pic) {
		this.list_pic = list_pic;
	}

	public String getHouse_pic() {
		return house_pic;
	}

	public void setHouse_pic(String house_pic) {
		this.house_pic = house_pic;
	}

	public String getTaxation_num() {
		return taxation_num;
	}

	public void setTaxation_num(String taxation_num) {
		this.taxation_num = taxation_num;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getIs_choice() {
		return is_choice;
	}

	public void setIs_choice(String is_choice) {
		this.is_choice = is_choice;
	}

	public String getHouse_detail() {
		return house_detail;
	}

	public void setHouse_detail(String house_detail) {
		this.house_detail = house_detail;
	}

	public String getHouse_theme() {
		return house_theme;
	}

	public void setHouse_theme(String house_theme) {
		this.house_theme = house_theme;
	}

	public int getPremium() {
		return premium;
	}

	public void setPremium(int premium) {
		this.premium = premium;
	}

	public String getPremium_rate() {
		return premium_rate;
	}

	public void setPremium_rate(String premium_rate) {
		this.premium_rate = premium_rate;
	}

	public String getCollection_date() {
		return collection_date;
	}

	public void setCollection_date(String collection_date) {
		this.collection_date = collection_date;
	}

	
	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	
	public int getBidCount() {
		return bidCount;
	}

	public void setBidCount(int bidCount) {
		this.bidCount = bidCount;
	}


	public String getCountNum() {
		return countNum;
	}

	public void setCountNum(String countNum) {
		this.countNum = countNum;
	}

	
	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	
	public String getTaxation() {
		return taxation;
	}

	public void setTaxation(String taxation) {
		this.taxation = taxation;
	}

	
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	
	public String getCommunity_name() {
		return community_name;
	}

	public void setCommunity_name(String community_name) {
		this.community_name = community_name;
	}
	
	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	
	public String getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}

	
	public String getLease() {
		return lease;
	}

	public void setLease(String lease) {
		this.lease = lease;
	}

	
	
	public String getInitialPrice() {
		return initialPrice;
	}

	public void setInitialPrice(String initialPrice) {
		this.initialPrice = initialPrice;
	}

	public String getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(String currentPrice) {
		this.currentPrice = currentPrice;
	}

	public String getConsultPrice() {
		return consultPrice;
	}

	public void setConsultPrice(String consultPrice) {
		this.consultPrice = consultPrice;
	}

	public String getItemUrl() {
		return itemUrl;
	}

	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}
	

	public String getType2() {
		return type2;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}

	private String transferLongToDate(String dateFormat, Long millSec) {

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		Date date = new Date(millSec);

		return sdf.format(date);

	}

	@Override
	public String toString() {
		return "Product_tb [id=" + id + ", city=" + city + ", title=" + title
				+ ", addr=" + addr + ", community_name=" + community_name
				+ ", start=" + start + ", startDate=" + startDate + ", end="
				+ end + ", endDate=" + endDate + ", initialPrice="
				+ initialPrice + ", currentPrice=" + currentPrice
				+ ", consultPrice=" + consultPrice + ", albumId=" + albumId
				+ ", vendorId=" + vendorId + ", shopName=" + shopName
				+ ", countNum=" + countNum + ", intermediary=" + intermediary
				+ ", house_type=" + house_type + ", plate=" + plate
				+ ", degree=" + degree + ", marketPrice=" + marketPrice
				+ ", section=" + section + ", around=" + around + ", list_pic="
				+ list_pic + ", house_pic=" + house_pic + ", taxation_num="
				+ taxation_num + ", commission=" + commission + ", cost="
				+ cost + ", result=" + result + ", is_choice=" + is_choice
				+ ", house_detail=" + house_detail + ", house_theme="
				+ house_theme + ", premium=" + premium + ", premium_rate="
				+ premium_rate + ", type=" + type + ", build_area="
				+ build_area + ", bond=" + bond + ", collection_date="
				+ collection_date + ", structure=" + structure + ", tel=" + tel
				+ ", bidCount=" + bidCount + ", taxation=" + taxation
				+ ", floor=" + floor + ", completionDate=" + completionDate
				+ ", lease=" + lease + ", itemUrl=" + itemUrl + ", type2="
				+ type2 + ", signCount=" + signCount + ", currprice="
				+ currprice + "]";
	}

	

	


	
	
}
