package com.xanglong.frame.net;

/**文本类型*/
public enum TextType {

	TXT("txt", "文本文档"),
	;

	private String code;

	private String name;

	TextType(String code, String name) {
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