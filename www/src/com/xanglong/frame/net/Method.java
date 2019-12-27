package com.xanglong.frame.net;

/**请求类型枚举*/
public enum Method {
	
	GET("GET", "请求指定页面的信息，并返回实体主体，幂等"),
	POST("POST", "向指定资源提交数据进行处理请求，数据存在请求体，非幂等"),
	HEAD("HEAD", "类似get，但不返回具体内容，用于获取报头，幂等"),
	PUT("PUT", "完整替换更新指定资源数据，没有就新增，幂等"),
	DELETE("DELETE", "删除指定资源的数据，幂等"),
	PATCH("PATCH", "部分更新指定资源的数据，非幂等"),
	OPTIONS("OPTIONS", "允许客户端查看服务器的支持的http请求方法"),
	CONNECT("CONNECT", "预留给能将连接改为管道的代理服务器"),
	TRACE("trace", "追踪服务器收到的请求，用于测试或诊断");
	
	Method(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	private String code;

	private String name;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

}