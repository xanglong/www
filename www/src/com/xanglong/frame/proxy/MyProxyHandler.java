package com.xanglong.frame.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**代理方法回调保持*/
public class MyProxyHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (Object.class.equals(method.getDeclaringClass())) {
			return method.invoke(this, args);
		}
		return new Object();
	}

}