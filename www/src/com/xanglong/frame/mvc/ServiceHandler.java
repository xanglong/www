package com.xanglong.frame.mvc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceHandler implements InvocationHandler {
	
	private Object target;
	
	public ServiceHandler(Object target) {  
        super();  
        this.target = target;  
    }   

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//进入业务层切面
		MvcManager.aopEnter(BeanType.SERVICE);
		//如果是实现类方法调用，那就按照实现类的方法执行反射
		Class<?> clazz = method.getDeclaringClass();
		if (Object.class.equals(clazz)) {
			return method.invoke(this, args);
		}
		//在扫描包的时候确保了注入的都是实现类，所以这里不会报错
		Object result = method.invoke(target, args);
		//离开业务层切面
		MvcManager.aopExit();
		return result;
	}

}
