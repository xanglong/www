package com.xanglong.edi.controller;

import com.xanglong.edi.api.EdiFrameService;
import com.xanglong.frame.config.EdiConst;
import com.xanglong.frame.mvc.MyAutowired;
import com.xanglong.frame.mvc.MyController;
import com.xanglong.frame.mvc.MyRequestMapping;

/**框架内部控制类*/
@MyController
@MyRequestMapping(EdiConst.EDI_FRAME)
public class EdiFrameController {
	
	@MyAutowired
    private EdiFrameService ediFrameService;
	
	/**
	 * 从代理服务器注册到主服务器上
	 * @param request 请求对象
	 * @return 返回注册地址
	 * */
	@MyRequestMapping(EdiConst.PROXY_REGISTER_DO)
	public void proxyRegister(String host) {
		ediFrameService.proxyRegister(host);
	}

}