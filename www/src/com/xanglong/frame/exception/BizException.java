package com.xanglong.frame.exception;

import com.xanglong.frame.Current;
import com.xanglong.frame.session.SessionData;
import com.xanglong.i18n.Language;
import com.xanglong.i18n.LanguageManager;

/**自定义异常处理类*/
public class BizException extends RuntimeException {

	private static final long serialVersionUID = 4180238855656427814L;

	private String code;

	private String message;
	
	private Throwable throwable;

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	/**自定义异常*/
	public BizException(IException iException, String...messages) {
		//默认异常用定义里面的异常，messages最好只是字母、输字和符号，否则国际化不动
		this.code = iException.getCode();
		SessionData sessionData = Current.getSession();
		if (sessionData == null) {
			//默认返回中文简体
			this.message = iException.getMessage();
		} else {
			//语言国际化处理
			Language language = sessionData.getLanguage();
			if (Language.ZH_CN == language) {
				this.message = iException.getMessage();
			} else {
				this.message = LanguageManager.getMessage(language, code);
			}
		}
		//如果遇到特殊情况需要重新替换的，则可以传入要替换的内容
		if (messages != null) {
			String msg = this.message;
			for (int i = 0; i < messages.length; i++) {
				msg = msg.replace("{" + i + "}", messages[i]);
			}
			//支持替换枚举中的异常信息
			if (this.message.equals(msg) && messages.length > 0) {
				this.message = messages[0];
			} else {
				this.message = msg;
			}
		}
	}

	/**原生异常*/
	public BizException(Throwable throwable) {
		this.throwable = throwable;
	}

}