package com.xanglong.frame.net;

public class Header {

	public static final String USER_AGENT = "user-agent";//请求头key,设备类型
	public static final String CONTENT_LENGTH = "content-length";//请求头key,文档长度
	public static final String CONTENT_DISPOSITION = "Content-Disposition";//文档描述
	public static final String CONTENT_TYPE = "content-type";//请求头key,文档类型
	public static final String ENCODING = "encoding";//请求头key,编码格式
	public static final String X_REQUESTED_WITH = "x_requested_with";//是否是Ajax请求
	public static final String MULTIPART = "multipart/";//多文件请求
	public static final String REFERER = "referer";//请求来源
	public static final String ORIGIN = "origin";//原始地址
	public static final String CACHE_CONTROL = "cache-Control";//缓存控制
	public static final String LAST_MODIFIED = "last-modified";//上次修改时间
	public static final String EXPRIES = "expries";//缓存有效期
	public static final String IF_MODIFIED_SINCE = "if-modified-since";//上次修改时间
	public static final String X_FORWARDED_FOR = "x-forwarded-for";
	public static final String PROXY_CLIENT_IP = "proxy-client-ip";
	public static final String WL_PROXY_CLIENT_IP = "wl-proxy-client-ip";
	public static final String COOKIE = "Cookie";

}