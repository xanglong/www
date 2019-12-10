package com.xanglong.frame.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyVerification {
	
	String name();
	
	/**非空*/
	boolean notNull() default false;
	
	/**最小长度*/
	int minLength() default -1;
	
	/**最大长度*/
	int maxLength() default -1;
	
	/**短整型：最大值*/
	short maxShort() default Short.MIN_VALUE;
	
	/**短整型：最小值*/
	short minShort() default Short.MAX_VALUE;
	
	/**整型：最大值*/
	int maxInt() default Integer.MIN_VALUE;
	
	/**整型：最小值*/
	int minInt() default Integer.MAX_VALUE;
	
	/**长整型：最大值*/
	long maxLong() default Long.MAX_VALUE;
	
	/**长整型：最小值*/
	long minLong() default Long.MAX_VALUE;
	
	/**单精度：最大值*/
	float maxFloat() default Float.MIN_VALUE;
	
	/**单精度：最小值*/
	float minFloat() default Float.MAX_VALUE;
	
	/**双精度：最大值*/
	double maxDouble() default Double.MIN_VALUE;
	
	/**双精度：最小值*/
	double minDouble() default Double.MAX_VALUE;
	
	/**字符型：最大值*/
	char maxChar() default Character.MIN_VALUE;
	
	/**字符型：最小值*/
	char minChar() default Character.MAX_VALUE;
	
	/**正则校验*/
	String regexp() default "";
	
	/**非真*/
	boolean notTrue() default false;
	
	/**非假*/
	boolean notFlase() default false;
	
	/**防跨站XSS攻击*/
	boolean XSS() default false;
	
}