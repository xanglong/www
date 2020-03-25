package com.xanglong.frame.net;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.xanglong.frame.Current;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Config;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.config.Template;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.io.FileUtil;
import com.xanglong.frame.util.BaseUtil;
import com.xanglong.frame.util.DateUtil;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.FrameException;
import com.xanglong.i18n.zh_cn.SystemException;

public class Source {

	/**
	 * 获取请求资源类型：公开外部方法
	 * @param uri 请求地址
	 * @return 请求资源类型 
	 * */
	public static SourceInfo getSourceInfo(String uri) {
		//如果请求没指定类型和文件名称，则按照欢饮页处理
		if (uri.charAt(uri.length() - 1) == '/') {
			uri += Sys.getConfig().getWelcomeFile();
		} else {
			String type = uri.substring(uri.lastIndexOf("/"));
			int idx = type.lastIndexOf(".");
			if (idx == -1) {
				uri += "/" + Sys.getConfig().getWelcomeFile();
			}
		}
		SourceInfo sourceInfo = getSourceInfoInner(uri);
		//缓存住资源信息，方便一些信息的处理
		Current.setSourceInfo(sourceInfo);
		return sourceInfo;
	}

	/**
	 * 获取请求资源类型：私有内部方法
	 * @param uri 请求地址
	 * @return 请求资源类型 
	 * */
	private static SourceInfo getSourceInfoInner(String uri) {
		SourceInfo sourceInfo = new SourceInfo();
		sourceInfo.setRequestURI(uri);
		String type = uri.substring(uri.lastIndexOf("/"));
		type = type.substring(type.lastIndexOf(".") + 1);
		Config config = Sys.getConfig();
		String[] actionTypes = config.getActionTypes();
		for (String actionType : actionTypes) {
			if (actionType.equals(type)) {
				//如果是动作请求，直接返回即可
				sourceInfo.setSourceType(SourceType.ACTION);
				return sourceInfo;
			}
		}
		//整个处理顺序按照请求的频率从高到低排列
		if (config.getDocumentType().equals(type)) {
			sourceInfo.setSourceType(SourceType.HTML);
			return sourceInfo;
		} else if (SourceType.CSS.getCode().equals(type)) {
			sourceInfo.setSourceType(SourceType.CSS);
			return sourceInfo;
		} else if (SourceType.JS.getCode().equals(type)) {
			sourceInfo.setSourceType(SourceType.JS);
			return sourceInfo;
		} else {
			//图片
			for (ImageType imageType : ImageType.values()) {
				if (imageType.getCode().equals(type)) {
					sourceInfo.setSourceType(SourceType.IMAGE);
					sourceInfo.setImageType(imageType);
					return sourceInfo;
				}
			}
			//文本
			for (TextType textType : TextType.values()) {
				if (textType.getCode().equals(type)) {
					sourceInfo.setSourceType(SourceType.TXT);
					sourceInfo.setTextType(textType);
					return sourceInfo;
				}
			}
			//字体
			for (FontType fontType : FontType.values()) {
				if (fontType.getCode().equals(type)) {
					sourceInfo.setSourceType(SourceType.FONT);
					sourceInfo.setFontType(fontType);
					return sourceInfo;
				}
			}
			//音频
			for (AudioType audioType : AudioType.values()) {
				if (audioType.getCode().equals(type)) {
					sourceInfo.setSourceType(SourceType.AUDIO);
					sourceInfo.setAudioType(audioType);
					return sourceInfo;
				}
			}
			//视频
			for (VideoType videoType : VideoType.values()) {
				if (videoType.getCode().equals(type)) {
					sourceInfo.setSourceType(SourceType.VIDEO);
					sourceInfo.setVideoType(videoType);
					return sourceInfo;
				}
			}
		}
		throw new BizException(true, FrameException.FRAME_UNSUPPORTED_SOURCE_TYPE, type, uri);
	}

