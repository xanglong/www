package com.xanglong.frame.proxy;

import java.lang.reflect.Proxy;

import com.xanglong.frame.mvc.RepositoryHandler;
import com.xanglong.frame.mvc.ServiceHandler;

/**代理类*/
public class ProxyManager {

	/**
	 * 获取通用代理实例
	 * @param clazz 类
	 * @return 代理实例
	 * */
	public Object getInstance(Class<?> clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ProxyHandler());
    }

	/**
	 * 获取数据库DAO代理实例
	 * @param clazz 类
	 * @return 代理实例
	 * */
	public Object getDaoInstance(Class<?> clazz) {
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new RepositoryHandler());
	}
	
	/**
	 * 获取业务类代码代理实例
	 * @param clazz 类
	 * @return 代理实例
	 * */
	public Object getServiceInstance(Class<?> clazz) {
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ServiceHandler());
	} 

}