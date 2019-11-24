package com.xanglong.frame.config;

public class Database {

	/**数据库名称*/
	private String name;

	/**数据库类型*/
	private String type;

	/**数据库地址*/
	private String host;

	/**初始化连接数*/
	private int initialSize;

	/**数据库密码*/
	private String password;

	/**数据库端口号*/
	private String port;

	/**数据库用户名称*/
	private String username;

	/**数据库驱动*/
	private String driver;

	/**数据库连接*/
	private String url;

	/**连接超时时间*/
	private long waitTimeout;
	
	/**完整链接*/
	private String fullUrl;

	/**总连接数量*/
	private int totalConnSize;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getWaitTimeout() {
		return waitTimeout;
	}

	public void setWaitTimeout(long waitTimeout) {
		this.waitTimeout = waitTimeout;
	}

	public int getTotalConnSize() {
		return totalConnSize;
	}

	public void setTotalConnSize(int totalConnSize) {
		this.totalConnSize = totalConnSize;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
	
}