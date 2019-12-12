package com.xanglong.frame;

import java.util.Stack;

import com.xanglong.frame.config.Database;
import com.xanglong.frame.dao.DaoConnection;
import com.xanglong.frame.dao.DaoManager;
import com.xanglong.frame.mvc.BeanType;
import com.xanglong.frame.net.SourceInfo;
import com.xanglong.frame.session.Session;
import com.xanglong.frame.session.SessionData;

public class Current {

	/**缓存当前会话ID*/
	private static ThreadLocal<String> sessionIdCache = new ThreadLocal<>();
	
	/**当前请求是否是动作请求*/
	private static ThreadLocal<SourceInfo> sourceInfoCache = new ThreadLocal<>();
	
	/**当前数据库链接*/
	private static ThreadLocal<DaoConnection> daoConnectionCache = new ThreadLocal<>();
	
	/**记录MVC切面栈*/
	private static ThreadLocal<Stack<BeanType>> aopStackCahce = new ThreadLocal<>();

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

	/**获取数据库连接*/
	public static DaoConnection getConnection() {
		DaoConnection connectionData = daoConnectionCache.get();
		if (connectionData == null) {
			connectionData = DaoManager.getConnection();
			daoConnectionCache.set(connectionData);
		} else {
			Database database = Sys.getConfig().getDatabase();
			long lastTime = connectionData.getTime();
			if (System.currentTimeMillis() - lastTime > database.getWaitTimeout()) {
				connectionData = DaoManager.getConnection();
				daoConnectionCache.set(connectionData);
			}
		}
		return connectionData;
	}
	
	/**进入切面*/
	public static void aopEnter(BeanType beanType) {
		Stack<BeanType> aopStack = aopStackCahce.get();
		if (aopStack == null) {
			aopStack = new Stack<>();
			aopStackCahce.set(aopStack);
		}
		aopStack.push(beanType);
	}
	
	/**离开切面*/
	public static BeanType aopExit() {
		Stack<BeanType> aopStack = aopStackCahce.get();
		if (aopStack == null || aopStack.isEmpty()) {
			return null;
		}
		return aopStack.pop();
	}
	
	/**获取当前切面*/
	public static BeanType getAop() {
		Stack<BeanType> aopStack = aopStackCahce.get();
		if (aopStack == null || aopStack.isEmpty()) {
			return null;
		}
		return aopStack.firstElement();
	}

}