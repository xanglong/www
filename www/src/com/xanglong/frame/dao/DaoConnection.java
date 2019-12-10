package com.xanglong.frame.dao;

import java.sql.Connection;

/**数据库连接实体*/
public class DaoConnection {

	/**数据库连接对象*/
	private Connection connection;

	/**连接创建时间*/
	private long time;

	/**是否开启事务*/
	private boolean isBegin;

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

}