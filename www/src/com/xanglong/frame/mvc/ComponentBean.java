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
import com.xanglong.frame.exception.BizException;

/**组件层Bean*/
public class ComponentBean {

	private static Map<String, Object> components = new HashMap<String, Object>();
	
	/**
	 * 是否包含类缓存
	 * @param key 类名
	 * @return 是否包含
	 * */
	public static boolean containsKey(String key) {
		return components.containsKey(key);
	}

	/**初始化*/
	public void init() {
		String[] componentPackages = Sys.getConfig().getPackages().getComponent();
		try {
			for (String componentPackage : componentPackages) {
				Enumeration<URL> urls = ComponentBean.class.getClassLoader().getResources(componentPackage.replace('.', '/'));
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					String protocol = url.getProtocol();
					String filePath;
						filePath = URLDecoder.decode(url.getFile(), Const.CHARSET_STR);
					if ("file".equals(protocol)) {
						findClasssByFile(componentPackage, filePath);
					} else if ("jar".equals(protocol)) {
						findClasssByJar(url, componentPackage, filePath);
					}
				}
			}
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 缓存有@MyComponent注解的类
	 * @param clazz 类
	 * */
	protected void handlerComponentByClass(Class<?> clazz) {
		MyComponent myComponent = clazz.getDeclaredAnnotation(MyComponent.class);
		if (myComponent == null) {
			return;
		}
		MvcManager mvcManager = new MvcManager();
		if (mvcManager.isExist(clazz.getName())) {
			return;
		}
		Object object = null;
		try {
			object = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new BizException(e);
		}
		//设置索引
		mvcManager.setIndex(clazz.getName(), BeanType.COMPONENT);
		components.put(clazz.getName(), object);
		//设置自动注入类
		mvcManager.setAutowiredBean(object);
	}

	/**获取Bean对象
	 * @param key 类的名称
	 * @return 类的实例
	 * */
	protected static Object getComponent(String key) {
		Object obj = components.get(key);
		if (obj == null) {
			try {
				new ComponentBean().handlerComponentByClass(Class.forName(key));
			} catch (ClassNotFoundException e) {
				throw new BizException(e);
			}
			obj = components.get(key);
		}
		return obj;
	}
	
	/**
	 * 通过文件获取类
	 * @param packageName 扫描包名
	 * @param filePath 文件路径
	 * */
	private void findClasssByFile(String packageName, String filePath) {
		File folder = new File(filePath);
		if (!folder.exists() || !folder.isDirectory()) {
			return;
		}
		try {
			for (File file : folder.listFiles()) {
				String fileName = file.getName();
				if (file.isDirectory()) {
					findClasssByFile(packageName + "." + fileName, file.getAbsolutePath());
				} else if (fileName.endsWith(".class")) {
					String className = fileName.substring(0, fileName.length() - 6);
						handlerComponentByClass(Class.forName(packageName + '.' + className));
				}
			}
		} catch (ClassNotFoundException e) {
			throw new BizException(e);
		}
	}

	/**通过JAR包获取类*/
	private void findClasssByJar(URL url, String packageName, String filePath) {
		JarFile jarFile = null;
		try {
			jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
		} catch (IOException e) {
			throw new BizException(e);
		}
		Enumeration<JarEntry> entries = jarFile.entries();
		try {
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
					handlerComponentByClass(Class.forName(packageName + '.' + className));
				}
			}
		} catch (ClassNotFoundException e) {
			throw new BizException(e);
		}
	}

}