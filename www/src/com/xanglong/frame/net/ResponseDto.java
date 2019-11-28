package com.xanglong.frame.net;

import java.nio.charset.Charset;

import org.jsoup.helper.StringUtil;

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
	private JSONObject headerParams;
	
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

	public JSONObject getHeaderParams() {
		return headerParams;
	}

	public void setHeaderParams(JSONObject headerParams) {
		this.headerParams = headerParams;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * 不分区大小写获取响应头参数
	 * @param key 参数名
	 * @return 参数值
	 * */
	public String getHeader(String key) {
		if (!StringUtil.isBlank(key) && headerParams != null && !headerParams.isEmpty()) {
			for (String k : headerParams.keySet()) {
				if (k.toLowerCase().equals(key.toLowerCase())) {
					return headerParams.getString(k);
				}
			}
		}
		return "";
	}

}