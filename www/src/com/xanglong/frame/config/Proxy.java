package com.xanglong.frame.config;

public class Proxy {
	
	/**代理是否开启*/
	private boolean isOpen;
	
	/**当前是否是代理服务器*/
	private boolean isProxy;
	
	/**代理服务通信凭证，设置一个随意长度的字符串*/
	private String authorization;
	
	/**当前服务器IP*/
	private String ip;
	
	/**可转发的服务器地址，这里用HTTP转发，本项目用的tomcat可以用AJP做集群*/
	private String[] hosts;

	public boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public boolean getIsProxy() {
		return isProxy;
	}

	public void setIsProxy(boolean isProxy) {
		this.isProxy = isProxy;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

}