package com.xanglong.frame.proxy;

import java.lang.reflect.Proxy;

import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.mvc.RepositoryHandler;
import com.xanglong.frame.mvc.ServiceHandler;
import com.xanglong.i18n.zh_cn.FrameException;

/**代理类*/
public class ProxyManager {

	/**
	 * 获取通用代理实例
	 * @param implementObject 实现类实体
	 * @return 代理实例
	 * */
	public Object getInstance(Object implementObject) {
		Class<?> clazz = implementObject.getClass();
		if (implementObject instanceof Class || clazz.isInterface()){
			throw new BizException(FrameException.FRAME_NEED_IMPLEMENT_OBJECT);
		}
		Class<?> implementClass = implementObject.getClass();
		ProxyHandler proxyHandler = new ProxyHandler(implementObject);
        return Proxy.newProxyInstance(implementClass.getClassLoader(), implementClass.getInterfaces(), proxyHandler);
    }

	/**
	 * 获取数据库DAO代理实例
	 * @param clazz 接口定义类
	 * @return 代理实例
	 * */
	public Object getDaoInstance(Class<?> interfaceClass) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new RepositoryHandler());
	}
	
	/**
	 * 获取业务类代码代理实例
	 * @param implementObject 实现类实体
	 * @return 代理实例
	 * */
	public Object getServiceInstance(Object implementObject) {
		Class<?> clazz = implementObject.getClass();
		if (implementObject instanceof Class || clazz.isInterface()){
			throw new BizException(FrameException.FRAME_NEED_IMPLEMENT_OBJECT);
		}
		Class<?> implementClass = implementObject.getClass();
		ServiceHandler serviceHandler = new ServiceHandler(implementObject);
		return Proxy.newProxyInstance(implementClass.getClassLoader(), implementClass.getInterfaces(), serviceHandler);
	}

}