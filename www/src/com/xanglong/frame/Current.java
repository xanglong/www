package com.xanglong.frame;

import com.xanglong.frame.config.Database;
import com.xanglong.frame.dao.ConnectionData;
import com.xanglong.frame.dao.DaoManager;
import com.xanglong.frame.net.SourceInfo;
import com.xanglong.frame.session.Session;
import com.xanglong.frame.session.SessionData;

public class Current {

	/**缓存当前会话ID*/
	private static ThreadLocal<String> sessionIdCache = new ThreadLocal<>();
	
	/**当前请求是否是动作请求*/
	private static ThreadLocal<SourceInfo> sourceInfoCache = new ThreadLocal<>();
	
	/**当前数据库链接*/
	private static ThreadLocal<ConnectionData> connectionDataCahe = new ThreadLocal<>();

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
	
	public static ConnectionData getConnection() {
		ConnectionData connectionData = connectionDataCahe.get();
		if (connectionData == null) {
			connectionData = DaoManager.getConnection();
			connectionDataCahe.set(connectionData);
		} else {
			Database database = Sys.getConfig().getDatabase();
			long lastTime = connectionData.getTime();
			if (System.currentTimeMillis() - lastTime > database.getWaitTimeout()) {
				connectionData = DaoManager.getConnection();
				connectionDataCahe.set(connectionData);
			}
		}
		return connectionData;
	}
	
}