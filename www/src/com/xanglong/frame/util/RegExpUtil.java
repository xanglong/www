package com.xanglong.frame.util;

import java.util.regex.Pattern;

/**正则类*/
public class RegExpUtil {
	
	/**整数*/		public static final String REGEXP_NUMBER 	= "^[+-]?\\d+$";
	/**密码*/		public static final String REGEXP_PWD 		= "^[0-9a-zA-Z_]{1,}$";
	/**安全密码*/		public static final String REGEXP_SAFE_PWD 	= "^(?![\\d]+$)(?![a-zA-Z]+$)(?![!@#$%^&*><?/`~}{:;,.\\'\"|]+$)[\\da-zA-Z!@#$%^&*><?/`~}{:;,.\\'\"|]{6,18}$";
	/**邮箱*/		public static final String REGEXP_MAIL 		= "^[0-9A-Za-zd]+([-_.][0-9A-Za-zd]+)*@([0-9A-Za-zd]+[-.])+[0-9A-Za-zd]{2,5}$";
	/**网址*/		public static final String REGEXP_URL		= "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
	
	/**密码*/
	public static boolean isPwd(String pwd) {
		if (StringUtil.isBlank(pwd)) return false;
		return Pattern.compile(REGEXP_PWD).matcher(pwd).matches();
	}
	
	/**安全密码*/
	public static boolean isSafePwd(String pwd) {
		if (StringUtil.isBlank(pwd)) return false;
		return Pattern.compile(REGEXP_SAFE_PWD).matcher(pwd).matches();
	}
	
	/**邮箱*/
	public static boolean isMail(String mail) {
		if (StringUtil.isBlank(mail)) return false;
		return Pattern.compile(REGEXP_MAIL).matcher(mail).matches();
	}

	/**网址*/
	public static boolean isUrl(String url) {
		if (StringUtil.isBlank(url)) return false;
		int idx = url.indexOf("?");
		url = idx == -1 ? url : url.substring(0, idx);
		return Pattern.compile(REGEXP_URL).matcher(url).matches();
	}
	
	/**整数*/
	public static boolean isNumber(String number) {
		if (StringUtil.isBlank(number)) return false;
		return Pattern.compile(REGEXP_NUMBER).matcher(number).matches();
	}
	
}