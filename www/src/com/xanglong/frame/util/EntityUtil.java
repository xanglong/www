package com.xanglong.frame.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
	
	/**获取PO对象*/
	public static <T>T getPo(JSONObject bodyParams, Class<T> clazz) {
		return getBean(bodyParams, clazz);
	}
	
	/**获取VO对象*/
	public static <T>T getVo(JSONObject bodyParams, Class<T> clazz) {
		return getBean(bodyParams, clazz);
	}
	
	/**获取SO对象*/
	public static <T>T getSo(JSONObject bodyParams, Class<T> clazz) {
		return getBean(bodyParams, clazz);
	}

}