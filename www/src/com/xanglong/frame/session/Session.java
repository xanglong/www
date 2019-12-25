package com.xanglong.frame.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xanglong.frame.Current;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Config;
import com.xanglong.frame.config.Proxy;
import com.xanglong.frame.io.RedisUtil;
import com.xanglong.frame.key.RedisKey;
import com.xanglong.frame.util.DateUtil;


/**会话信息*/
public class Session {
	
	private static Map<String, MySession> sessionMap = new HashMap<>();
	
	/**
	 * 开始会话
	 * @param request 请求对象
	 * */
	public static void start(HttpServletRequest request) {
		//创建当前线程会话信息
		Context context = new Context(request);
		//把当前线程会话信息塞入到线程中
		Current.setContext(context);
		String sessionId = context.getSessionId();
		//如果会话信息没有构建过，则构建会话信息
		if (!sessionMap.containsKey(sessionId)) {
			Proxy proxy = Sys.getConfig().getProxy();
			MySession mySession = null;
			//如果是多机部署，则先从redis中获取一下，因为可能是别的机器已经生成会话信息
			if (proxy.getIsOpen()) {
				Object object = RedisUtil.get(RedisKey.SESSION + sessionId);
				if (object == null) {
					//如果redis中也没有会话信息，则创建会话信息，并塞入到redis中
					mySession = new MySession(request);
					//把会话信息同步到redis中去
					RedisUtil.set(RedisKey.SESSION + sessionId, mySession);
				} else {
					mySession = (MySession) object;
				}
				//就只把会话信息的ID塞入，以此标记可以从redis获取到会话信息
				sessionMap.put(sessionId, null);
			} else {
				//如果是单机部署，那么没有会话信息就需要创建会话信息
				mySession = sessionMap.get(sessionId);
				if (mySession == null) {
					mySession = new MySession(request);
					//单机部署，会话信息塞入整个，存到本地缓存
					sessionMap.put(sessionId, mySession);
				}
			}
		}
		//绑定当前会话信息到当前线程上
		Current.setSessionId(sessionId);
	}
	
	/**
	 * 结束会话
	 * @param request 请求对象
	 * */
	public static void end(HttpServletRequest request) {
		Context context = Current.getContext();
		if (context == null) {
			return;
		}
		long requestStart = context.getRequestStart();
		long requestEnd = System.currentTimeMillis();
		context.setRequestEnd(requestEnd);
		Config config = Sys.getConfig();
		if (config.getIsDebug()) {
			String url = config.getBaseUrl() + request.getRequestURI();
			long timeCost = requestEnd - requestStart;
			System.out.println(DateUtil.getTimeMillis(new Date()) + " " + url + " " + timeCost + "ms ");
		}
	}

	/**
	 * 获取会话信息
	 * @param id 会话ID
	 * @return 会话信息
	 * */
	public static MySession getSession(String id) {
		Proxy proxy = Sys.getConfig().getProxy();
		if (proxy.getIsOpen()) {
			//多机部署就从Redis获取会话信息
			Object object = RedisUtil.get(RedisKey.SESSION + id);
			if (object != null) {
				return (MySession) object;
			}
		}
		return sessionMap.get(id);
	}
	
	/**
	 * 多机部署时同步会话信息到redis中
	 * @param mySession 会话信息
	 * */
	protected static void updateSession(MySession mySession) {
		Proxy proxy = Sys.getConfig().getProxy();
		if (proxy.getIsOpen()) {
			RedisUtil.set(RedisKey.SESSION + mySession.getSessionId(), mySession);
		}
	}

}