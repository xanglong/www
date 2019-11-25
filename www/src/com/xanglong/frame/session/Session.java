package com.xanglong.frame.session;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xanglong.frame.Current;
import com.xanglong.frame.Sys;
import com.xanglong.frame.util.DateUtil;


/**会话信息*/
public class Session {
	
	private static Map<String, SessionData> sessionMap = new HashMap<>();
	
	/**开始会话*/
	public static void start(HttpServletRequest request) {
		String id = request.getSession().getId();
		SessionData sessionData = sessionMap.get(id);  
		if (sessionData == null) {
			sessionData = new SessionData(request);
			sessionMap.put(id, sessionData);
		}
		//绑定当前会话信息到当前线程上
		Current.setSessionId(id);
	}
	
	/**结束会话*/
	public static void end(HttpServletRequest request) {
		SessionData sessionData = Current.getSession();
		if (sessionData == null) {
			return;
		}
		long requestStart = sessionData.getRequestStart();
		long requestEnd = System.currentTimeMillis();
		sessionData.setRequestEnd(requestEnd);
		if (Sys.getConfig().getIsDebug()) {
			System.out.println(DateUtil.getDateTime() + " " + request.getRequestURI() + " " + (requestEnd - requestStart) + "ms ");
		}
	}

	/**获取会话信息*/
	public static SessionData getSession(String id) {
		return sessionMap.get(id);
	}

}