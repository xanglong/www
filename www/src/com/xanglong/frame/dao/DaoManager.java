package com.xanglong.frame.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;

import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.config.Database;
import com.xanglong.frame.exception.BizException;
import com.xanglong.i18n.zh_cn.FrameException;

/**数据库连接管理类*/
public class DaoManager {
	
	/**数据库连接池*/
	private static Stack<ConnectionData> connections = new Stack<>();

	/**
	 * 创建连接池管理单例
	 * */
	public void init() {
		Database database = Sys.getConfig().getDatabase();
		String type = database.getType();
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			database.setFullUrl("jdbc:mysql://" + database.getHost() + ":" + database.getPort() + "/" + database.getName()
				+ "?user=" + database.getUsername() + "&password=" + database.getPassword() + "&serverTimezone=GMT%2B8&useSSL=false");
			String sql = "SHOW VARIABLES LIKE 'wait_timeout'";
			try (Connection connection = getNewConnection().getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery();
			){
				while (resultSet.next()) {
					String value = resultSet.getString(Const.MYSQL_VARIABLES_VALUE);
					database.setWaitTimeout(Integer.valueOf(value) * 1000);
				}
			} catch (SQLException e) {
				throw new BizException(e);
			}
			int initialSize = database.getInitialSize();
			for (int i = 0; i < initialSize; i++) {
				connections.add(getNewConnection());
			}
			database.setTotalConnSize(initialSize);
		} else {
			throw new BizException(FrameException.FRAME_DATABASE_TYPE_INVALID, type);
		}
	}
	
	/**
	 * 创建一个新的连接
	 * @return 一个数据库连接信息对象
	 * */
	private static ConnectionData getNewConnection() {
		Database database = Sys.getConfig().getDatabase();
		Connection connection = null;
		try {
			Class.forName(database.getDriver());
			connection = DriverManager.getConnection(database.getFullUrl());
		} catch (ClassNotFoundException | SQLException e) {
			throw new BizException(e);
		}
		ConnectionData connectionData = new ConnectionData();
		connectionData.setConnection(connection);
		connectionData.setTime(System.currentTimeMillis());
		return connectionData;
	}

	/**
	 * 获取一个连接
	 * @return 一个数据库连接信息对象
	 * */
	public static synchronized ConnectionData getConnection() {
		Database database = Sys.getConfig().getDatabase();
		ConnectionData connectionData = null;
		if (connections.size() > 0) {
			connectionData = connections.pop();
			long lastTime = connectionData.getTime();
			if (System.currentTimeMillis() - lastTime > database.getWaitTimeout()) {
				connectionData = getNewConnection();
			}
		} else {
			connectionData = getNewConnection();
			database.setTotalConnSize(database.getTotalConnSize() + 1);
		}
		return connectionData;
	}

	/**
	 * 释放一个链接
	 * @param connectionData 一个数据库连接信息对象
	 * */
	protected static void freeConnection(ConnectionData connectionData) {
		connectionData.setTime(System.currentTimeMillis());
		connections.push(connectionData);
	}

}