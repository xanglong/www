package com.xanglong.frame.net;

/**请求类型枚举*/
public enum Method {
	
	GET("GET", "浏览器请求"),
	POST("POST", "接口请求");
	
	Method(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	private String code;

	private String name;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

}