package com.xanglong.frame.mvc;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.proxy.MyProxy;
import com.xanglong.i18n.zh_cn.FrameException;

/**DAO层Bean*/
public class RepositoryBean {

	private static Map<String, Object> daos = new HashMap<String, Object>();
	private static List<Class<?>> cacheDaos = new ArrayList<>();
	
	/**
	 * 获取缓存DAO
	 * @return 所有缓存类型DAO
	 * */
	public List<Class<?>> getCacheDaos() {
		return cacheDaos;
	}

	/**初始化*/
	public void init() throws IOException, ClassNotFoundException {
		String[] daoPackages = Sys.getConfig().getPackages().getDao();
		for (String daoPackage : daoPackages) {
			Enumeration<URL> urls = RepositoryBean.class.getClassLoader().getResources(daoPackage.replace('.', '/'));
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				String protocol = url.getProtocol();
				String filePath = URLDecoder.decode(url.getFile(), Const.CHARSET_STR);
				if ("file".equals(protocol)) {
					//根据类来解析
					findClasssByFile(daoPackage, filePath);
				} else if ("jar".equals(protocol)) {
					//根据jar包来解析
					findClasssByJar(url, daoPackage);
				}
			}
		}
	}

	/**
	 * 缓存有@MyRepository注解的类
	 * @param clazz 类
	 * */
	private void handlerRepositoryByClass(Class<?> clazz) {
		MyRepository myRepository = clazz.getDeclaredAnnotation(MyRepository.class);
		if (myRepository == null) {
			throw new BizException(FrameException.FRAME_CLASS_MISS_ANNOTATION_MYREPOSITORY);
		}
		//注解标记为缓存的类
		MyCacheDao myCacheDao = clazz.getDeclaredAnnotation(MyCacheDao.class);
		if (myCacheDao != null) {
			cacheDaos.add(clazz);
		}
		Object object = MyProxy.getDaoInstance(clazz);
		MvcManager beanIndex = new MvcManager();
		//设置bean的索引，类名对应类型
		beanIndex.setIndex(clazz.getName(), BeanType.REPOSITORY);
		//缓存bean对象
		daos.put(clazz.getName(), object);
	}

	/**
	 * 获取Bean对象
	 * @param key 类名字符串
	 * @return bean实例
	 * */
	public Object getRepository(String key) {
		return daos.get(key);
	}

	/**
	 * 通过文件获取类
	 * @param packageName 扫描包名
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
				//递归查找文件夹下的类文件
				findClasssByFile(packageName + "." + fileName, file.getAbsolutePath());
			} else if (fileName.endsWith(".class")) {
				String className = fileName.substring(0, fileName.length() - 6);
				handlerRepositoryByClass(Class.forName(packageName + '.' + className));
			}
		}
	}

	/**
	 * 通过JAR包获取类
	 * @param url 资源对象
	 * @param packageName 扫描包名
	 * */
	private void findClasssByJar(URL url, String packageName) throws ClassNotFoundException, IOException {
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
				handlerRepositoryByClass(Class.forName(packageName + '.' + className));
			}
		}
	}

}