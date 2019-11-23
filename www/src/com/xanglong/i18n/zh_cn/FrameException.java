package com.xanglong.i18n.zh_cn;

import com.xanglong.frame.exception.IException;
import com.xanglong.i18n.I18n;

public enum FrameException implements IException, I18n {
	
	FRAME_CAN_NOT_FIND_USER_AGENT("找不到请求的用户代理"),
	;
	
	private String code;
	
	private String message;
	
	private FrameException(String message) {
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