	/**
	 * 执行资源请求响应
	 * @param request 请求对象
	 * @param response 响应对象
	 * @param sourceInfo 资源信息
	 * */
	public static void execute(HttpServletRequest request, HttpServletResponse response, SourceInfo sourceInfo) {
		String rootPath = BaseUtil.getRootPath();
		String uri = sourceInfo.getRequestURI();
		String filePath = rootPath + uri;
		Config config = Sys.getConfig();
		//如果请求地址和下载地址前缀匹配，则说明是走资源下载
		if (uri.startsWith(config.getDownloadUriPrefix())) {
			filePath = config.getDataPath() + uri;
		}
		File file = new File(filePath);
		if (file.exists()) {
			boolean isDebug = config.getIsDebug();
			Template template = config.getTemplate();
			if (SourceType.HTML == sourceInfo.getSourceType()) {
				//处理网页压缩和网页模板，得到最终网页
				String html = dealHtmlCompressAndTemplate(file, isDebug, template.getUseHtml());
				HttpUtil.responseText(response, ContentType.HTML, html);
			} else {
				if (!isDebug) {
					//浏览器缓存处理
					dealBrowseCache(request, response, file);
				}
				if (SourceType.CSS == sourceInfo.getSourceType()) {
					//处理样式压缩和样式模板，得到最终样式
					String css = dealCssCompressAndTemplate(file, isDebug, template.getUseCss());
					HttpUtil.responseText(response, ContentType.CSS, css);
				} else if (SourceType.JS == sourceInfo.getSourceType()) {
					//处理脚本压缩和脚本模板，得到最终脚本
					String js = dealJsCompressAndTemplate(file, isDebug, template.getUseJs());
					HttpUtil.responseText(response, ContentType.JS, js);
				} else if (SourceType.IMAGE == sourceInfo.getSourceType()) {
					byte[] bytes = FileUtil.readByte(file);
					//不设置文件名称在浏览器用新窗口打开就不会变成图片下载了
					HttpUtil.responseImage(response, bytes, sourceInfo.getImageType(), null);
				} else if (SourceType.TXT == sourceInfo.getSourceType()) {
					String text = FileUtil.read(file);
					//搜索引擎协议、第三方验证文件等
					HttpUtil.responseText(response, ContentType.FORM_TEXT, text);
				} else if (SourceType.FONT == sourceInfo.getSourceType()) {
					HttpUtil.responseFile(response, FileUtil.readByte(file), file.getName());
				}
			}
		} else {
			if (SourceType.HTML == sourceInfo.getSourceType()) {
				throw new BizException(true, SystemException.E404, filePath);
			} else {
				response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
				throw new BizException(true, FrameException.FRAME_FILE_NULL, uri);
			}
		}
	}

	/**
	 * 处理脚本压缩和脚本模板：开放给后端渲染服务用
	 * @param file 脚本文件
	 * @return 最终脚本
	 * */
	public static String dealJsCompressAndTemplate(File file) {
		Config config = Sys.getConfig();
		Template template = config.getTemplate();
		return dealJsCompressAndTemplate(file, config.getIsDebug(), template.getUseJs());
	}

	/**
	 * 处理脚本压缩和脚本模板
	 * @param file 脚本文件
	 * @param isDebug 是否调试模式
	 * @param useJs 是否启用脚本模板
	 * @return 最终脚本
	 * */
	private static String dealJsCompressAndTemplate(File file, boolean isDebug, boolean useJs) {
		String css = FileUtil.read(file);
		if (isDebug && useJs) {
			//调试模式下实时替换模板，支持持续开发编程修改原文件
			css = setJsTemplate(css);
		}
		//运行模式，文件没已压缩标记，则压缩文件并打标记
		if (!isDebug && !css.startsWith(Const.STATIC_CSS_PREFIX)) {
			//先做模板替换，后做文件压缩
			if (useJs) {
				css = setJsTemplate(css);
			}
			css = Const.STATIC_CSS_PREFIX + trims(css);
			FileUtil.writeCover(file, css);
		}
		return css;
	}

	/**
	 * 处理样式压缩和样式模板：开放给后端渲染服务用
	 * @param file 样式文件
	 * @return 最终样式
	 * */
	public static String dealCssCompressAndTemplate(File file) {
		Config config = Sys.getConfig();
		Template template = config.getTemplate();
		return dealCssCompressAndTemplate(file, config.getIsDebug(), template.getUseCss());
	}

	/**
	 * 处理样式压缩和样式模板
	 * @param file 样式文件
	 * @param isDebug 是否调试模式
	 * @param useCss 是否启用样式模板
	 * */
	private static String dealCssCompressAndTemplate(File file, boolean isDebug, boolean useCss) {
		String css = FileUtil.read(file);
		if (isDebug && useCss) {
			//调试模式下实时替换模板，支持持续开发编程修改原文件
			css = setCssTemplate(css);
		}
		//运行模式，文件没已压缩标记，则压缩文件并打标记
		if (!isDebug && !css.startsWith(Const.STATIC_CSS_PREFIX)) {
			//先做模板替换，后做文件压缩
			if (useCss) {
				css = setCssTemplate(css);
			}
			css = Const.STATIC_CSS_PREFIX + trims(css);
			FileUtil.writeCover(file, css);
		}
		return css;
	}

