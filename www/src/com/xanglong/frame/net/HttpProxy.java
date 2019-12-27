package com.xanglong.frame.net;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Proxy;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.util.MathUtil;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.FrameException;

public class HttpProxy {
	
	/**
	 * 执行代理转发服务，作为代理不应该处理异常，所以抛出所有异常
	 * @param request 请求对象
	 * @param response 响应对象
	 * @param bytes 二进制数据
	 * */
	public static void forward(HttpServletRequest request, HttpServletResponse response, byte[] bytes) {
		Proxy proxy = Sys.getConfig().getProxy();
		RequestDto requestDto = new RequestDto();
		//设置请求类型
		requestDto.setContentType(HttpUtil.getContentType(request.getContentType()));
		//设置请求头参数，加入转发凭证
		JSONObject headerParams = HttpUtil.getHeaderParams(request);
		headerParams.put(Header.PROXY_AUTHORIZATION, proxy.getAuthorization());
		requestDto.setHeaderParams(headerParams);
		//获取要随机转发的服务器地址
		List<String> hosts = proxy.getHosts();
		if (hosts.size() == 0) {
			throw new BizException(FrameException.FRAME_NO_PROXY_SLAVE_SERVER);
		}
		String host = hosts.size() > 1 ? hosts.get(MathUtil.getRandomIndex(hosts.size())) : hosts.get(0);
		//获取查询参数，并设置目标服务器请求地址，GET和POST都支持
		String query = request.getQueryString();
		query = query == null ? "" : "?" + query;
		String url = host + request.getRequestURI() + query;
		requestDto.setUrl(url);
		String method = request.getMethod();
		ResponseDto responseDto = null;
		try {
			if (Method.GET.getCode().equals(method)) {
				responseDto = HttpUtil.doGet(requestDto);
			} else if (Method.POST.getCode().equals(method)) {
				//考虑到递归重试，二进制数据流不能被重复，那么可以用传参解决
				bytes = bytes == null ? HttpUtil.getBytes(request) : bytes;
				requestDto.setBytes(bytes);
				responseDto = HttpUtil.doPost(requestDto);
			} else {
				throw new BizException(FrameException.FRAME_REQUEST_METHOD_INVALID, method);
			}
		} catch(BizException e) {
			Throwable throwable = e.getThrowable();
			//如果不是连接拒绝错误，则抛出异常出去
			if (throwable != null && "Connection refused: connect".equals(throwable.getMessage())) {
				//如果转发的从服务器连接拒绝，则当前服务器移除注册信息
				List<String> newHosts = new ArrayList<>();
				for (String hos : hosts) {
					if (!hos.equals(host)) {
						newHosts.add(hos);
					}
				}
				proxy.setHosts(newHosts);
				//移除没用的服务器后，继续转发当前请求
				forward(request, response, bytes);
			} else {
				throw e;
			}
		}
		//返回代理返回的结果
		HttpUtil.responseDto(response, responseDto);
	}
	
	/**
	 * 执行代理授权，过滤非法请求
	 * @param request 请求对象
	 * */
	public static void authorize(HttpServletRequest request) {
		Proxy proxy = Sys.getConfig().getProxy();
		String authorization = proxy.getAuthorization();
		String proxyAuthorization = request.getHeader(Header.PROXY_AUTHORIZATION);
		//转发凭证不能为空
		if (StringUtil.isBlank(proxyAuthorization)) {
			throw new BizException(FrameException.FRAME_PROXY_AUTHORIZATION_NULL);
		}
		//转发凭证无效
		if (!authorization.equals(proxyAuthorization)) {
			throw new BizException(FrameException.FRAME_PROXY_AUTHORIZATION_INVALID, proxyAuthorization);
		}
		String ip = proxy.getIp();
		String proxyIp = HttpUtil.getIP(request);
		//IP地址非法，只允许转发服务器的IP地址通过
		if (!ip.equals(proxyIp)) {
			throw new BizException(FrameException.FRAME_PROXY_IP_INVALID, proxyIp);
		}
	}

}