package com.xanglong.frame.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.xanglong.frame.exception.BizException;

public class DateUtil {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat timeMillisFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	private static SimpleDateFormat gmtFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
	
	static {
		gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	/**
	 * 获取日期
	 * @param date 日期时间
	 * @return 日期字符串
	 * */
	public static String getDate(Date date) {
		return dateFormat.format(date);
	}
	
	/**
	 * 获取时间
	 * @param date 日期时间
	 * @return 时间字符串
	 * */
	public static String getTime(Date date) {
		return timeFormat.format(date);
	}
	
	/**
	 * 获取日期时间
	 * @param date 日期时间
	 * @return 日期时间字符串
	 * */
	public static String getDateTime(Date date) {
		return dateTimeFormat.format(date);
	}
	
	/**
	 * 获取日期时间
	 * @param date 日期时间
	 *  @return 日期时间字符串
	 * */
	public static String getTimeMillis(Date date) {
		return timeMillisFormat.format(date);
	}
	
	/**
	 * 获取GMT时间
	 * @return GMT时间字符串
	 * */
	public static String getGmtTime(long timestamp) {
		return gmtFormat.format(timestamp);
	}
	
	/**
	 * 获取年份
	 * @return 年份
	 * */
	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	/**
	 * 获取日期
	 * @param date 日期字符串
	 * @return 日期
	 * */
	public static Date getDate(String date) {
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 获取时间
	 * @param time 时间字符串
	 * @return 时间
	 * */
	public static Date getTime(String time) {
		try {
			return timeFormat.parse(time);
		} catch (ParseException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 获取日期时间
	 * @param datetime 日期时间字符串
	 * @return 日期时间
	 * */
	public static Date getDateTime(String datetime) {
		try {
			return dateTimeFormat.parse(datetime);
		} catch (ParseException e) {
			throw new BizException(e);
		}
	}
	
}