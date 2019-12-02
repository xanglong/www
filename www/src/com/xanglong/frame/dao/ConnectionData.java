package com.xanglong.frame.dao;

import java.sql.Connection;

/**数据库连接实体*/
public class ConnectionData {

	/**数据库连接对象*/
	private Connection connection;

	/**连接创建时间*/
	private long time;

	/**是否开启事务*/
	private boolean isBegin;

	/**是否出现异常*/
	private boolean isError;
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean getIsBegin() {
		return isBegin;
	}

	public void setIsBegin(boolean isBegin) {
		this.isBegin = isBegin;
	}

	public boolean getIsError() {
		return isError;
	}

	public void setIsError(boolean isError) {
		this.isError = isError;
	}
	
}