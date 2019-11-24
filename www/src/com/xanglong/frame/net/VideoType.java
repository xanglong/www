package com.xanglong.frame.net;

/**图片类型枚举*/
public enum VideoType {
	
	ASF("asf", "视频", "video/x-ms-asf"),
	
	ASX("asx", "视频", "video/x-ms-asf"),
	
	AVI("avi", "视频", "video/avi"),
	
	IVF("IVF", "视频", "video/x-ivf"),
	
	M1V("m1v", "视频", "video/x-mpeg"),
	
	M2V("m2v", "视频", "video/x-mpeg"),
	
	M4E("m4e", "视频", "video/mpeg4"),
	
	MOVIE("movie", "视频", "video/x-sgi-movie"),
	
	MP2V("mp2v", "视频", "video/mpeg"),
	
	MP4("mp4", "视频", "video/mpeg4"),
	
	MPA("mpa", "视频", "video/x-mpg"),
	
	MPE("mpe", "视频", "video/x-mpeg"),
	
	MPG("mpg", "视频", "video/mpg"),
	
	MPEG("mpeg", "视频", "video/mpg"),
	
	MPS("mps", "视频", "video/x-mpeg"),
	
	MPV("mpv", "视频", "video/mpg"),
	
	MPV2("mpv2", "视频", "video/mpeg"),
	
	WM("wm", "视频", "video/x-ms-wm"),
	
	WMV("wmv", "视频", "video/x-ms-wmv"),
	
	WMX("wmx", "视频", "video/x-ms-wmx"),
	
	WVX("wvx", "视频", "video/x-ms-wvx");
	
	VideoType (String code, String name, String type) {
		this.code = code;
		this.name = name;
		this.type = type;
	}
	
	private String code;
	
	private String name;
	
	private String type;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

}