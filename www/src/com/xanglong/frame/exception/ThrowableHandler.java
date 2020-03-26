package com.xanglong.frame.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Config;
import com.xanglong.frame.log.Logger;
import com.xanglong.frame.net.ContentType;
import com.xanglong.frame.net.HttpUtil;
import com.xanglong.frame.net.MailUtil;
import com.xanglong.frame.net.Method;
import com.xanglong.frame.net.Result;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.SystemException;

public class ThrowableHandler {
	
	/**
	 * 处理异常
	 * @param throwable 可抛出异常对象
	 * */
	public static void dealException(Throwable throwable) {
		dealException(throwable, null, null);
	}
	
	/**
	 * 处理异常
	 * @param throwable 可抛出异常对象
	 * @param request 请求对象
	 * @param response 响应对象
	 * */
	public static void dealException(Throwable throwable, HttpServletRequest request, HttpServletResponse response) {
		while (throwable instanceof BizException) {
			BizException bizException = (BizException) throwable;
			//处理系统异常抛出的错误，需要获取系统异常信息
			Throwable innerThrowable = bizException.getThrowable();
			if (innerThrowable == null) {
				dealBizException(bizException, request, response);
				//发送邮件
				if (bizException.getSendMail()) {
					MailUtil.send(bizException);
				}
				break;
			} else {
				throwable = innerThrowable;
			}
		}
		if (throwable instanceof Exception) {
			dealException((Exception) throwable, request, response);
		} else if (throwable instanceof Error) {
			dealError((Error) throwable, request, response);
		}
		if (Sys.getConfig().getIsDebug()) {
			throwable.printStackTrace();
		}
	}
	
	/**
	 * 处理系统错误
	 * @param bizException 业务异常
	 * @param request 请求对象
	 * @param response 响应对象
	 * @return 是否继续响应
	 * */
	private static void dealError(Error error, HttpServletRequest request, HttpServletResponse response) {
		Logger.error(error);
		//只要是系统异常就发送邮件记录下
		MailUtil.send(error);
		String message = error.getMessage();
		message = StringUtil.isBlank(message) ? SystemException.E500.getMessage() : message;
		responseResult("-500", message, response);
	}
	
	/**
	 * 处理系统异常
	 * @param bizException 业务异常
	 * @param request 请求对象
	 * @param response 响应对象
	 * @return 是否继续响应
	 * */
	private static void dealException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
		Config config = Sys.getConfig();
		if (config.getLog().getIsSystemException()) {
			Logger.danger(exception);
		}
		if (!config.getIsDebug()) {
			MailUtil.send(exception);
		}
		String message = exception.getMessage();
		message = StringUtil.isBlank(message) ? SystemException.E500.getMessage() : message;
		responseResult("500", exception.getMessage(), response);
	}
	
	/**
	 * 处理业务异常
	 * @param bizException 业务异常
	 * @param request 请求对象
	 * @param response 响应对象
	 * @return 是否继续响应
	 * */
	private static void dealBizException(BizException bizException, HttpServletRequest request, HttpServletResponse response) {
		boolean isResponse = true;
		Config config = Sys.getConfig();
		if (config.getLog().getIsBizException()) {
			Logger.warn(bizException);
		}
		String code = bizException.getCode();
		String message = bizException.getMessage();
		if (SystemException.E304.name().equals(code)) {
			isResponse = false;
			//在抓包的时候有些请求会返回304状态码，是一种文件缓存技术处理，大多是前端缓存
			HttpUtil.responseText(response, ContentType.FORM_TEXT, "");
		} else if (SystemException.E401.name() == code) {
			//需要登陆或者指定身份才可以访问的异常
			isResponse = false;
			String loginUrl = config.getLoginUri();
			//特殊处理，需要登陆才可以访问的页面，异常消息为回调地址
			if (!StringUtil.isBlank(message)) {
				loginUrl += "?" + config.getRedirectUriKey() + "=" + message;
			}
			HttpUtil.redirect(request, response, loginUrl);
		} else if (SystemException.E404.name() == code) {
			if (Method.GET.name().equals(request.getMethod()) && response != null) {
				isResponse = false;
				HttpUtil.redirect(request, response, config.getErrorPages().getE404());
			}
		}
		if (isResponse) {
			responseResult(code, message, response);
		}
	}
	
	/**
	 * 返回错误信息
	 * @param code 错误编码
	 * @param message 错误信息
	 * @param response 响应对象
	 * */
	private static void responseResult(String code, String message, HttpServletResponse response) {
		if (response != null) {
			Result result = new Result();
			result.setSuccess(false);
			result.setCode(code);
			result.setMessage(message);
			HttpUtil.responseText(response, ContentType.JSON, JSONObject.toJSONString(result));
		}
	}
	
}