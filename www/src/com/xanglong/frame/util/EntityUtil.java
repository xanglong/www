package com.xanglong.frame.util;

import com.alibaba.fastjson.JSON;

public class EntityUtil {

	/**
	 * 获取Bean对象
	 * @param json JSON对象
	 * @param clazz 类
	 * @return 类的实例对象
	 * */
	public static <T>T getBean(JSON json, Class<T> clazz) {
		return JSON.toJavaObject(json, clazz);
	}

}