package com.xanglong.frame.dao;

public enum MapperTag {

	MAPPER("mapper", "根"),
	SQL("sql", "SQL语句"),
	INSERT("insert", "新增SQL语句"),
	UPDATE("update", "编辑SQL语句"),
	DELETE("delete", "删除SQL语句"),
	SELECT("select", "查询SQL语句"),
	INCLUDE("include", "包含"),
	WHERE("where", "where语句"),
	IF("if", "判断语句"),
	SELECTKEY("selectKey", "查询key值"),
	FOREACH("foreach", "遍历集合"),
	;

	private String code;

	private String name;

	MapperTag(String code, String name) {
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