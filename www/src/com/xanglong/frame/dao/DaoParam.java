package com.xanglong.frame.dao;

import com.alibaba.fastjson.JSONObject;

public class DaoParam {
	
	/**SQL语句*/
	private String sql;

	/**前端入参*/
	private JSONObject webParams;

	/**分页参数*/
	private	PageParam pageParam;
	
	/**可执行SQL*/
	private String exeSql;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public JSONObject getWebParams() {
		return webParams;
	}

	public void setWebParams(JSONObject webParams) {
		this.webParams = webParams;
	}

	public PageParam getPageParam() {
		return pageParam;
	}

	public void setPageParam(PageParam pageParam) {
		this.pageParam = pageParam;
	}

	public String getExeSql() {
		return exeSql;
	}

	public void setExeSql(String exeSql) {
		this.exeSql = exeSql;
	}

}