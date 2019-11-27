package com.xanglong.frame.util;

import com.xanglong.frame.config.Const;

public class BaseUtil {

	/**
	 * 获取应用发布类编译路径
	 * @return 应用发布类编译路径
	 * */
	public static String getClassPath() {
		return BaseUtil.class.getClassLoader().getResource("").getPath();
	}

	/**
	 * 获取应用发布根路径
	 * @return 应用发布根路径
	 * */
	public static String getRootPath() {
		return getClassPath().replace("/WEB-INF/classes", "");
	}
	
	/**
	 * 获取存储目录
	 * @return 存储目录
	 * */
	public static String getSavePath() {
		String resourcePath = BaseUtil.class.getClassLoader().getResource("").getPath();
		String savePath = null;
		if (resourcePath.endsWith("build/classes/")) {
			savePath = resourcePath.replace("build/classes/", "") + Const.WEBCONTENT_FOLDER_NAME + "/WEB-INF/";
		} else {
			savePath = resourcePath.replace("classes/", "");
		}
		return savePath;
	}
	
	/**
	 * 二进制数组合并
	 * @param byte1 二进制数组1
	 * @param byte2 二进制数组2
	 * @return 合并后的二进制数据
	 * */
	public static byte[] byteMerger(byte[] byte1, byte[] byte2) {
		byte[] byte3 = new byte[byte1.length + byte2.length];
		System.arraycopy(byte1, 0, byte3, 0, byte1.length);
		System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);
		return byte3;  
	}

}