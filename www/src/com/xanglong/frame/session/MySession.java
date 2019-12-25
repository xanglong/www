package com.xanglong.frame.session;

import javax.servlet.http.HttpServletRequest;

import com.xanglong.frame.key.HeaderKey;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.Language;

public class MySession {

	MySession(HttpServletRequest request) {
		this.sessionId = request.getSession().getId();
		//默认语言
		this.language = Language.ZH_CN;
		String languageString = request.getHeader(HeaderKey.LANGUAGE);
		if (!StringUtil.isBlank(languageString)) {
			for (Language language : Language.values()) {
				if (language.name().equals(languageString)) {
					this.language = language;
					break;
				}
			}
		}
	}
	
	/**会话ID*/
	private String sessionId;
	
	/**语言*/
	private Language language;
	
	public String getSessionId() {
		return sessionId;
	}

	public Language getLanguage() {
		return language;
	}

}