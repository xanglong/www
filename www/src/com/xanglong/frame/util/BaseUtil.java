package com.xanglong.frame.util;

import com.xanglong.frame.config.Const;

public class BaseUtil {

	/**获取应用发布类编译路径*/
	public static String getClassPath() {
		return BaseUtil.class.getClassLoader().getResource("").getPath();
	}

	/**获取应用发布根路径*/
	public static String getRootPath() {
		return getClassPath().replace("/WEB-INF/classes", "");
	}
	
	/**获取存储目录*/
	public static String getSavePath() {
		String resourcePath = BaseUtil.class.getClassLoader().getResource("").getPath();
		String savePath = null;
		if (resourcePath.endsWith("build/classes/")) {
			savePath = resourcePath.replace("build/classes/", "") + Const.WEBCONTENT_FOLDER_NAME + "/WEB-INF/";
		} else {
			savePath = resourcePath.replace("classes/", "");
		}
		return savePath;
	}

}