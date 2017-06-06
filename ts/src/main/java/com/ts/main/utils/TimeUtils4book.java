package com.ts.main.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils4book {
	
	public final static String yMdHms= "yyyy-MM-dd HH:mm:ss";
	
	public final static String yMdHmscn= "yyyy年MM月dd HH时mm分ss";
	
	public final static String yMd= "yyyyMMdd";
	public final static String yMd_ = "yyyy-MM-dd";
	
	public static String date2str(Date date){
		SimpleDateFormat time = new SimpleDateFormat(yMdHms);
		return time.format(date);
	}
	
	public static String dateInterval(Long date){
		Long l = System.currentTimeMillis()-date;
		long minute = l / 60000;
		if(minute<=0){
			return "刚刚";
		}else if(minute<60){
			return minute+"分钟前";
		}else {
			long hour = minute/60;
			if(hour<24){
				return hour+"小时前";
			}else{
				long day = hour/24;
				if(day<7){
					return day+"天前";
				}else if(day<31){
					return day/7+"周前";
				}else if(day<365){
					return day/30+"月前";
				}else{
					return day/365+"年前";
				}
			}
		}
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
	
	public static String long2Str(Long date){
		SimpleDateFormat time = new SimpleDateFormat(yMdHmscn);
		return time.format(new Date(date));
	}
	
	public static String long2Str(Long date,String format){
		SimpleDateFormat time = new SimpleDateFormat(format);
		return time.format(new Date(date));
	}
	
	/**
	 * @return 当天零点
	 */
	public static Long getTimesmorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	/**
	 * @return 本周零点
	 */
	public static Long getTimesWeekmorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTimeInMillis();
	}
	
	public static Long getBefore(Long d,TimeUnit timeUnit){
		Long td = System.currentTimeMillis();
		td = td-timeUnit.toMillis(d);
		return td;
	}
	
	/**
	 * @param args
	 * ceshi
	 */
	public static void main(String args[]){
		System.out.println(dateInterval(1496171998089l));
//		Long sd = System.currentTimeMillis();
//		Long bd = getBefore(30l,TimeUnit.DAYS);
//		System.out.println(sd);
//		System.out.println(bd);
//		System.out.println(sd-bd);
	}

}
