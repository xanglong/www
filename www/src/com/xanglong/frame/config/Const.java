package com.xanglong.frame.config;

import java.nio.charset.Charset;

public class Const {

	/**默认编码格式*/
	public static final String CHARSET_STR = "UTF-8";
	
	/**缓冲区默认独写一次大小8K*/
	public static final int BUFFER_SIZE = 8192;
	
	/**默认编码格式*/
	public static final Charset CHARSET = Charset.forName(CHARSET_STR);
	
	/**静态脚本前缀*/
	public static final String STATIC_JS_PREFIX = "/**/";
	
	/**静态样式前缀*/
	public static final String STATIC_CSS_PREFIX = "/**/";
	
	/**静态网页前缀*/
	public static final String STATIC_HTML_PREFIX = "<!---->";
	
	/**数据库参数值*/
	public static final String MYSQL_VARIABLES_VALUE = "Value";
	
	/**配置文件夹*/
	public static final String CONFIG_FOLDER_NAME = "config";
	public static final String DTD_FOLDER_NAME = "dtd";
	
	/**配置文件名称*/
	public static final String CONFIG_PROPERTIES = "config.properties";
	public static final String UEDITOR_PROPERTIES = "ueditor.properties";
	
}