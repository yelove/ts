package com.ts.main.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	
	public final static String yMdHms= "yyyy-MM-dd HH:mm:ss";
	
	public final static String yMd= "yyyyMMdd";
	
	public static String date2str(Date date){
		SimpleDateFormat time = new SimpleDateFormat(yMdHms);
		return time.format(date);
	}
	
	public static String date2str(Date date,String formate){
		SimpleDateFormat time = new SimpleDateFormat(formate);
		return time.format(date);
	}
	
	public static Date str2date(String date,String formate){
		SimpleDateFormat time = new SimpleDateFormat(formate);
		try {
			return time.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
