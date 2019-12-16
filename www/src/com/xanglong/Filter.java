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
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.ConfigManager;
import com.xanglong.frame.config.EdiConst;
import com.xanglong.frame.config.Proxy;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.exception.ThrowableHandler;
import com.xanglong.frame.net.Header;
import com.xanglong.frame.net.HttpProxy;
import com.xanglong.frame.net.Method;
import com.xanglong.frame.net.Source;
import com.xanglong.frame.net.SourceInfo;
import com.xanglong.frame.net.SourceType;
import com.xanglong.frame.session.Session;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.FrameException;

public class Filter implements javax.servlet.Filter {
	
	public void init(FilterConfig filterConfig) {
		try {
			ConfigManager configManager = new ConfigManager();
			//加载配置
			configManager.loadConfig();
			//注册服务
			configManager.registerServer();
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
		String uri = request.getRequestURI();
		try {
			//校验请求
			checkRequest(request);
			Proxy proxy = Sys.getConfig().getProxy();
			if (proxy.getIsOpen() && proxy.getIsProxy() && !uri.startsWith(EdiConst.EDI_FRAME)) {
				//代理的非业务异常会被系统捕获处理
				HttpProxy.forward(request, response);
			} else {
				//如果开启了代理，则需要做授权
				if (proxy.getIsOpen()) {
					HttpProxy.authorize(request);
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
	
	/**
	 * 校验请求
	 * @param request 请求对象
	 * */
	private void checkRequest(HttpServletRequest request) {
		//校验浏览器代理
		String userAgent = request.getHeader(Header.USER_AGENT);
		if (StringUtil.isBlank(userAgent)) {
			throw new BizException(FrameException.FRAME_USER_AGENT_NULL);
		}
		//校验请求方法
		String method = request.getMethod();
		if (!Method.GET.name().equals(method) && !Method.POST.name().equals(method)) {
			throw new BizException(FrameException.FRAME_REQUEST_METHOD_INVALID, method);
		}
	}
	
}