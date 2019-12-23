package com.xanglong.frame.mvc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.xanglong.frame.Current;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.config.Transaction;
import com.xanglong.frame.dao.Dao;
import com.xanglong.frame.dao.DaoMapper;
import com.xanglong.frame.exception.BizException;
import com.xanglong.i18n.zh_cn.FrameException;

/**MVC管理类*/
public class MvcManager {
	
	/**类名、类型缓存索引*/
	private static Map<String, BeanType> beanIndexs = new HashMap<>();
	
	/**缓存除控制层、业务层、数据层、组件层之外的所有Bean*/
	private static Map<String, Object> otherBeans = new HashMap<>();
	
	/**缓存所有接口类和实现类的映射*/
	private static Map<String, Class<?>> interfaceMap = new HashMap<>();
	
	/**
	 * 类的实例缓存是否已存在
	 * @param key 类的名称
	 * @return 是否已存在
	 * */
	protected boolean isExist(String key) {
		return beanIndexs.containsKey(key);
	}

	/**
	 * 设置实例索引
	 * @param key 类的名称
	 * @param beanType 实例类型
	 * */
	protected void setIndex(String key, BeanType beanType) {
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
		//Mapper解析
		new DaoMapper().init();
	}
	
	/**
	 * 给类设置自动注入的Bean对象到缓存
	 * @param bean 实例对象
	 * */
	protected void setAutowiredBean(Object bean) {
		Field[] fields = bean.getClass().getDeclaredFields();
		Field field = null;
		//虽然报错整个项目都启动失败，但是还是要按照规范把访问权限还原
		boolean isAccessible = false;
		try {
			for (int i = 0; i < fields.length; i++) {
				field = fields[i];
				MyAutowired myAutowired = field.getDeclaredAnnotation(MyAutowired.class);
				if (myAutowired == null) {
					continue;
				}
				isAccessible = field.isAccessible();
				field.setAccessible(true);
				Class<?> fieldClass = field.getType();
				String key = fieldClass.getName();
				BeanType beanType = beanIndexs.get(key);
				//如果没获取到类型，说明还没被扫描过
				if (beanType == null) {
					Class<?> clazz = Class.forName(key);
					Class<?> implementClass = null;
					if (fieldClass.isInterface()) {
						//如果自动注入的是接口类，那么就从缓存中提取实现类
						implementClass = getImplementClass(key);
						//如果找不到实现类，说明没有被支持扫描接口和实现类的映射关系
						if (implementClass == null) {
							field.setAccessible(isAccessible);
							throw new BizException(FrameException.FRAME_PLEASE_USE_IMPLEMENT_CLASS, key);
						}
					} else {
						implementClass = clazz;
					}
					MyService myService = implementClass.getDeclaredAnnotation(MyService.class);
					if (myService != null) {
						new ServiceBean().handlerServiceByClass(clazz);
						field.set(bean, ServiceBean.getService(key));
						field.setAccessible(isAccessible);
						continue;
					}
					MyComponent myComponent = implementClass.getDeclaredAnnotation(MyComponent.class);
					if (myComponent != null) {
						new ComponentBean().handlerComponentByClass(clazz);
						field.set(bean, ComponentBean.getComponent(key));
						field.setAccessible(isAccessible);
						continue;
					}
					//上面的业务层、组件层都被排除掉了，剩下的只能是普通实现类了
					Object other = fieldClass.newInstance();
					otherBeans.put(key, other);
					beanIndexs.put(key, BeanType.OTHER);
					field.set(bean, other);
				} else {
					if (BeanType.CONTROLLER == beanType) {
						field.setAccessible(isAccessible);
						throw new BizException(FrameException.FRAME_CLASS_ANNOTATION_MYCONTROLLER_INVALID);
					} else if (BeanType.SERVICE == beanType) {
						field.set(bean, ServiceBean.getService(key));
					} else if (BeanType.REPOSITORY == beanType) {
						field.set(bean, RepositoryBean.getRepository(key));
					} else if (BeanType.COMPONENT == beanType) {
						field.set(bean, ComponentBean.getComponent(key));
					} else if (BeanType.OTHER == beanType) {
						field.set(bean, otherBeans.get(key));
					}
				}
				field.setAccessible(isAccessible);
			}
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			field.setAccessible(isAccessible);
			throw new BizException(e);
		}
	}

