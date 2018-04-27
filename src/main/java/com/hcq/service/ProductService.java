package com.hcq.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class ProductService {
	public List<Record> getAllByLimit(int start,int limit){
		int start_0 = (start-1)*limit;
		return Db.find("select * from product_data limit "+ start_0 +","+limit+";");
	}
	public long getCount(){
		Long count = Db.queryLong("select count(1) from product_data");
		return count;
		
	}
}
