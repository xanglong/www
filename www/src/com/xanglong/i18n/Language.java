package com.xanglong.i18n;

public enum Language {

	ZH_CN("简体中文");

	private String cnName;

	private Language(String cnName) {
		this.cnName = cnName;
	}

	public String getCode() {
		return this.cnName;
	}

}