package com.xanglong.frame.dao;

import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.entity.BasePage;

public class DaoParam {
	
	/**SQL语句*/
	private String sql;

	/**前端入参*/
	private JSONObject params;

	/**分页参数*/
	private	BasePage page;
	
	/**可执行SQL*/
	private String exeSql;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public JSONObject getParams() {
		return params;
	}

	public void setParams(JSONObject params) {
		this.params = params;
	}

	public BasePage getPage() {
		return page;
	}

	public void setPage(BasePage page) {
		this.page = page;
	}

	public String getExeSql() {
		return exeSql;
	}

	public void setExeSql(String exeSql) {
		this.exeSql = exeSql;
	}

}