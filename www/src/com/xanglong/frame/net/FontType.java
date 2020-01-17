package com.xanglong.frame.net;

/**字体类型*/
public enum FontType {

	EOT("eot", "字体"),
	WOFF("woff", "字体"),
	WOFF2("woff2", "字体"),
	TTF("ttf", "字体"),
	OTF("otf", "字体")
	;

	private String code;

	private String name;

	FontType(String code, String name) {
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