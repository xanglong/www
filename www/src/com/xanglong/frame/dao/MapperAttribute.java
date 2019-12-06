package com.xanglong.frame.dao;

public enum MapperAttribute {

	ID("id", "文件内唯一ID"),
	NAMESPACE("namespace", "命名空间"),
	REFID("refid", "节点ID"),
	ITEM("item", "集合子项"),
	INDEX("index", "集合子项索引"),
	COLLECTION("collection", "集合"),
	OPEN("open", "开始"),
	CLOSE("close", "结束"),
	SEPARATOR("separator", "分隔符"),
	TEST("test", "boolean表达式"),
	RESULTMAP("resultMap", "结果集"),
	PARAMETERTYPE("parameterType", "参数类型"),
	KEYPROPERTY("keyProperty", "key名"),
	ORDER("order", "命令"),
	RESULTTYPE("resultType", "返回类型")
	;

	private String code;

	private String name;

	MapperAttribute(String code, String name) {
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