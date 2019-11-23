package com.xanglong.frame.session;

import javax.servlet.http.HttpServletRequest;

import com.xanglong.i18n.Language;

public class SessionData {

	SessionData(HttpServletRequest request) {
		this.id = request.getSession().getId();
		this.language = Language.ZH_CN;
		this.uri = request.getRequestURI();
	}
	
	/**会话ID*/
	private String id;
	
	/**语言*/
	private Language language;
	
	/**请求链接*/
	private String uri;

	public String getId() {
		return id;
	}

	public Language getLanguage() {
		return language;
	}

	public String getUri() {
		return uri;
	}

}