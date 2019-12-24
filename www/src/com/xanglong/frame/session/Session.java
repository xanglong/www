package com.xanglong.frame.session;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xanglong.frame.Current;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Config;
import com.xanglong.frame.config.Proxy;
import com.xanglong.frame.config.RedisKey;
import com.xanglong.frame.io.RedisUtil;
import com.xanglong.frame.util.DateUtil;


/**会话信息*/
public class Session {
	
	private static Map<String, SessionData> sessionMap = new HashMap<>();
	
	/**开始会话*/
	public static void start(HttpServletRequest request) {
		Proxy proxy = Sys.getConfig().getProxy();
		String id = request.getSession().getId();
		SessionData sessionData = null;
		//开启代理服务代表多机部署，否则就单机部署就行
		if (proxy.getIsOpen()) {
			//开启多机的就用Redis解决分布式会话问题
			Object object = RedisUtil.get(RedisKey.SESSION + id);
			if (object != null) {
				sessionData = (SessionData) object;
			} else {
				sessionData = new SessionData(request);
				RedisUtil.set(RedisKey.SESSION + id, sessionData);
			}
		} else {
			//单机部署就按照本地缓存处理就行
			sessionData = sessionMap.get(id);
			if (sessionData == null) {
				sessionData = new SessionData(request);
				sessionMap.put(id, sessionData);
			}
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
		Config config = Sys.getConfig();
		if (config.getIsDebug()) {
			System.out.println(DateUtil.getDateTime() + " " + config.getBaseUrl() + request.getRequestURI() + " " + (requestEnd - requestStart) + "ms ");
		}
	}

	/**获取会话信息*/
	public static SessionData getSession(String id) {
		Proxy proxy = Sys.getConfig().getProxy();
		if (proxy.getIsOpen()) {
			//多机部署就从Redis获取会话信息
			Object object = RedisUtil.get(RedisKey.SESSION + id);
			if (object != null) {
				return (SessionData) object;
			}
		}
		return sessionMap.get(id);
	}

}