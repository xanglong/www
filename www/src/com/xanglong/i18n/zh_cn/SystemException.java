package com.xanglong.i18n.zh_cn;

import com.xanglong.frame.exception.IException;
import com.xanglong.i18n.I18n;

public enum SystemException implements IException, I18n {
	
	E100("继续"),
	E101("切换协议"),
	E200("请求成功"),
	E201("已创建"),
	E202("已接受"),
	E203("非授权信息"),
	E204("无内容"),
	E205("重置内容"),
	E206("部分内容"),
	E300("多种选择"),
	E301("永久移动"),
	E302("临时移动"),
	E303("查看其它地址"),
	E304("未修改"),
	E305("使用代理"),
	E307("临时重定向"),
	E400("客户端请求的语法错误，服务器无法理解"),
	E401("请求要求用户的身份认证"),
	E403("服务器理解请求客户端的请求，但是拒绝执行此请求"),
	E404("服务器无法根据客户端的请求找到资源（网页）:{0}"),
	E405("客户端请求中的方法被禁止"),
	E406("服务器无法根据客户端请求的内容特性完成请求"),
	E407("请求要求代理的身份认证"),
	E408("服务器等待客户端发送的请求时间过长，超时"),
	E409("服务器处理请求时发生了冲突"),
	E410("客户端请求的资源已经不存在"),
	E411("服务器无法处理客户端发送的不带Content-Length的请求信息"),
	E412("客户端请求信息的先决条件错误"),
	E413("由于请求的实体过大，服务器无法处理，因此拒绝请求"),
	E414("请求的URI过长，服务器无法处理"),
	E415("服务器无法处理请求附带的媒体格式"),
	E416("客户端请求的范围无效"),
	E417("服务器无法满足预期的请求头信息"),
	E428("要求先决条件"),
	E429("太多请求"),
	E431("请求头字段太大"),
	E500("服务器内部错误，无法完成请求"),
	E501("服务器不支持请求的功能，无法完成请求"),
	E502("充当网关或代理的服务器，从远端服务器接收到了一个无效的请求"),
	E503("由于超载或系统维护，服务器暂时的无法处理客户端的请求"),
	E504("充当网关或代理的服务器，未及时从远端服务器获取请求"),
	E505("服务器不支持请求的HTTP协议的版本，无法完成处理"),
	E511("要求网络认证");
	
	private String code;
	
	private String message;
	
	private SystemException(String message) {
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