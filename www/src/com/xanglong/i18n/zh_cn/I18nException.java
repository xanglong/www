package com.xanglong.i18n.zh_cn;

import com.xanglong.frame.exception.IException;
import com.xanglong.i18n.I18n;

public enum I18nException implements IException, I18n {
	
	I18N_UNREALIZED_I18N_INTERFACE("类没有实现国际化接口"),
	I18N_CAN_NOT_FIND_LANGUAGE_RESOURCE("找不到语言包资源"),
	I18N_CAN_NOT_FIND_LANGUAGE_MESSAGE("找不到指定的语言文本")
	;
	
	private String code;
	
	private String message;
	
	private I18nException(String message) {
		this.code = this.name();
		this.message = message;
	}

	public String getCode() {
		return this.code;
	}

	public String getMessage() {
		return this.message;
	}
	
	public String getName() {
		return this.message;
	}

}