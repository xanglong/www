package com.xanglong.frame.mvc;

public enum BeanType {

	CONTROLLER("controller", "控制类"),
	SERVICE("service", "业务类"),
	REPOSITORY("dao", "数据操作类"),
	COMPONENT("component", "部件"),
	OTHER("other", "其他类"),
	;

	private String code;

	private String name;

	BeanType(String code, String name) {
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