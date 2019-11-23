package com.xanglong.frame.net;

/**文档类型实体*/
public enum ContentType {

	//最常用的请求类型，多用于响应
	JSON("application/json", "JSON"),
	
	//样式文件
	CSS("text/css", "CSS"),
	
	//脚本文件
	JS("text/javascript", "JS"),
	
	//网页请求HTML、HTX、HTM、JSP、PLG、STM、XHTML
	HTML("text/html", "网页"),
	
	//在发送请求到服务器前编码所有字符，空格转换为 "+"加号，特殊符号转换为 ASCII HEX值
	FORM_URLENCODED("application/x-www-form-urlencoded", "表单"),
	
	//在发送请求到服务器前不编码所有字符，文件上次必须用的格式
	FORM_DATA("multipart/form-data", "表单"),
	
	//在发送请求到服务器前编码字符，空格空格转换为 "+"加号，特殊符号不转化
	FORM_TEXT("text/plain", "表单"),
	
	//文件传输
	STREAM("application/octet-stream", "文件")
	;
	
	private String code;
	
	private String name;
	
	ContentType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
}