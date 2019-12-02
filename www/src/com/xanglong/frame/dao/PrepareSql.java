package com.xanglong.frame.dao;

/**预编译SQL实体*/
public class PrepareSql {

	/**参数key数组*/
	private String[] keys;

	/**预编译SQL*/
	private String preSql;
	
	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String getPreSql() {
		return preSql;
	}

	public void setPreSql(String preSql) {
		this.preSql = preSql;
	}

}