package com.xanglong.edi.service;

import java.util.List;

import com.xanglong.edi.api.EdiFrameService;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Proxy;
import com.xanglong.frame.mvc.MyService;

/**框架内部实现类*/
@MyService
public class EdiFrameServiceImpl implements EdiFrameService {

	/**
	 * 从代理服务器注册到主服务器上
	 * @param host 注册服务器地址
	 * */
	@Override
	public void proxyRegister(String host) {
		Proxy proxy = Sys.getConfig().getProxy();
		List<String> hosts = proxy.getHosts();
		hosts.add(host);
	}

}