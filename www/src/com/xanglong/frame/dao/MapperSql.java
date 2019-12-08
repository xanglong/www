package com.xanglong.frame.dao;

/**解析XML里的SQL后的对象*/
public class MapperSql {

	/**预编译SQL*/
	private String preSql;

	/**SQL类型*/
	private SqlType sqlType;

	public String getPreSql() {
		return preSql;
	}

	public void setPreSql(String preSql) {
		this.preSql = preSql;
	}

	public SqlType getSqlType() {
		return sqlType;
	}

	public void setSqlType(SqlType sqlType) {
		this.sqlType = sqlType;
	}

}