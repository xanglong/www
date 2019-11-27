package com.xanglong.i18n.zh_cn;

import com.xanglong.frame.exception.IException;
import com.xanglong.i18n.I18n;

public enum FrameException implements IException, I18n {
	
	FRAME_CAN_NOT_FIND_USER_AGENT("找不到请求的用户代理"),
	FRAME_CAN_NOT_FIND_CONFIG_FOLDER("找不到配置文件夹：{0}"),
	FRAME_CONFIG_FILE_INVALID("配置文件无效：{0}"),
	FRAME_UNSUPPORTED_SOURCE_TYPE("不支持的请求资源类型：{0},请求地址：{1}"),
	FRAME_CONT_NOT_FIND_FILE("找不到请求的文件：{0}"),
	FRAME_URL_ILLEGAL("URL地址非法：{0}"),
	FRAME_CONTENT_TYPE_CANT_NOT_NULL("接口请求文档类型不能为空"),
	FRAME_REQUEST_PARAM_CANT_NOT_NULL("接口请求参数不能为空")
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