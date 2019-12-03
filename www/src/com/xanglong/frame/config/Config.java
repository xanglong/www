package com.xanglong.frame.config;

public class Config {

	/**是否调试模式*/
	private boolean isDebug;
	
	/**登陆超时时间(毫秒)*/
	private long loginTimeout;
	
	/**基础地址*/
	private String baseUrl;
	
	/**数据库配置*/
	private Database database;
	
	/**文件下载地址前缀*/
	private String downloadUriPrefix;
	
	/**文件存储地址*/
	private String dataPath;
	
	/**浏览器资源文件缓存失效时间*/
	private long browseCacheExpireTime;
	
	/**模板引擎配置*/
	private Template template;
	
	/**邮件配置*/
	private Mail mail;
	
	/**错误页*/
	private ErrorPages errorPages;
	
	/**回调重定向地址参数名称*/
	private String redirectUriKey;
	
	/**登陆地址*/
	private String loginUri;
	
	/**日志配置*/
	private Log log;
	
	/**代理配置*/
	private Proxy proxy;
	
	/**包扫描配置*/
	private Packages packages;
	
	public boolean getIsDebug() {
		return isDebug;
	}

	public void setIsDebug(boolean isDebug) {
		this.isDebug = isDebug;
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

	public String getDownloadUriPrefix() {
		return downloadUriPrefix;
	}

	public void setDownloadUriPrefix(String downloadUriPrefix) {
		this.downloadUriPrefix = downloadUriPrefix;
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public long getBrowseCacheExpireTime() {
		return browseCacheExpireTime;
	}

	public void setBrowseCacheExpireTime(long browseCacheExpireTime) {
		this.browseCacheExpireTime = browseCacheExpireTime;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

	public ErrorPages getErrorPages() {
		return errorPages;
	}

	public void setErrorPages(ErrorPages errorPages) {
		this.errorPages = errorPages;
	}

	public String getRedirectUriKey() {
		return redirectUriKey;
	}

	public void setRedirectUriKey(String redirectUriKey) {
		this.redirectUriKey = redirectUriKey;
	}

	public String getLoginUri() {
		return loginUri;
	}

	public void setLoginUri(String loginUri) {
		this.loginUri = loginUri;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public Packages getPackages() {
		return packages;
	}

	public void setPackages(Packages packages) {
		this.packages = packages;
	}

}