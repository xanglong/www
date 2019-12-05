package com.xanglong.i18n.zh_cn;

import com.xanglong.frame.exception.IException;
import com.xanglong.i18n.I18n;

public enum FrameException implements IException, I18n {
	
	FRAME_USER_AGENT_NULL("找不到请求的用户代理"),
	FRAME_CONFIG_FOLDER_NULL("找不到配置文件夹：{0}"),
	FRAME_CONFIG_FILE_INVALID("配置文件无效：{0}"),
	FRAME_UNSUPPORTED_SOURCE_TYPE("不支持的请求资源类型：{0},请求地址：{1}"),
	FRAME_FILE_NULL("找不到请求的文件：{0}"),
	FRAME_URL_ILLEGAL("URL地址非法：{0}"),
	FRAME_CONTENT_TYPE_NULL("接口请求文档类型不能为空"),
	FRAME_REQUEST_PARAM_NULL("接口请求参数不能为空"),
	FRAME_PROXY_AUTHORIZATION_NULL("转发凭证不能为空"),
	FRAME_PROXY_AUTHORIZATION_INVALID("转发凭证无效：{0}"),
	FRAME_PROXY_IP_INVALID("当前请求IP非法：{0}"),
	FRAME_REQUEST_METHOD_INVALID("请求类型不支持：{0}"),
	FRAME_DATABASE_TYPE_INVALID("不支持的数据库类型：{0}"),
	FRAME_SQL_MISS_RIGHT_CURLY_BRACE("预编译SQL语句缺失右花括号"),
	FRAME_SQL_PARAM_NULL("SQL参数不能为空"),
	FRAME_SQL_MISS_PARAM("SQL缺失参数：{0}"),
	FRAME_CLASS_MISS_ANNOTATION_MYREPOSITORY("{0}：类缺少注解@MyRepository"),
	FRAME_CLASS_MISS_ANNOTATION_MYCONTROLLER("{0}：类缺少注解@MyController"),
	FRAME_CLASS_MISS_ANNOTATION_MYREQUESTMAPPING("{0}：类缺少注解@MyRequestMapping"),
	FRAME_METHOD_MISS_ANNOTATION_MYREQUESTMAPPING("{0}：方法缺少注解@MyRequestMapping"),
	FRAME_METHOD_ANNOTATION_MYREQUESTMAPPING_REPEAT("{0}：方法@yRequestMapping注解重复"),
	FRAME_CLASS_ANNOTATION_MYCONTROLLER_INVALID("{0}：类不支持引入有@MyController注解的Bean"),
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