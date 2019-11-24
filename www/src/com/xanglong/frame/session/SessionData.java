package com.xanglong.frame.session;

import javax.servlet.http.HttpServletRequest;

import com.xanglong.i18n.Language;

public class SessionData {

	SessionData(HttpServletRequest request) {
		this.id = request.getSession().getId();
		this.language = Language.ZH_CN;
		this.requestStart = System.currentTimeMillis();
	}
	
	/**会话ID*/
	private String id;
	
	/**语言*/
	private Language language;
	
	/**请求开始时间*/
	private long requestStart;
	
	/**请求结束时间*/
	private long requestEnd;
	
	public String getId() {
		return id;
	}

	public Language getLanguage() {
		return language;
	}

	public long getRequestStart() {
		return requestStart;
	}

	public long getRequestEnd() {
		return requestEnd;
	}

	public void setRequestEnd(long requestEnd) {
		this.requestEnd = requestEnd;
	}

}