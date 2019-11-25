package com.xanglong.frame.net;

import java.io.File;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletResponse;

import com.xanglong.frame.Current;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.io.FileUtil;
import com.xanglong.frame.util.BaseUtil;
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
	
	/**执行资源请求*/
	public static void execute(SourceInfo sourceInfo, HttpServletResponse response) {
		String rootPath = BaseUtil.getRootPath();
		String uri = sourceInfo.getRequestURI();
		String filePath = rootPath + uri;
		File file = new File(filePath);
		if (file.exists()) {
			if (SourceType.HTML == sourceInfo.getSourceType()) {
				String html = FileUtil.read(file);
				HttpUtil.responseText(response, ContentType.HTML, html);
			} else if (SourceType.CSS == sourceInfo.getSourceType()) {
				String css = FileUtil.read(file);
				HttpUtil.responseText(response, ContentType.CSS, css);
			} else if (SourceType.JS == sourceInfo.getSourceType()) {
				String js = FileUtil.read(file);
				HttpUtil.responseText(response, ContentType.JS, js);
			} else if (SourceType.IMAGE == sourceInfo.getSourceType()) {
				byte[] bytes = FileUtil.readByte(file);
				//不设置文件名称在浏览器用新窗口打开就不会变成图片下载了
				HttpUtil.responseImage(response, bytes, sourceInfo.getImageType(), null);
			} else if (SourceType.FONT == sourceInfo.getSourceType()) {
				HttpUtil.responseFile(response, file, file.getName());
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

}