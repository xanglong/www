package com.xanglong.frame.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.dao.DaoManager;
import com.xanglong.frame.dao.DaoMapper;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.exception.ThrowableHandler;
import com.xanglong.frame.mvc.MvcManager;
import com.xanglong.frame.util.BaseUtil;
import com.xanglong.frame.util.EntityUtil;
import com.xanglong.i18n.LanguageManager;
import com.xanglong.i18n.zh_cn.FrameException;

public class ConfigManager {
	
	/**配置缓存*/
	private static Map<String, Object> configMap = new HashMap<>();
	
	/**初始化配置*/
	public void loadConfig() {
		if (!configMap.isEmpty()) {
			return;
		}
		try {
			//[1]配置
			init();
			//[2]语言
			new LanguageManager().init();
			//[3]MVC框架
			new MvcManager().init();
			//[4]Mapper解析
			new DaoMapper().init();
			//[5]数据库连接池
			new DaoManager().init();
		} catch (Throwable throwable) {
			ThrowableHandler.dealException(throwable);
			System.exit(1);
		}
	}

	/**配置入口*/
	public void init() {
		String savePath = BaseUtil.getSavePath();
		File configFile = new File(savePath + Const.CONFIG_FOLDER_NAME + "/");
		if (!configFile.exists()) {
			throw new BizException(FrameException.FRAME_CONFIG_FOLDER_NULL, savePath);
		}
		File[] fileList = configFile.listFiles();
		for (File file : fileList) {
			String name = file.getName();
			switch (name) {
			case Const.CONFIG_PROPERTIES://系统配置
				configMap.put(name, setProperties(Config.class, file));
				break;
			case Const.UEDITOR_PROPERTIES://百度富文本插件配置
				configMap.put(name, setProperties(UeditorConfig.class, file));
				break;
			default:
				throw new BizException(FrameException.FRAME_CONFIG_FILE_INVALID, savePath + name);
			}
		}
	}
	
	/**设置Properties配置*/
	private Object setProperties(Class<?> clazz, File file) {
		Properties properties = new Properties();
		try (FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Const.CHARSET);
		) {
			properties.load(inputStreamReader);
		} catch (IOException e) {
			throw new BizException(e);
		}
		JSONObject JO = new JSONObject();
		for (String key : properties.stringPropertyNames()) {
			setValue(JO, key, properties.getProperty(key));
		}
		return EntityUtil.getBean(JO, clazz);
	}

	/**递归设值*/
	private void setValue(JSONObject JO, String key, String value) {
		String[] keys = key.split("\\.");
		String key0 = keys[0];
		if (key0.endsWith("]")) {
			String k = key0.substring(0, key0.indexOf("["));
			int index = Integer.parseInt(key0.substring(k.length() + 1, key0.length() - 1));
			JSONArray JA = JO.getJSONArray(k);
			if (JA == null) {
				JA = new JSONArray();
				JO.put(k, JA);
			}
			if (index > JA.size() - 1) {
				for (int i = 0, l = index - JA.size(); i <= l; i++) {
					JA.add(keys.length > 1 ? new JSONObject() : null);
				}
			}
			if (keys.length > 1) {
				setValue(JA.getJSONObject(index), key.substring(key0.length() + 1), value);
			} else {
				JA.set(index, value);
			}
		} else {
			if (keys.length > 1) {
				JSONObject nextJO = JO.getJSONObject(key0);
				if (nextJO == null) {
					nextJO = new JSONObject();
					JO.put(key0, nextJO);
				}
				setValue(nextJO, key.substring(key0.length() + 1), value);
			} else {
				JO.put(key0, value);
			}
		}
	}
	
	/**获取配置*/
	private static Object getConfig(String key) {
		//如果没有配置缓存说明就没有初始化过，直接重置配置
		if (!configMap.containsKey(key)) {
			new ConfigManager().loadConfig();
		}
		if (Const.CONFIG_PROPERTIES.equals(key)) {
			return (Config)configMap.get(Const.CONFIG_PROPERTIES);
		} else if (Const.UEDITOR_PROPERTIES.equals(key)) {
			return (UeditorConfig)configMap.get(Const.UEDITOR_PROPERTIES);
		}
		return null;
	}
	
	/**获取配置信息*/
	public static Config getConfig() {
		return (Config)getConfig(Const.CONFIG_PROPERTIES);
	}
	
	/**获取百度富文本配置*/
	public static UeditorConfig getUeditorConfig() {
		return (UeditorConfig)getConfig(Const.UEDITOR_PROPERTIES);
	}

}