	/**
	 * 处理网页压缩和网页模板：开放给后端渲染服务用
	 * @param file 网页文件
	 * @return 最终网页
	 * */
	public static String dealHtmlCompressAndTemplate(File file) {
		Config config = Sys.getConfig();
		Template template = config.getTemplate();
		return dealHtmlCompressAndTemplate(file, config.getIsDebug(), template.getUseHtml());
	}

	/**
	 * 处理网页压缩和网页模板
	 * @param file 网页文件
	 * @param isDebug 是否调试模式
	 * @param useHtml 是否启用网页模板
	 * */
	private static String dealHtmlCompressAndTemplate(File file, boolean isDebug, boolean useHtml) {
		//读取文件
		String html = FileUtil.read(file);
		if (isDebug && useHtml) {
			//调试模式下实时替换模板，支持持续开发编程修改原文件
			html = setHtmlTemplate(html);
		}
		//运行模式，文件没已压缩标记，则压缩文件并打标记
		if (!isDebug && !html.startsWith(Const.STATIC_HTML_PREFIX)) {
			//先做模板替换，后做文件压缩
			if (useHtml) {
				html = setHtmlTemplate(html);
			}
			html = Const.STATIC_HTML_PREFIX + trims(html);
			FileUtil.writeCover(file, html);
		}
		return html;
	}
	
