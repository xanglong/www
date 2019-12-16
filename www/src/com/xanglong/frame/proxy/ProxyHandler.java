package com.xanglong.frame.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyHandler implements InvocationHandler {
	
	private Object target;
	
	public ProxyHandler(Object target) {  
        super();  
        this.target = target;  
    }   

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (Object.class.equals(method.getDeclaringClass())) {
			return method.invoke(this, args);
		}
		return method.invoke(target, args);
	}

}