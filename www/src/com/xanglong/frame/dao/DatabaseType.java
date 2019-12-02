package com.xanglong.frame.dao;

/**数据库类型枚举*/
public enum DatabaseType {
	
	MYSQL("mysql", "MySQL"),
	ORACLE("oracle", "Oracle");

	private String code;

	private String name;

	DatabaseType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

}