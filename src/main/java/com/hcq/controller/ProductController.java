package com.hcq.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.hcq.bean.Product;
import com.hcq.service.ProductService;
import com.hcq.util.ExcelExportUtil;

public class ProductController extends Controller{
	private ProductService ps = new ProductService();
	public void index() throws IOException{
		int page = 1;
		long total = 0;
		total = ps.getCount();
		page = getParaToInt("p");
		setAttr("p", page);
		setAttr("total", total);
		setAttr("list", ps.getAllByLimit(page, 20));
		render("/product.jsp");
	}
	public void getAllMsg(){
		String sql = "select * from product_data";
		Map<String, String> titleData =  new LinkedHashMap();//标题，后面用到
       
        titleData.put("city", "区域");
        titleData.put("community_name", "楼盘名称");
        titleData.put("addr", "拍卖房地产地址");
        titleData.put("plate", "板块");
        titleData.put("house_type", "房型");
        titleData.put("type", "房屋类型");
        titleData.put("structure", "房屋结构");
        titleData.put("floor", "楼层");
        titleData.put("build_area", "建筑面积㎡");
        titleData.put("completionDate", "竣工年限");
        titleData.put("degree", "装修程度");
        titleData.put("currentPriceCN", "起拍价");
        titleData.put("assessmentPriceCN", "评估价");
        titleData.put("bond", "保证金");
        titleData.put("marketPrice", "市场参考价");
        titleData.put("section", "价格区间");
        titleData.put("startDate", "开拍日期");
        titleData.put("endDate", "结束时间");
        titleData.put("collection_date", "采集日期");
        titleData.put("lease", "房屋现状");
        titleData.put("taxation", "税费分担");
        titleData.put("shopName", "执行法院");
        titleData.put("intermediary", "拍卖机构");
        titleData.put("countNum", "拍卖次数");
        titleData.put("tel", "咨询电话");
        titleData.put("around", "周边配套");
        titleData.put("list_pic", "列表图片");
        titleData.put("house_pic", "房型图片");
        titleData.put("taxation_num", "税费");
        titleData.put("commission", "佣金");
        titleData.put("cost", "成本");
        titleData.put("result", "拍卖结果");
        titleData.put("is_choice", "是否为精选房源");
        titleData.put("house_detail", "房源详情");
        titleData.put("house_theme", "房源主题");
        titleData.put("bidCount", "出价次数");
        titleData.put("premium", "    溢价额     （成交价-起拍价）");
        titleData.put("premium_rate", "    溢价率%    （差额/起拍价）");
        File file = new File(ExcelExportUtil.getTitle());
        file = ExcelExportUtil.saveFile(titleData, sql, file);
        this.renderFile(file);
	}
}
