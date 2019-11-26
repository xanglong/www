package com.xanglong.frame.config;

public class Log {
	
	/**开启业务异常日志*/
	private boolean isBizException;
	
	/**是否开启系统异常日志*/
	private boolean isSystemException;
	
	/**日志存放文件夹*/
	private String logBasePath;
	
	/**正常请求日志文件夹*/
	private String infoPath;
	
	/**业务异常日志文件夹*/
	private String bizExceptionPath;
	
	/**系统异常日志文件夹*/
	private String systemExceptionPath;
	
	/**系统错误日志文件夹：默认强制开启日志*/
	private String errorPath;
	
	public boolean getIsBizException() {
		return isBizException;
	}

	public void setIsBizException(boolean isBizException) {
		this.isBizException = isBizException;
	}

	public boolean getIsSystemException() {
		return isSystemException;
	}

	public void setIsSystemException(boolean isSystemException) {
		this.isSystemException = isSystemException;
	}

	public String getLogBasePath() {
		return logBasePath;
	}

	public void setLogBasePath(String logBasePath) {
		this.logBasePath = logBasePath;
	}

	public String getInfoPath() {
		return infoPath;
	}

	public void setInfoPath(String infoPath) {
		this.infoPath = infoPath;
	}

	public String getBizExceptionPath() {
		return bizExceptionPath;
	}

	public void setBizExceptionPath(String bizExceptionPath) {
		this.bizExceptionPath = bizExceptionPath;
	}

	public String getSystemExceptionPath() {
		return systemExceptionPath;
	}

	public void setSystemExceptionPath(String systemExceptionPath) {
		this.systemExceptionPath = systemExceptionPath;
	}

	public String getErrorPath() {
		return errorPath;
	}

	public void setErrorPath(String errorPath) {
		this.errorPath = errorPath;
	}

}