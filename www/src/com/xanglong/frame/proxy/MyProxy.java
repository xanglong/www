package com.xanglong.frame.proxy;

import java.lang.reflect.Proxy;

import com.xanglong.frame.dao.DaoHandler;

/**代理类*/
public class MyProxy {

	/**
	 * 获取通用代理实例
	 * @param clazz 类
	 * @return 代理实例
	 * */
	public static Object getInstance(Class<?> clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new MyProxyHandler());
    }

	/**
	 * 获取数据库DAO代理实例
	 * @param clazz 类
	 * @return 代理实例
	 * */
	public static Object getDaoInstance(Class<?> clazz) {
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new DaoHandler());
	}

}