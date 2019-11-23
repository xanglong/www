package com.xanglong;

import java.io.File;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.exception.ThrowableHandler;
import com.xanglong.frame.io.FileUtil;
import com.xanglong.frame.net.ContentType;
import com.xanglong.frame.net.Header;
import com.xanglong.frame.net.HttpUtil;
import com.xanglong.frame.session.Session;
import com.xanglong.frame.util.BaseUtil;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.LanguageManager;
import com.xanglong.i18n.zh_cn.FrameException;

public class Filter implements javax.servlet.Filter {
	
	public void init(FilterConfig filterConfig) {
		try {
			//初始化加载所有语言包
			new LanguageManager().init();
		} catch (Throwable exception) {
			ThrowableHandler.dealException(exception);
			//配置加载失败的情况，整个应用退出，必须把异常处理好
			System.exit(1);
		}
	}

	public void destroy() {}
	
	private Session session = new Session();

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		
		//转换请求对象、响应对象
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		
		//发现如果在本地开发时，像迅雷、优酷、腾讯视频等软件会发起本地请求造成一定开发干扰，拦截这些请求
		//有些刚入门的爬虫选手会用代码或者工具进行抓包爬虫，也拦截部分没有用户代理的请求，减小服务器压力
		String userAgent = request.getHeader(Header.USER_AGENT);
		if (StringUtil.isBlank(userAgent)) {
			throw new BizException(FrameException.FRAME_CAN_NOT_FIND_USER_AGENT);
		}

		//开始会话
		session.start(request);

		String uri = request.getRequestURI();
		if ("/".equals(uri)) {
			File file = new File(BaseUtil.getRootPath() + Config.WELCOME_FILE);
			String html = FileUtil.read(file);
			HttpUtil.responseText(response, ContentType.HTML, html);
		}
		
	}

}