	/**
	 * 进入切面
	 * @param beanType 代码层类型
	 * */
	protected static void aopEnter(BeanType beanType) {
		//进入切面目前其实就控制层和业务层，暂时不需要处理逻辑
		Current.aopEnter(beanType);
	}
	
	/**离开切面*/
	protected static void aopExit() {
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
	
	/**
	 * 注意：只有需要生成动态代理的层且有交叉引入的层才需要前置扫描
	 * 根据路径先扫描一遍所有包，提取实现的接口列表和实现类的map集合
	 * @param packagePaths 配置的包扫描路径
	 * */
	protected void findAllInterfaceClassMap(String[] packagePaths) {
		try {
			for (String packagePath : packagePaths) {
				Enumeration<URL> urls = MvcManager.class.getClassLoader().getResources(packagePath.replace('.', '/'));
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					String protocol = url.getProtocol();
					String filePath = URLDecoder.decode(url.getFile(), Const.CHARSET_STR);
					if ("file".equals(protocol)) {
						//根据类来解析
						findClasssByFile(packagePath, filePath);
					} else if ("jar".equals(protocol)) {
						//根据jar包来解析
						findClasssByJar(url, packagePath, filePath);
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 通过文件获取类
	 * @param packageName 包的扫描路径
	 * @param filePath 文件路径
	 * */
	private void findClasssByFile(String packageName, String filePath) throws ClassNotFoundException {
		File folder = new File(filePath);
		if (!folder.exists() || !folder.isDirectory()) {
			return;
		}
		for (File file : folder.listFiles()) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				findClasssByFile(packageName + "." + fileName, file.getAbsolutePath());
			} else if (fileName.endsWith(".class")) {
				String className = fileName.substring(0, fileName.length() - 6);
				handlerImplementsByClass(Class.forName(packageName + '.' + className));
			}
		}
	}
	
	/**
	 * 通过JAR包获取类
	 * @param url 资源
	 * @param packageName 包扫描路径
	 * @param filePath 文件地址
	 * */
	private void findClasssByJar(URL url, String packageName, String filePath) throws IOException, ClassNotFoundException {
		JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			if (name.charAt(0) == '/') {
				name = name.substring(1);
			}
			if (!name.startsWith(packageName)) {
				continue;
			}
			int idx = name.lastIndexOf('/');
			if (idx == -1) {
				continue;
			}
			String pathName = name.substring(0, idx).replace('/', '.');
			if (name.endsWith(".class") && !entry.isDirectory()) {
				String className = name.substring(pathName.length() + 1, name.length() - 6);
				handlerImplementsByClass(Class.forName(packageName + '.' + className));
			}
		}
	}
	
	/**
	 * 提取接口类名和接口实现类名的映射关系
	 * @param clazz 类
	 * */
	private void handlerImplementsByClass(Class<?> clazz) {
		if (clazz.isInterface()) {
			return;
		}
		String clazzName = clazz.getName();
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> inter : interfaces) {
			String interfaceName = inter.getName();
			if (interfaceMap.containsKey(inter.getName())) {
				throw new BizException(FrameException.FRAME_INTERFACE_HAS_IMPLEMENT_CLASS, interfaceName, clazzName);
			}
			interfaceMap.put(inter.getName(), clazz);
		}
	}
	
	/**
	 * 根据接口类获取其实现类
	 * @param interfaceName 接口类类名
	 * @return 实现类
	 * */
	protected static Class<?> getImplementClass(String interfaceName) {
		return interfaceMap.get(interfaceName);
	}
	
	/**
	 * 公开获取获取Bean对象
	 * @param clazz 类
	 * @return bean对象
	 * */
	public static Object getImplementBean(Class<?> clazz) {
		String key = clazz.getName();
		if (clazz.isInterface()) {
			clazz = getImplementClass(key);
			key = clazz.getName();
		}
		BeanType beanType = beanIndexs.get(key);
		if (BeanType.SERVICE == beanType) {
			return ServiceBean.getService(key);
		} else if (BeanType.REPOSITORY == beanType) {
			return RepositoryBean.getRepository(key);
		} else if (BeanType.COMPONENT == beanType) {
			return ComponentBean.getComponent(key);
		} else if (BeanType.OTHER == beanType) {
			return otherBeans.get(key);
		}
		return null;
	}

}