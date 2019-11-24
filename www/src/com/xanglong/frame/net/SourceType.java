package com.xanglong.frame.net;

public enum SourceType {
	
	HTML("html", "网页"),
	JS("js", "脚本"),
	CSS("css", "样式"),
	IMAGE("image", "图片"),
	FONT("font", "字体"),
	VIDEO("video", "视频"),
	AUDIO("audio", "音频"),
	ACTION("action", "动作")
	;

	private String code;

	private String name;

	SourceType(String code, String name) {
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