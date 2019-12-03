package com.xanglong.frame.mvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**MVC管理类*/
public class MvcManager {
	
	/**类名、类型缓存索引*/
	private static Map<String, BeanType> indexs = new HashMap<>();

	/**是否存在*/
	public boolean isExist(String key) {
		return indexs.containsKey(key);
	}

	/**设置索引*/
	public void setIndex(String key, BeanType beanType) {
		indexs.put(key, beanType);
	}
	
	/**初始化*/
	public void init() throws IOException, ClassNotFoundException {
		//DAO层
		new RepositoryBean().init();
	}

}