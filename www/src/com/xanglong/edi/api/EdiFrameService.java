package com.xanglong.edi.api;

/**框架内部接口类*/
public interface EdiFrameService {
	
	/**
	 * 从代理服务器注册到主服务器上
	 * @param host 注册服务器地址
	 * */
	void proxyRegister(String host);

}