package com.xanglong.frame.util;

public class StringUtil {

	/**
	 * 是否为空字符串
	 * @param text 字符串
	 * @return 是否
	 * */
	public static boolean isBlank(String text) {
		return text == null || text.trim().length() == 0;
	}

}