package com.xanglong.frame;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.xanglong.frame.exception.BizException;

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
					field.setAccessible(isAccessible);
					throw new BizException(e);
				}
				field.setAccessible(isAccessible);
				break;
			}
		}
		return target;
	}
	
	/**
	* 获取方法泛型参数返回类型
	* @param method 方法
	* @return typeList 参数类型列表
	*/
	public static List<Class<?>> getReturnTypes(Method method) {
		List<Class<?>> typeList = new ArrayList<>();
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		for (Type genericParameterType : genericParameterTypes) {
			if (genericParameterType instanceof ParameterizedType) {
				Type[] parameterizedTypes = ((ParameterizedType) genericParameterType).getActualTypeArguments();
				for (Type parameterizedType : parameterizedTypes) {
					typeList.add((Class<?>) parameterizedType);
				}
			}
		}
		return typeList;
	}
	
	/**
	 * 获取对象所有定义字段
	 * @param clazz 类对象
	 * @return fields 字段数组
	 * */
	public static Field[] getAllDeclaredFields(Class<?> clazz){
		List<Field> fieldList = new ArrayList<>();
		while (clazz != null){
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				fieldList.add(field);
			}
			clazz = clazz.getSuperclass();
		}
		Field[] fields = new Field[fieldList.size()];
		fieldList.toArray(fields);
		return fields;
	}
	
}