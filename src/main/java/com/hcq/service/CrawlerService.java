package com.hcq.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import com.hcq.bean.Product;
import com.hcq.util.JsonToClass;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class CrawlerService {
	public List<Product> getList(String url) throws UnsupportedEncodingException, IOException{
		
		return null;
	}
}
