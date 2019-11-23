package com.xanglong.frame;

import com.xanglong.frame.session.Session;
import com.xanglong.frame.session.SessionData;

public class Current {

	/**缓存当前会话ID*/
	private static ThreadLocal<String> sessionIdCache = new ThreadLocal<>();

	public static String getSessionId() {
		return sessionIdCache.get();
	}
	
	public static void setSessionId(String id) {
		sessionIdCache.set(id);
	}

	public static SessionData getSession() {
		return Session.getSession(sessionIdCache.get());
	}
	
}