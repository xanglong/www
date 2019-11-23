package com.xanglong.frame;

import java.lang.reflect.Field;

import com.xanglong.frame.log.Logger;

public class Reflect {

	/**
	 * 通过反射获取不可访问的私有对象
	 * @param obj 要获取私有成员变量的对象
	 * @param fieldName 成员变量的名称
	 * @return object 成员变量对象
	 * */
	public static Object get(Object obj, String fieldName) {
		//这种遍历操作性能上要比getDeclaredField方法好很多
		Object target = null;
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				boolean isAccessible = field.isAccessible();
				field.setAccessible(true);
				try {
					target = field.get(obj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					Logger.error(e);
				}
				field.setAccessible(isAccessible);
				break;
			}
		}
		return target;
	}
	
}