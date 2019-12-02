package com.xanglong.frame.dao;

import com.alibaba.fastjson.JSONArray;

public class DaoData {
	
	/**总行数*/
	private int totalCount;
	
	/**数据*/
	private JSONArray datas;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public JSONArray getDatas() {
		return datas;
	}

	public void setDatas(JSONArray datas) {
		this.datas = datas;
	}

}