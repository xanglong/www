package com.xanglong.frame.net;

public class Email {
	
	public Email(String personal, String receive, String subject, String content) {
		this.receive = receive;
		this.subject = subject;
		this.content = content;
		this.personal = personal;
	}
	
	/**发件人昵称*/
	private String personal;
	
	/**收件人*/
	private String receive;
	
	/**主题*/
	private String subject;
	
	/**正文*/
	private String content;

	public String getPersonal() {
		return personal;
	}

	public String getReceive() {
		return receive;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

}