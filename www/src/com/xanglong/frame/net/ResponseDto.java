package com.xanglong.frame.net;

import java.nio.charset.Charset;

import com.alibaba.fastjson.JSONObject;

/**响应对象*/
public class ResponseDto {

	/**文档类型*/
	private String contentType;

	/**二进制数据*/
	private byte[] bytes;

	/**编码格式*/
	private Charset charset = Charset.defaultCharset();
	
	/**响应头参数*/
	private JSONObject headers;
	
	/**请求地址,重定向后会变掉*/
	private String url;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public JSONObject getHeaders() {
		return headers;
	}

	public void setHeaders(JSONObject headers) {
		this.headers = headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}