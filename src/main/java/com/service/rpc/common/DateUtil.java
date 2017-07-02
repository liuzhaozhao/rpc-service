package com.service.rpc.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static final String DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";
	
	public static void main(String[] args) {
		System.err.println(formate(addSecond(60*10)));
	}
	
	/**
	 * 格式化日期
	 * @param date
	 * @return
	 */
	public static String formate(Date date){
		return formate(date, DATE_FORMATE);
	}
	public static String formate(Date date, String formate){
		if(date == null){
			return "";
		}
		return new SimpleDateFormat(formate).format(date);
	}
	
	/**
	 * 在指定日期上增加时间
	 * @param date
	 * @param seconds
	 * @return
	 */
	public static Date addSecond(int seconds){
		return addSecond(new Date(), seconds);
	}
	public static Date addSecond(Date date, int seconds){
		return addDate(Calendar.SECOND, date, seconds);
	}
	private static Date addDate(int field, Date date, int time){
		if(date == null){
			date = new Date();
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, time);
		return calendar.getTime();
	}
}
