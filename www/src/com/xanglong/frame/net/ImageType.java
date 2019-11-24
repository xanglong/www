package com.xanglong.frame.net;

/**图片类型枚举*/
public enum ImageType {
	
	TIFF("tiff", "标签", "image/tiff"),
	FAX("fax", "传真", "image/fax"),
	GIF("gif", "动态图", "image/gif"),
	ICO("ico", "标签", "image/x-icon"),
	JFIF("jfif", "图片", "image/jpeg"),
	JPE("jpe", "图片", "image/jpeg"),
	JPEG("jpeg", "图片", "image/jpeg"),
	JPG("jpg", "图片", "image/jpeg"),
	NET("net", "图片", "image/pnetvue"),
	PNG("png", "图片", "image/png"),
	RP("rp", "图片", "image/vnd.rn-realpix"),
	TIF("tif", "图片", "image/tiff"),
	WBMP("wbmp", "图片", "image/vnd.wap.wbmp"),
	BMP("bmp", "图片", "image/bmp")
	;
	
	ImageType (String code, String name, String type) {
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