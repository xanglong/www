package com.xanglong.frame.net;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xanglong.Config;
import com.xanglong.frame.exception.BizException;

public class HttpUtil {

	/**
	 * 返回字符
	 * @param response 响应对象
	 * @param text 内容
	 * */
	public static void responseText(HttpServletResponse response, ContentType contentType, String text) {
		response.setContentType(contentType.getCode() + ";charset=" + Config.DEFAUTL_CHARSET);
		try (PrintWriter printWriter = response.getWriter();) {
			printWriter.append(text);
		} catch (IOException e) {
			throw new BizException(e);
		}
	}

	/**重定向*/
	public static void forward(HttpServletRequest request, HttpServletResponse response, String uri) {
		try {
			response.sendRedirect(request.getContextPath() + uri);
		} catch (IOException e) {
			throw new BizException(e);
		}
	}

}