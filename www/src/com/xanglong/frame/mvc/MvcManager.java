package com.xanglong.frame.mvc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.xanglong.frame.Current;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Transaction;
import com.xanglong.frame.dao.Dao;
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
					//反查接口实现类太消耗性能了，为了启动时间更快，必须注入实现类
					if (fieldClass.isInterface()) {
						throw new BizException(FrameException.FRAME_MYAUTOWIRED_CANNOT_BE_INTERFACE);
					}
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
	
	/**
	 * 进入切面
	 * @param beanType 代码层类型
	 * */
	public static void aopEnter(BeanType beanType) {
		//进入切面目前其实就控制层和业务层，暂时不需要处理逻辑
		Current.aopEnter(beanType);
	}
	
	/**离开切面*/
	public static void aopExit() {
		BeanType prevBeanType = Current.aopExit();
		BeanType nextBeanType = Current.getAop();
		if (nextBeanType == null) {
			//如果下一层没了，那么提交事务
			Dao.sysCommit();
			return;
		}
		//前面如果退出到最后一层了，事务会提交且释放连接
		//下面的提交事务不释放连接，确保有问题的连接不被快速扩散，好断点跟踪问题
		Transaction transaction = Sys.getConfig().getTransaction();
		if (transaction.getOnController()) {
			//如果当前退出的是控制层，则提交事务
			if (BeanType.CONTROLLER == prevBeanType) {
				Dao.commit();
			}
		} else if (transaction.getOnService()) {
			//只要下一层不是业务层就提交事务
			if (BeanType.SERVICE != nextBeanType) {
				Dao.commit();
			}
		} else if (transaction.getOnServiceMethod()) {
			//如果当前层是业务层就提交事务
			if (BeanType.SERVICE == prevBeanType) {
				Dao.commit();
			}
		}
	}

}