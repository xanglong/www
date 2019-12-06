package com.xanglong.frame.dao;

public enum SqlType {

	INSERT("insert", "新增"),
	UPDATE("update", "编辑"),
	DELETE("delete", "删除"),
	SELECT("select", "查询")
	;

	private String code;

	private String name;

	SqlType(String code, String name) {
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