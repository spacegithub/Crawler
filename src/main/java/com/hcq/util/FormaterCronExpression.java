package com.hcq.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormaterCronExpression {
	static String formart = "yyyy-MM-dd HH:mm:ss";
	 
	public static String formaterCronExpression(String date) {
		SimpleDateFormat format = new SimpleDateFormat(formart.substring(0, date.length() - 1));
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy MM dd HH mm ss");
		try {
			Date d = format.parse(date);
			date = format2.format(d);
			String[] dateArry = date.split(" ");
			String exp = dateArry[5] + " " + dateArry[4] + " " + dateArry[3] + " " + dateArry[2] + " " + dateArry[1]
					+ " ? " + dateArry[0];
			return exp;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
