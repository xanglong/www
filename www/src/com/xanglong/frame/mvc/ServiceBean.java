package com.xanglong.frame.mvc;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.proxy.ProxyManager;

/**业务层Bean*/
public class ServiceBean {

	private static Map<String, Object> services = new HashMap<String, Object>();

	/**初始化*/
	public void init() throws IOException, ClassNotFoundException {
		String[] servicePackages = Sys.getConfig().getPackages().getService();
		for (String servicePackage : servicePackages) {
			Enumeration<URL> urls = ServiceBean.class.getClassLoader().getResources(servicePackage.replace('.', '/'));
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				String protocol = url.getProtocol();
				String filePath = URLDecoder.decode(url.getFile(), Const.CHARSET_STR);
				if ("file".equals(protocol)) {
					//根据类来解析
					findClasssByFile(servicePackage, filePath);
				} else if ("jar".equals(protocol)) {
					//根据jar包来解析
					findClasssByJar(url, servicePackage, filePath);
				}
			}
		}
	}
	
	/**
	 * 缓存有@MyService注解的类
	 * @param clazz 类
	 * */
	private void handlerServiceByClass(Class<?> clazz) {
		MyService myService = clazz.getDeclaredAnnotation(MyService.class);
		if (myService == null) {
			return;
		}
		MvcManager mvcManager = new MvcManager();
		//由于会被交叉引用，所以如果存在就跳过设置
		if (mvcManager.isExist(clazz.getName())) {
			return;
		}
		Object object = new ProxyManager().getServiceInstance(clazz);
		//设置索引
		mvcManager.setIndex(clazz.getName(), BeanType.SERVICE);
		services.put(clazz.getName(), object);
		//设置自动注入类
		mvcManager.setAutowiredBean(object);
	}
	
	/**
	 * 获取Bean对象
	 * @param key 类的名称
	 * @return 类的实例对象
	 * */
	public Object getService(String key) throws ClassNotFoundException {
		Object obj = services.get(key);
		//处理自动注入时解析成员变量的Bean对象的问题
		if (obj == null) {
			handlerServiceByClass(Class.forName(key));
			obj = services.get(key);
		}
		return obj;
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
				handlerServiceByClass(Class.forName(packageName + '.' + className));
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
				handlerServiceByClass(Class.forName(packageName + '.' + className));
			}
		}
	}

}