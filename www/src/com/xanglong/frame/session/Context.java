package com.xanglong.frame.session;

import javax.servlet.http.HttpServletRequest;

/**只记录系统数据，不记录业务数据*/
public class Context {
	
	Context(HttpServletRequest request) {
		this.sessionId = request.getSession().getId();
		this.requestStart = System.currentTimeMillis();
	}

	/**会话ID*/
	private String sessionId;
	
	/**请求开始时间*/
	private long requestStart;
	
	/**请求结束时间*/
	private long requestEnd;

	public String getSessionId() {
		return sessionId;
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