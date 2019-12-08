package com.xanglong.frame.util;

import com.alibaba.fastjson.JSONObject;
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

	/**
	 * 根据字符串键获取对象里面的值，键可以是链式的
	 * @param data 对象数据
	 * @param key 链式key
	 * @return 字符串值
	 * */
	public static Object getChainValue(JSONObject data, String key) {
		if (StringUtil.isBlank(key)) {
			return null;
		}
		if (key.contains(".")) {
			String[] keys = key.split("\\.");
			for (int i = 0; i < keys.length - 1; i++) {
				key = keys[i];
				data = data.getJSONObject(key);
				if (data == null || data.isEmpty()) {
					return "";
				}
			}
			key = keys[keys.length - 1];
		}
		return data.get(key);
	}

}