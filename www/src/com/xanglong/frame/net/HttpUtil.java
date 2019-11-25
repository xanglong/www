package com.xanglong.frame.net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xanglong.frame.config.Const;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.util.StringUtil;

public class HttpUtil {

	/**
	 * 返回字符
	 * @param response 响应对象
	 * @param text 内容
	 * */
	public static void responseText(HttpServletResponse response, ContentType contentType, String text) {
		response.setContentType(contentType.getCode() + ";charset=" + Const.CHARSET_STR);
		try (PrintWriter printWriter = response.getWriter();) {
			printWriter.append(text);
		} catch (IOException e) {
			throw new BizException(e);
		}
	}

	/**
	 * 重定向
	 * @param request 请求对象
	 * @param response 响应对象
	 * */
	public static void forward(HttpServletRequest request, HttpServletResponse response, String uri) {
		try {
			response.sendRedirect(request.getContextPath() + uri);
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 返回图片
	 * @param response 响应对象
	 * @param bytes 二进制数据
	 * @param imageType 图片类型
	 * @param imageName 图片名称
	 * */
	public static void responseImage(HttpServletResponse response, byte[] bytes, ImageType imageType, String imageName) {
		response.resetBuffer();
		response.addHeader(Header.CONTENT_LENGTH_KEY, "" + bytes.length);
		response.setContentType(imageType.getType());
		try (
			ServletOutputStream sos = response.getOutputStream();
			OutputStream outputStream = new BufferedOutputStream(sos);
		) {
			if (!StringUtil.isBlank(imageName)) {
				imageName = new String(imageName.getBytes(Const.CHARSET), "ISO-8859-1");
				response.setHeader(Header.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(imageName, Const.CHARSET_STR));
			}
			outputStream.write(bytes);
			outputStream.flush();
			response.flushBuffer();
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 返回文件
	 * @param response 响应对象
	 * @param file 文件对象
	 * @param 文件名称
	 * */
	public static void responseFile(HttpServletResponse response, File file, String fileName) {
		response.setContentType(ContentType.FORM_DATA.getCode());
		try (
			OutputStream outputStream = response.getOutputStream();
			InputStream inputStream = new FileInputStream(file);
		){
			fileName = new String(fileName.getBytes("iso8859-1"), Const.CHARSET);
			response.setHeader(Header.CONTENT_DISPOSITION_KEY, "attachment;filename=" + fileName);
			byte[] buffer = new byte[1024];
			int i = -1;
			while ((i = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, i);
			}
			outputStream.flush();
		} catch (IOException e) {
			throw new BizException(e);
		}
	}


}