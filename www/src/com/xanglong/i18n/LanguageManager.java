package com.xanglong.i18n;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.helper.StringUtil;

import com.xanglong.frame.exception.BizException;
import com.xanglong.i18n.zh_cn.I18nException;


/**语言国际化类*/
public class LanguageManager {
	
	private static Map<String, Map<String, String>> languageMapCache = new HashMap<>();

	/**初始化加载所有语言包到内存*/
	public void init() {
		//这里面抛出异常就终止加载，防止捕获异常后程序运行异常
		String packageName = LanguageManager.class.getPackage().getName();
		ClassLoader classLoader = LanguageManager.class.getClassLoader();
		try {
			for (Language language : Language.values()) {
				Map<String, String> languageMap = new HashMap<>();
				//加载包资源，对包下所有枚举类内容做加载
				String i18nPackageName = packageName + "." + language.name().toLowerCase();
				Enumeration<URL> urls = classLoader.getResources(i18nPackageName.replace(".", "/"));
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					String protocol = url.getProtocol();
					if ("file".equals(protocol)) {
						findClasssByFile(i18nPackageName, url.getFile(), languageMap);
					}
				}
				languageMapCache.put(language.name(), languageMap);
			}
		} catch(Exception e) {
			throw new BizException(e);
		}
	}
	
	/**通过文件获取类*/
	private void findClasssByFile(String packageName, String filePath, Map<String, String> languageMap) throws Exception {
		File folder = new File(filePath);
		if (!folder.exists() || !folder.isDirectory()) {
			return;
		}
		for (File file : folder.listFiles()) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				findClasssByFile(packageName + "." + fileName, file.getAbsolutePath(), languageMap);
			} else if (fileName.endsWith(".class")) {
				String className = fileName.substring(0, fileName.length() - 6);
				Class<?> clazz = Class.forName(packageName + '.' + className);
				Object[] objects = clazz.getEnumConstants();
				//方法名写死，如果接口被改了，这里执行肯定也报错，知错就改就行
		        Method getCode = clazz.getMethod("getCode");
		        Method getName = clazz.getMethod("getName");
		        for (Object object : objects){
		        	if(object instanceof I18n){
		        		languageMap.put((String)getCode.invoke(object), (String)getName.invoke(object));
					} else {
						throw new BizException(I18nException.I18N_UNREALIZED_I18N_INTERFACE);
					}
		        }
			}
		}
	}
	
	/**根据设置的显示语言获取国际化翻译文本信息*/
	public static String getMessage(Language language, String code) {
		if (language == null || StringUtil.isBlank(code)) {
			return null;
		}
		Map<String, String> languageMap = languageMapCache.get(language.name());
		if (languageMap == null) {
			throw new BizException(I18nException.I18N_CAN_NOT_FIND_LANGUAGE_RESOURCE);
		}
		String message = languageMap.get(code);
		if (StringUtil.isBlank(message)) {
			throw new BizException(I18nException.I18N_CAN_NOT_FIND_LANGUAGE_MESSAGE);
		}
		return message;
	}

}