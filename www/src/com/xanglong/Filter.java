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
import com.xanglong.frame.MyProxy;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.ConfigManager;
import com.xanglong.frame.config.Proxy;
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
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		try {
			String userAgent = request.getHeader(Header.USER_AGENT);
			if (StringUtil.isBlank(userAgent)) {
				throw new BizException(FrameException.FRAME_USER_AGENT_NULL);
			}
			Proxy proxy = Sys.getConfig().getProxy();
			if (proxy.getIsOpen() && proxy.getIsProxy()) {
				//代理的非业务异常会被系统捕获处理
				MyProxy.forward(request, response);
			} else {
				//如果开启了代理，则需要做授权
				if (proxy.getIsOpen()) {
					MyProxy.authorize(request);
				}
				//会话开始
				Session.start(request);
				SourceInfo sourceInfo = Source.getSourceInfo(request.getRequestURI());
				if (SourceType.ACTION == sourceInfo.getSourceType()) {
					filterChain.doFilter(servletRequest, servletResponse);
					//判断页面不需要做后端渲染时会重置资源类型为网页
					sourceInfo = Current.getSourceInfo();
				}
				if (SourceType.ACTION != sourceInfo.getSourceType()) {
					//执行资源请求
					Source.execute(request, response, sourceInfo);
				}
				//会话结束
				Session.end(request);
			}
		} catch (Throwable exception) {
			ThrowableHandler.dealException(exception, request, response);
		}
	}
	
}