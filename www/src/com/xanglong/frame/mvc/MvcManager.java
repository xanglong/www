package com.xanglong.frame.mvc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.xanglong.frame.exception.BizException;
import com.xanglong.i18n.zh_cn.FrameException;

/**MVC管理类*/
public class MvcManager {
	
	/**类名、类型缓存索引*/
	private static Map<String, BeanType> beanIndexs = new HashMap<>();
	
	/**缓存除控制层、业务层、数据层、组件层之外的所有Bean*/
	private static Map<String, Object> otherBeans = new HashMap<>();
	

	/**
	 * 类的实例缓存是否已存在
	 * @param key 类的名称
	 * @return 是否已存在
	 * */
	public boolean isExist(String key) {
		return beanIndexs.containsKey(key);
	}

	/**
	 * 设置实例索引
	 * @param key 类的名称
	 * @param beanType 实例类型
	 * */
	public void setIndex(String key, BeanType beanType) {
		beanIndexs.put(key, beanType);
	}

	/**初始化*/
	public void init() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		//数据层
		new RepositoryBean().init();
		//业务层
		new ServiceBean().init();
		//组件层
		new ComponentBean().init();
		//控制层
		new ControllerBean().init();
		
	}

	/**
	 * 给类设置自动注入的Bean对象到缓存
	 * @param bean 实例对象
	 * */
	public void setAutowiredBean(Object bean) {
		ServiceBean serviceBean = new ServiceBean();
		RepositoryBean repositoryBean = new RepositoryBean();
		ComponentBean componentBean = new ComponentBean();
		Field[] fields = bean.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				MyAutowired myAutowired = field.getDeclaredAnnotation(MyAutowired.class);
				if (myAutowired == null) {
					continue;
				}
				boolean isAccessible = field.isAccessible();
				field.setAccessible(true);
				Class<?> fieldClass = field.getType();
				String key = fieldClass.getName();
				BeanType beanType = beanIndexs.get(key);
				if (beanType == null) {
					Class<?> clazz = Class.forName(key);
					MyService myService = clazz.getDeclaredAnnotation(MyService.class);
					if (myService != null) {
						new ServiceBean().handlerServiceByClass(clazz);
						field.set(bean, serviceBean.getService(key));
						continue;
					}
					MyComponent myComponent = clazz.getDeclaredAnnotation(MyComponent.class);
					if (myComponent != null) {
						new ComponentBean().handlerComponentByClass(clazz);
						field.set(bean, componentBean.getComponent(key));
						continue;
					}
					Object other = fieldClass.newInstance();
					otherBeans.put(key, other);
					beanIndexs.put(key, BeanType.OTHER);
					field.set(bean, other);
				} else {
					if (BeanType.CONTROLLER == beanType) {
						throw new BizException(FrameException.FRAME_CLASS_ANNOTATION_MYCONTROLLER_INVALID);
					} else if (BeanType.SERVICE == beanType) {
						field.set(bean, serviceBean.getService(key));
					} else if (BeanType.REPOSITORY == beanType) {
						field.set(bean, repositoryBean.getRepository(key));
					} else if (BeanType.COMPONENT == beanType) {
						field.set(bean, componentBean.getComponent(key));
					} else if (BeanType.OTHER == beanType) {
						field.set(bean, otherBeans.get(key));
					}
				}
				field.setAccessible(isAccessible);
			}
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			throw new BizException(e);
		}
	}

	public static Map<String, Object> getOtherBeans() {
		return otherBeans;
	}

	public static void setOtherBeans(Map<String, Object> otherBeans) {
		MvcManager.otherBeans = otherBeans;
	}

}