package com.xanglong.frame.mvc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.xanglong.frame.Sys;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.FrameException;

/**控制层Bean*/
public class ControllerBean {
	
	private static ConcurrentHashMap<String, Object> controllers = new ConcurrentHashMap<String, Object>();
	private static Map<String, Method> methods = new HashMap<String, Method>();

	/**初始化*/
	public void init() {
		String[] controllerPackages = Sys.getConfig().getPackages().getController();
		try {
			for (String controllerPackage : controllerPackages) {
				Enumeration<URL> urls = ControllerBean.class.getClassLoader().getResources(controllerPackage.replace('.', '/'));
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					String protocol = url.getProtocol();
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					if ("file".equals(protocol)) {
						findClasssByFile(controllerPackage, filePath);
					} else if ("jar".equals(protocol)) {
						findClasssByJar(url, controllerPackage, filePath);
					}
				}
			}
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 获取所有URI
	 * @return 所有注解的URI地址
	 * */
	public List<String> getAllUris() {
		List<String> keys = new ArrayList<>(controllers.size());
		for (Entry<String, Object> entry : controllers.entrySet()) {
			keys.add(entry.getKey());
		}
		return keys;
	}

	/**
	 * 根据URL获取类对象
	 * @param requestURI 请求地址
	 * @return 控制类实例对象
	 * */
	protected static Object getController(String requestURI) {
		return controllers.get(requestURI);
	}

	/**
	 * 根据URL获取方法
	 * @param requestURI 请求地址
	 * @return 定义在控制类下的方法实体
	 * */
	protected static Method getMethod(String requestURI) {
		return methods.get(requestURI);
	}
	
	/**
	 * 缓存有@MyController注解的类
	 * @param clazz 类
	 * */
	private void handlerControllerByClass(Class<?> clazz) {
		MyController myController = clazz.getDeclaredAnnotation(MyController.class);
		if (myController == null) {
			throw new BizException(FrameException.FRAME_CLASS_MISS_ANNOTATION_MYCONTROLLER, clazz.getName());
		}
		MyRequestMapping myRequestMappingClass = clazz.getDeclaredAnnotation(MyRequestMapping.class);
		if (myRequestMappingClass == null || StringUtil.isBlank(myRequestMappingClass.value())) {
			throw new BizException(FrameException.FRAME_CLASS_MISS_ANNOTATION_MYREQUESTMAPPING, clazz.getName());
		}
		String baseUrl = myRequestMappingClass.value().trim();
		Object object = null;
		try {
			object = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new BizException(e);
		}
		MvcManager mvcManager = new MvcManager();
		//设置索引
		mvcManager.setIndex(clazz.getName(), BeanType.CONTROLLER);
		//设置自动注入类
		mvcManager.setAutowiredBean(object);
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			MyRequestMapping myRequestMappingMethod = method.getDeclaredAnnotation(MyRequestMapping.class);
			String methodName = clazz.getName() + "." + method.getName();
			if (myRequestMappingMethod == null) {
				throw new BizException(FrameException.FRAME_METHOD_MISS_ANNOTATION_MYREQUESTMAPPING, methodName);
			}
			String methodUrl = baseUrl + myRequestMappingMethod.value().trim();
			if (controllers.contains(methodUrl)) {
				throw new BizException(FrameException.FRAME_METHOD_ANNOTATION_MYREQUESTMAPPING_REPEAT, methodName);
			}
			controllers.put(methodUrl, object);
			methods.put(methodUrl, method);
		}
	}

	/**
	 * 通过文件获取类
	 * @param packageName 包扫描路径
	 * @param filePath 文件地址
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
						handlerControllerByClass(Class.forName(packageName + '.' + className));
				}
			}
		} catch (ClassNotFoundException e) {
			throw new BizException(e);
		}
	}

	/**
	 * 通过JAR包获取类 
	 * @param url 资源
	 * @param packageName 包扫描路径
	 * @param filePath 文件地址
	 * */
	private void findClasssByJar(URL url, String packageName, String filePath) {
		JarFile jarFile;
		try {
			jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
		} catch (IOException e) {
			throw new BizException(e);
		}
		try {
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
					handlerControllerByClass(Class.forName(packageName + '.' + className));
				}
			}
		} catch (ClassNotFoundException e) {
			throw new BizException(e);
		}
	}

}