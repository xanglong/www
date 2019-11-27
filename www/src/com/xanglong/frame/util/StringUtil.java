package com.xanglong.frame.util;

import java.math.BigInteger;
import java.util.UUID;

public class StringUtil {

	/**
	 * 是否为空字符串
	 * @param text 字符串
	 * @return 是否
	 * */
	public static boolean isBlank(String text) {
		return text == null || text.trim().length() == 0;
	}
	
	/**
	 * 获取JAVA端生成的UUID，36进制压缩
	 * @return 唯一识别ID
	 * */
	public static String getUUID() {
		String uuid = new BigInteger(UUID.randomUUID().toString().replaceAll("-", ""), 16).toString(Character.MAX_RADIX);
		return (uuid + "00").substring(0, 25);
	}

}