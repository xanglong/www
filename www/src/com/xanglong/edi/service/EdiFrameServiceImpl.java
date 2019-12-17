package com.xanglong.edi.service;

import com.xanglong.edi.api.EdiFrameService;
import com.xanglong.frame.Sys;
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
		Sys.getConfig().getProxy().getHosts().add(host);
	}

}