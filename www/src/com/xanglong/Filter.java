package com.xanglong;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xanglong.frame.Current;
import com.xanglong.frame.config.ConfigManager;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.exception.ThrowableHandler;
import com.xanglong.frame.net.Header;
import com.xanglong.frame.net.Source;
import com.xanglong.frame.net.SourceInfo;
import com.xanglong.frame.net.SourceType;
import com.xanglong.frame.session.Session;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.FrameException;

public class Filter implements javax.servlet.Filter {

	public void init(FilterConfig filterConfig) {
		try {
			//加载配置
			new ConfigManager().loadConfig();
		} catch (Throwable exception) {
			ThrowableHandler.dealException(exception);
			//配置加载失败的情况，整个应用退出，必须把异常处理好
			System.exit(1);
		}
	}

	public void destroy() {}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		//转换请求对象、响应对象
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;

		try {
			//发现如果在本地开发时，像迅雷、优酷、腾讯视频等软件会发起本地请求造成一定开发干扰，拦截这些请求
			//有些刚入门的爬虫选手会用代码或者工具进行抓包爬虫，也拦截部分没有用户代理的请求，减小服务器压力
			String userAgent = request.getHeader(Header.USER_AGENT);
			if (StringUtil.isBlank(userAgent)) {
				throw new BizException(FrameException.FRAME_CAN_NOT_FIND_USER_AGENT);
			}
	
			//开始会话
			Session.start(request);
			//获取请求资源类型
			SourceInfo sourceInfo = Source.getSourceInfo(request.getRequestURI());
	
			if (SourceType.ACTION == sourceInfo.getSourceType()) {
				//执行请求链，默认被servlet拦截器拦截然后分发请求，所以也可以引入Spring这样的框架来托管
				filterChain.doFilter(servletRequest, servletResponse);
				//如果要做网页后端渲染要介入代码渲染的话会配置网页也是动作请求，如果找不到对应的处理器则需要返回为网页请求
				sourceInfo = Current.getSourceInfo();
			}
	
			//如果是资源请求，则返回资源
			if (SourceType.ACTION != sourceInfo.getSourceType()) {
				Source.execute(sourceInfo, response);
			}
			
			//结束会话
			Session.end(request);
		} catch (Throwable exception) {
			ThrowableHandler.dealException(exception, request, response);
		}
	}
	
}