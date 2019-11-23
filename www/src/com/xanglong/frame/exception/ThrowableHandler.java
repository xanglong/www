package com.xanglong.frame.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.net.ContentType;
import com.xanglong.frame.net.HttpUtil;
import com.xanglong.frame.net.Method;
import com.xanglong.frame.net.Result;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.SystemException;

public class ThrowableHandler {
	
	/**处理异常*/
	public static void dealException(Throwable throwable) {
		dealException(throwable, null, null);
	}
	
	/**处理异常*/
	public static void dealException(Throwable throwable, HttpServletRequest request, HttpServletResponse response) {
		String code = null, message = null;
		boolean isSendMail = false;//是否发送邮件，到时候分线上和线下，然后走配置文件决定
		boolean isDebug = true;//是否是开发调试模式
		boolean isResponse = true;//是否要继续响应
		Throwable superThrowable = throwable.getCause();
		if (superThrowable != null) {
			throwable = superThrowable;
		}
		if (throwable instanceof BizException) {
			//业务异常是否发送邮件与开发模式和线上运行模式相关
			isSendMail = !isDebug;
			BizException bizException = (BizException) throwable;
			code = bizException.getCode();
			message = bizException.getMessage();
			if (SystemException.E304.name().equals(code)) {
				//在抓包的时候有些请求会返回304状态码，是一种文件缓存技术处理，大多是前端缓存
				HttpUtil.responseText(response, ContentType.FORM_TEXT, "");
			} else if (SystemException.E401.name() == code) {
				//需要登陆或者指定身份才可以访问的异常
				isResponse = false;
				//TODO 登录页
				HttpUtil.forward(request, response, "");
			} else if (SystemException.E404.name() == code) {
				if (Method.GET.name().equals(request.getMethod()) && response != null) {
					isResponse = false;
					//TODO 404页
					HttpUtil.forward(request, response, "");
				}
			}
		} else if (throwable instanceof Exception) {
			code = "500";
			isSendMail = true;
			message = throwable.getMessage();
		} else if (throwable instanceof Error) {
			code = "-500";
			message = throwable.getMessage();
		}
		if (isDebug) {
			throwable.printStackTrace();
		}
		if (isSendMail) {
			//TODO 发邮件
		}
		if (isResponse && response != null) {
			Result result = new Result();
			result.setSuccess(false);
			result.setCode(code);
			message = StringUtil.isBlank(message) ? SystemException.E500.getMessage() : message;
			result.setMessage(message);
			HttpUtil.responseText(response, ContentType.JSON, JSONObject.toJSONString(result));
		}
	}

}