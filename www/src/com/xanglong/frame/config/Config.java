package com.xanglong.frame.config;

public class Config {

	/**是否调试模式*/
	private boolean isDebug;
	
	/**网络协议*/
	private String protocol;
	
	/**登陆超时时间(毫秒)*/
	private long loginTimeout;
	
	/**基础地址*/
	private String baseUrl;
	
	/**数据库配置*/
	private Database database;
	
	public boolean getIsDebug() {
		return isDebug;
	}

	public void setIsDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public long getLoginTimeout() {
		return loginTimeout;
	}

	public void setLoginTimeout(long loginTimeout) {
		this.loginTimeout = loginTimeout;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Database getDatabase() {
		return database;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

}