	/**
	 * 压缩JS
	 * @param javaScript 脚本
	 * @return 压缩后的脚本
	 * */
	public static String getMinJS(String javaScript) {
		Compiler compiler = new Compiler();
		CompilerOptions compilerOptions = new CompilerOptions();
		CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions);
		SourceFile extern = SourceFile.fromCode("extern", "");
		SourceFile input = SourceFile.fromCode("input", javaScript);
		compiler.compile(extern, input, compilerOptions);
		return compiler.toSource();
	}
	
	/**
	 * 处理浏览器缓存
	 * @param request 请求对象
	 * @param file 文件对象
	 * */
	private static void dealBrowseCache(HttpServletRequest request,  HttpServletResponse response, File file) {
		//如果是线上运行模式，开启浏览器缓存，通过缓存的上次修改实际来判断缓存失效时间
		String oldLastModified = request.getHeader(Header.IF_MODIFIED_SINCE);
		//浏览器换成的资源文件的上次修改时间格式是GMT格式的，需要做一次转换处理
		String newLastModified = DateUtil.getGmtTime(file.lastModified());
		//返回文件的上次修改时间，让浏览器缓存住，下次请求的时候会带进来到Header.IF_MODIFIED_SINCE中
		response.setHeader(Header.LAST_MODIFIED, newLastModified);
		//如果文件的上次修改时间和浏览器缓存的上次修改时间一致
		if (newLastModified.equals(oldLastModified)) {
			//给浏览器一个文件未修改的状态码，让它执行加载缓存的资源文件
			response.setStatus(HttpURLConnection.HTTP_NOT_MODIFIED);
			//抛出异常，终端本次请求
			throw new BizException(SystemException.E304);
		}
		//如果走的不是缓存，则设置文件的缓存过期时间，因为这一次会读取最新的文件返回给前端
		long browseCacheExpireTime = Sys.getConfig().getBrowseCacheExpireTime();
		response.setDateHeader(Header.EXPIRES, System.currentTimeMillis() + browseCacheExpireTime);
	}
	
	/**
	 * 每行前后去空格,且去除空行
	 * @param text 文本
	 * @return 去除前后空格后的文本
	 * */
	private static String trims(String text) {
		StringBuilder stringBuilder = new StringBuilder();
		String[] lines = text.split("\\n?\\r");
		for (String line : lines) {
			String newLine = line.trim();
			if (newLine.length() != 0) {
				stringBuilder.append(newLine);
			}
		}
		return stringBuilder.toString();
	}
	
	/**
	 * 对网页文档做模板替换,在网页中引入${文件相对地址}的字符就可以把网页模板文档作为内容替换
	 * @param document 网页文档
	 * @return 模板替换后的网页文档
	 * */
	private static String setHtmlTemplate(String document) {
		return setHtmlTemplate(document, null);
	}
	
	/**
	 * 对网页文档做模板替换,在网页中引入${文件相对地址}或${全局唯一键}的字符就可以把网页模板文档或者值作为内容替换
	 * @param document 网页文档
	 * @param map 键值对缓存
	 * @return 模板替换后的网页文档
	 * */
	public static String setHtmlTemplate(String document, Map<String, String> map) {
		//如果原网页文档没内容，则返回空字符串
		if (StringUtil.isBlank(document)) {
			return "";
		}
		boolean isDebug = Sys.getConfig().getIsDebug();
		char[] chars = document.toCharArray();
		String rootPath = BaseUtil.getRootPath();
		StringBuilder doc = new StringBuilder();
		F:for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			//以$作为切入点，开始匹配模板配置
			if (c == '$') {
				//指向下一个字符
				i++;
				//如果$后面没字符了，则把$回写，退出循环
				if (i == chars.length) { doc.append("$"); break F; }
				//取出下一个字符
				c = chars[i];
				//如果匹配到了{说明离${key}更近了，进入匹配
				if (c == '{') {
					//模板配置字符串一般很短，直接用String定义变量
					String temp = "";
					while (true) {
						//指向下一个字符
						i++;
						//如果$后面没字符了，则把${回写，退出循环
						if (i == chars.length) { doc.append("${").append(temp); break F; }
						//取出下一个字符
						c = chars[i];
						//如果匹配到了}说明${key}匹配成功，开始处理模板操作
						if (c == '}') {
							//如果模板配置是网页模板，则读文件替换
							if (temp.endsWith(".html")) {
								String text = FileUtil.read(new File(rootPath + temp));
								//debug模式要换行
								doc.append(isDebug ? "\n" : "").append(text).append(isDebug ? "\n" : "");
							} else {
								//map为null或者值不存在，说明匹配无效
								if (map == null || !map.containsKey(temp)) {
									doc.append("${").append(temp).append("}");
								} else {
									//后端静态渲染用，也用于国际化处理
									doc.append(map.get(temp));
								}
							}
							//处理当前模板结束，跳出死循环
							break;
						} else {
							//前面匹配到了${，这里是${key}中key的一个字符
							temp += c;
						}
					}
				} else {
					//没有匹配到{，则需要还原，判断调试模式特殊字符
					doc.append("$").append(isDebug ? c : c != '\n' && c != '\t' ? c : "");
				}
			} else {
				//没匹配到$，则是普通网页内容，直接写入即可，同时判断调试模式特殊字符
				doc.append(isDebug ? c : c != '\n' && c != '\t' ? c : "");
			}
		}
		return doc.toString();
	}
	
	/**
	 * 设置样式模板,用在网页模板中
	 * @param document 网页文档
	 * @return 模板替换后的网页文档
	 * */
	public static String setCssTemplate(String document) {
		//如果原网页文档没内容，则返回空字符串
		if (StringUtil.isBlank(document)) {
			return "";
		}
		boolean isDebug = Sys.getConfig().getIsDebug();
		char[] chars = document.toCharArray();
		StringBuilder doc = new StringBuilder();
		String rootPath = BaseUtil.getRootPath();
		F:for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			//以$作为切入点，开始匹配模板配置
			if (c == '$') {
				//指向下一个字符
				i++;
				//如果$后面没字符了，则把$回写，退出循环
				if (i == chars.length) { doc.append("$"); break F; }
				//取出下一个字符
				c = chars[i];
				//如果匹配到了{说明离${key}更近了，进入匹配
				if (c == '{') {
					//模板配置字符串一般很短，直接用String定义变量
					String temp = "";
					while (true) {
						//指向下一个字符
						i++;
						//如果$后面没字符了，则把${回写，退出循环
						if (i == chars.length) { doc.append("${").append(temp); break F; }
						//取出下一个字符
						c = chars[i];
						//如果匹配到了}说明${key}匹配成功，开始处理模板操作
						if (c == '}') {
							//如果模板配置是样式模板，则读文件替换
							if (temp.endsWith(".css")) {
								String text = FileUtil.read(new File(rootPath + temp));
								//debug模式要换行，而且要对排版做整齐，经测试标准是两个制表位
								doc.append(isDebug ? "\n" : "").append(text).append(isDebug ? "\n\t\t" : "");
							} else {
								//样式模板只做文件模板，匹配不到就回写还原
								doc.append("${").append(temp).append("}");
							}
							//处理当前模板结束，跳出死循环
							break;
						} else {
							//前面匹配到了${，这里是${key}中key的一个字符
							temp += c;
						}
					}
				} else {
					//没有匹配到{，则需要还原，判断调试模式特殊字符
					doc.append("$").append(isDebug ? c : c != '\n' && c != '\t' ? c : "");
				}
			} else {
				//没匹配到$，则是普通样式内容，直接写入即可，同时判断调试模式特殊字符
				doc.append(isDebug ? c : c != '\n' && c != '\t' ? c : "");
			}
		}
		return doc.toString();
	}

	/**
	 * 设置脚本模板,用在网页模板中
	 * @param document 网页文档
	 * @return 模板替换后的网页文档
	 * */
	public static String setJsTemplate (String document) {
		//如果原网页文档没内容，则返回空字符串
		if (StringUtil.isBlank(document)) {
			return "";
		}
		boolean isDebug = Sys.getConfig().getIsDebug();
		char[] chars = document.toCharArray();
		StringBuilder doc = new StringBuilder();
		String rootPath = BaseUtil.getRootPath();
		F:for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			//以$作为切入点，开始匹配模板配置
			if (c == '$') {
				//指向下一个字符
				i++;
				//如果$后面没字符了，则把$回写，退出循环
				if (i == chars.length) { doc.append("$"); break F; }
				//取出下一个字符
				c = chars[i];
				//如果匹配到了}说明${key}匹配成功，开始处理模板操作
				if (c == '{') {
					//模板配置字符串一般很短，直接用String定义变量
					String temp = "";
					while (true) {
						//指向下一个字符
						i++;
						//如果$后面没字符了，则把${回写，退出循环
						if (i == chars.length) { doc.append("${").append(temp); break F; }
						//取出下一个字符
						c = chars[i];
						//如果匹配到了}说明${key}匹配成功，开始处理模板操作
						if (c == '}') {
							//如果模板配置是脚本模板，则读文件替换
							if (temp.endsWith(".js")) {
								String text = FileUtil.read(new File(rootPath + temp));
								//debug模式要换行
								doc.append(isDebug ? "\n" : "").append(text).append(isDebug ? "\n" : "");
							} else {
								//脚本模板只做文件模板，匹配不到就回写还原
								doc.append("${" + temp + "}");
							}
							//处理当前模板结束，跳出死循环
							break;
						} else {
							//前面匹配到了${，这里是${key}中key的一个字符
							temp += c;
						}
					}
				} else {
					//没有匹配到{，则需要还原，判断调试模式特殊字符
					doc.append("$").append(isDebug ? c : c != '\n' && c != '\t' ? c : "");
				}
			} else {
				//没匹配到$，则是普通样式内容，直接写入即可，同时判断调试模式特殊字符
				doc.append(isDebug ? c : c != '\n' && c != '\t' ? c : "");
			}
		}
		return doc.toString();
	}

	/**前端打包服务，扫描工程目录下所有网页文件*/
	public static void webPackage() {
		//调试模式不需要打包，直接请求源文件，不走前端缓存
		if (Sys.getConfig().getIsDebug()) return;
		String documentType = Sys.getConfig().getDocumentType();
		File root = new File(BaseUtil.getRootPath());
		//先递归获取所有文件，免得在打包服务里面做递归导致栈溢出
		List<File> files = FileUtil.listFiles(root);
		for (File file : files) {
			//如果是网页文件，那么就对网页文件做一次版本打包处理
			if (file.getName().endsWith(documentType)) {
				String doc = FileUtil.read(file);
				Document document = Jsoup.parse(doc);
				//设置所有资源的版本号，让前端缓存失效
				setSourceVersion(document);
				//重新覆盖写入网页文件
				FileUtil.writeCover(file, document.outerHtml());
			}
		}
	}

	/**
	 * 设置资源的版本号
	 * @param elements 元素集合
	 * */
	private static void setSourceVersion(Document document) {
		long version = Sys.getConfig().getVersion();
		Elements srcElements = document.getElementsByAttribute("src");
		for (int i = 0; i < srcElements.size(); i++) {
			Element element = srcElements.get(i);
			String src = element.attr("src");
			if (src.indexOf("?") == -1) {
				element.attr("src", src + "?v=" + version);	
			} else {
				element.attr("src", src + "&v=" + version);
			}
		}
		Elements linkElements = document.getElementsByTag("link");
		for (int i = 0; i < linkElements.size(); i++) {
			Element element = linkElements.get(i);
			String href = element.attr("href");
			if (href.indexOf("?") == -1) {
				element.attr("href", href + "?v=" + version);	
			} else {
				element.attr("href", href + "&v=" + version);
			}
		}
	}

}