package com.xanglong.frame.util;

public class BaseUtil {

	/**获取应用发布类编译路径*/
	public static String getClassPath() {
		return BaseUtil.class.getClassLoader().getResource("").getPath();
	}

	/**获取应用发布根路径*/
	public static String getRootPath() {
		return getClassPath().replace("/WEB-INF/classes", "");
	}

}