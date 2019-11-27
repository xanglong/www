package com.xanglong.frame.net;

import java.nio.charset.Charset;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**请求对象*/
public class RequestDto {

	/**请求地址*/
	private String url;

	/**文档类型*/
	private ContentType contentType = ContentType.JSON;

	/**header参数*/
	private JSONObject headerParams;

	/**body参数*/
	private JSONObject bodyParams;

	/**二进制参数，如果传值则只按照bytes传参bodyParams失效*/
	private byte[] bytes;

	/**连接超时时间*/
	private int connectionTimeout = 30000;

	/**读取*/
	private int readTimeout = 30000;
	
	/**编码格式*/
	private Charset charset = Charset.defaultCharset();
	
	private List<RequestFileDto> files;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public JSONObject getHeaderParams() {
		return headerParams;
	}

	public void setHeaderParams(JSONObject headerParams) {
		this.headerParams = headerParams;
	}

	public JSONObject getBodyParams() {
		return bodyParams;
	}

	public void setBodyParams(JSONObject bodyParams) {
		this.bodyParams = bodyParams;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public List<RequestFileDto> getFiles() {
		return files;
	}

	public void setFiles(List<RequestFileDto> files) {
		this.files = files;
	}

}