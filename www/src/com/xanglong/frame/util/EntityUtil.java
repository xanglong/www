package com.xanglong.frame.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.Reflect;

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
	
	/**
	 * 获取Bean对象，不支持嵌套，忽略key的大小写
	 * @param jsonObject JSON对象
	 * @param clazz 类
	 * @return 类的实例对象
	 * */
	public static <T>T getDaoBean(JSONObject jsonObject, Class<T> clazz) {
		if (jsonObject == null) {
			return null;
		}
		Map<String, String> objKeys = new HashMap<>();
		for (String key : jsonObject.keySet()) {
			objKeys.put(key.toLowerCase(), key);
		}
		JSONObject data = new JSONObject();
		Field[] fields = Reflect.getAllDeclaredFields(clazz);
		for (Field field : fields) {
			String key = field.getName();
			data.put(key, jsonObject.get(objKeys.get(key.toLowerCase())));
		}
		return getBean(data, clazz);
	}
	
	/**
	 * 是否是基础类型，JDK自带的方法没有判断封装类型
	 * @param clazz 类
	 * @return 是否
	 * */
	public static boolean isBaseType(Class<?> clazz) {
		if (String.class == clazz
			|| int.class == clazz || Integer.class == clazz
			|| long.class == clazz || Long.class == clazz
			|| boolean.class == clazz || Boolean.class == clazz
			|| double.class == clazz || Double.class == clazz
			|| float.class == clazz || Float.class == clazz
		) {
			return true;
		}
		return false;
	}

	/**
	 * 获取基本类型值
	 * @param data 数据
	 * @param returnType 返回类型
	 * @param key 键
	 * @return value 值
	 * */
	public static Object getBaseValue(JSONObject data, Class<?> returnType, String key) {
		if (String.class == returnType) {
			return data.getString(key);
		} else if (int.class == returnType) {
			return data.getIntValue(key);
		} else if (long.class == returnType) {
			return data.getLongValue(key);
		} else if (boolean.class == returnType) {
			return data.getBooleanValue(key);
		} else if (double.class == returnType) {
			return data.getDoubleValue(key);
		} else if (Integer.class == returnType) {
			return data.getInteger(key);
		} else if (Long.class == returnType) {
			return data.getLong(key);
		} else if (Boolean.class == returnType) {
			return data.getBoolean(key);
		} else if (Double.class == returnType) {
			return data.getDouble(key);
		} else if (float.class == returnType) {
			return data.getFloatValue(key);
		} else if (Float.class == returnType) {
			return data.getFloat(key);
		}
		return null;
	}
	
	/**
	 * 获取基本类型默认值
	 * @param returnType 返回类型Class
	 * @return value 默认值
	 * */
	public static Object getDefaultValue(Class<?> returnType) {
		if (String.class == returnType) {
			return null;
		} else if (int.class == returnType) {
			return 0;
		} else if (long.class == returnType) {
			return 0l;
		} else if (boolean.class == returnType) {
			return false;
		} else if (double.class == returnType) {
			return 0d;
		} else if (Integer.class == returnType) {
			return new Integer(0);
		} else if (Long.class == returnType) {
			return 0L;
		} else if (Boolean.class == returnType) {
			return Boolean.FALSE;
		} else if (Double.class == returnType) {
			return 0D;
		} else if (float.class == returnType) {
			return 0f;
		} else if (Float.class == returnType) {
			return 0F;
		} else {
			return null;
		}
	}

}