package com.xanglong.frame;

import com.xanglong.frame.net.SourceInfo;
import com.xanglong.frame.session.Session;
import com.xanglong.frame.session.SessionData;

public class Current {

	/**缓存当前会话ID*/
	private static ThreadLocal<String> sessionIdCache = new ThreadLocal<>();
	
	/**当前请求是否是动作请求*/
	private static ThreadLocal<SourceInfo> sourceInfoCache = new ThreadLocal<>();

	public static String getSessionId() {
		return sessionIdCache.get();
	}
	
	public static void setSessionId(String id) {
		sessionIdCache.set(id);
	}

	public static SessionData getSession() {
		return Session.getSession(sessionIdCache.get());
	}
	
	public static SourceInfo getSourceInfo() {
		return sourceInfoCache.get();
	}
	
	public static void setSourceInfo(SourceInfo sourceInfo) {
		sourceInfoCache.set(sourceInfo);
	}
	
}