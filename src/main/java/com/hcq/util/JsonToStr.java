package com.hcq.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class JsonToStr {
	 public static Map<String, String> headers = null;

	    static {
	        headers = new HashMap<String, String>();
	        headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
	        headers.put("Accept-Language", "zh-CN,zh;q=0.8");
	        headers.put("Accept-Encoding", "gzip, deflate, sdch, br");
	        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	        headers.put("Connection", "Keep-Alive");
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	    }

	    public String getJson(String url) {
	        String content = JsoupHelper.get(url, null, "utf-8", headers);
	        return content;
	    }
		
}
