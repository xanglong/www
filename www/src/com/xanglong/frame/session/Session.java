package com.xanglong.frame.session;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xanglong.frame.Current;


/**会话信息*/
public class Session {
	
	private static Map<String, SessionData> sessionMap = new HashMap<>();
	
	/**开始会话*/
	public void start(HttpServletRequest request) {
		String id = request.getSession().getId();
		SessionData sessionData = sessionMap.get(id);  
		if (sessionData == null) {
			sessionData = new SessionData(request);
			sessionMap.put(id, sessionData);
		}
		//绑定当前会话信息到当前线程上
		Current.setSessionId(id);
	}

	/**获取会话信息*/
	public static SessionData getSession(String id) {
		return sessionMap.get(id);
	}

}