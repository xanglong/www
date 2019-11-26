package com.xanglong.frame.config;

public class Mail {

	/**账号*/
	private String account;

	/**密码*/
	private String password;

	/**地址*/
	private String host;

	/**端口*/
	private String port;
	
	/**邮件发件人称呼*/
	private String personal;
	
	/**异常邮件接收邮箱*/
	private String errorMailReceive;
	
	/**异常邮件主题名称*/
	private String errorMailSubject;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

	public String getErrorMailReceive() {
		return errorMailReceive;
	}

	public void setErrorMailReceive(String errorMailReceive) {
		this.errorMailReceive = errorMailReceive;
	}

	public String getErrorMailSubject() {
		return errorMailSubject;
	}

	public void setErrorMailSubject(String errorMailSubject) {
		this.errorMailSubject = errorMailSubject;
	}

}