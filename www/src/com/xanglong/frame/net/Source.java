package com.xanglong.frame.net;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.xanglong.frame.Current;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Config;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.io.FileUtil;
import com.xanglong.frame.util.BaseUtil;
import com.xanglong.frame.util.DateUtil;
import com.xanglong.i18n.zh_cn.FrameException;
import com.xanglong.i18n.zh_cn.SystemException;
import net.coobird.thumbnailator.Thumbnails;

public class Source {
	
	/**
	 * 获取请求资源类型：公开外部方法
	 * @param uri 请求地址
	 * @return 请求资源类型 
	 * */
	public static SourceInfo getSourceInfo(String uri) {
		//如果请求没指定类型和文件名称，则按照欢饮页处理
		if (uri.charAt(uri.length() - 1) == '/') {
			uri += Const.WELCOME_FILE;
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
		int idx = type.indexOf(".");
		if (idx != -1) {
			type = type.substring(idx + 1);
			for (String actionType : Const.ACTION_TYPES) {
				if (actionType.equals(type)) {
					//如果是动作请求，直接返回即可
					sourceInfo.setSourceType(SourceType.ACTION);
					return sourceInfo;
				}
			}
		}
		//整个处理顺序按照请求的频率从高到低排列
		if (Const.DOCUMENT_TYPE.equals(type)) {
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
		throw new BizException(FrameException.FRAME_UNSUPPORTED_SOURCE_TYPE, type, uri);
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
			if (SourceType.HTML == sourceInfo.getSourceType()) {
				String html = FileUtil.read(file);
				//运行模式，文件没已压缩标记，则压缩文件并打标记
				if (!isDebug && !html.startsWith(Const.STATIC_HTML_PREFIX)) {
					html = Const.STATIC_HTML_PREFIX + trims(html);
					FileUtil.writeCover(file, html);
				}
				HttpUtil.responseText(response, ContentType.HTML, html);
			} else {
				if (!isDebug) {
					//浏览器缓存处理
					dealBrowseCache(request, response, file);
				}
				if (SourceType.CSS == sourceInfo.getSourceType()) {
					String css = FileUtil.read(file);
					//运行模式，文件没已压缩标记，则压缩文件并打标记
					if (!isDebug && !css.startsWith(Const.STATIC_CSS_PREFIX)) {
						css = Const.STATIC_CSS_PREFIX + trims(css);
						FileUtil.writeCover(file, css);
					}
					HttpUtil.responseText(response, ContentType.CSS, css);
				} else if (SourceType.JS == sourceInfo.getSourceType()) {
					String js = FileUtil.read(file);
					//运行模式，文件没已压缩标记，则压缩文件并打标记
					if (!isDebug && !js.startsWith(Const.STATIC_JS_PREFIX)) {
						//这里的压缩文件要用到谷歌的一款插件closure-compiler
						js = Const.STATIC_JS_PREFIX + getMinJS(js);
						FileUtil.writeCover(file, js);
					}
					HttpUtil.responseText(response, ContentType.JS, js);
				} else if (SourceType.IMAGE == sourceInfo.getSourceType()) {
					byte[] bytes = FileUtil.readByte(file);
					//不设置文件名称在浏览器用新窗口打开就不会变成图片下载了
					HttpUtil.responseImage(response, bytes, sourceInfo.getImageType(), null);
				} else if (SourceType.FONT == sourceInfo.getSourceType()) {
					HttpUtil.responseFile(response, file, file.getName());
				}
			}
		} else {
			if (SourceType.HTML == sourceInfo.getSourceType()) {
				throw new BizException(SystemException.E404);
			} else {
				response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
				throw new BizException(FrameException.FRAME_CONT_NOT_FIND_FILE, uri);
			}
		}
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
	 * 压缩图片：默认模式
	 * @param bytes 图片二进制数据
	 * @param height 图片高度
	 * @param width 图片宽度
	 * @return 二进制数据
	 * */
	public static byte[] getImageBySize(byte[] bytes, int height, int width) {
		return getImageBySize(bytes, height, width, "jpg");
	}
	
	/**
	 * 压缩图片：带图片格式
	 * @param bytes 图片二进制数据
	 * @param height 图片高度
	 * @param width 图片宽度
	 * @return 二进制数据
	 * */
	public static byte[] getImageBySize(byte[] bytes, int height, int width, String type) {
		byte[] minBytes = null;
		try {
			BufferedImage bufferedImage = FileUtil.getBufferedImage(bytes);
			try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
				bufferedImage = Thumbnails.of(bufferedImage).size(height, width).asBufferedImage();
				ImageIO.write(bufferedImage, type, byteArrayOutputStream);
				minBytes = byteArrayOutputStream.toByteArray();
            }
		} catch (IOException e) {
			throw new BizException(e);
		}
		return minBytes;
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
		response.setDateHeader(Header.EXPRIES, System.currentTimeMillis() + browseCacheExpireTime);
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

}