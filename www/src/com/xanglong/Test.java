package com.xanglong;

import com.xanglong.frame.exception.ThrowableHandler;
import com.xanglong.frame.net.HttpUtil;
import com.xanglong.frame.net.ResponseDto;

public class Test {
	
	public static void main(String[] args) {
		try {
			ResponseDto responseDto = HttpUtil.doGet("http://lt.800best.com");
			String result = new String(responseDto.getBytes());
			System.out.println(result);
		} catch (Throwable e) {
			ThrowableHandler.dealException(e);
		}
	}
	
}