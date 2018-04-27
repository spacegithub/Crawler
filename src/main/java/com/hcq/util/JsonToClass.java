package com.hcq.util;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gson.Gson;
public class JsonToClass {
	public JSONArray getProductTotal(String url){
		 JsonToStr t = new JsonToStr();
		 String products = t.getJson(url);
		 Gson gson = new Gson();
		 Map<String, Object> map = new HashMap<String, Object>();
		 map = gson.fromJson(products, map.getClass());
		 JSONArray jsonArray=JSONArray.fromObject(map.get("total"));
		 return jsonArray;
	}
	
	public JSONObject getJSONObj(String url){
		JsonToStr t = new JsonToStr();
		String products = t.getJson(url);
		String replace1 = products.replace("renderReverse&&renderReverse(", "");
		String replace2 = replace1.replace(")", "");
		JSONObject jsonArray = JSONObject.fromObject(replace2);
		return jsonArray;
	}
	
	public JSONArray getProductClass(String url,String key){
		 JsonToStr t = new JsonToStr();
		 String products = t.getJson(url);
		 System.out.println(products);
		 Gson gson = new Gson();
		 Map<String, Object> map = new HashMap<String, Object>();
		 map = gson.fromJson(products, map.getClass());
		 JSONArray jsonArray=JSONArray.fromObject(map.get(key));
		 return jsonArray;
	}
/*
 * ��json����ת������
 */
	public Map<String, Object> getMsg(String url){
		 JsonToStr t = new JsonToStr();
		 String products = t.getJson(url);
		 Gson gson = new Gson();
		 Map<String, Object> map = new HashMap<String, Object>();
		 map = gson.fromJson(products, map.getClass());
		 return map;
	}
}
