package com.xanglong.frame.log;

public class LogData {
	
	LogData(Throwable throwable, String logPath, String dateTime) {
		this.throwable = throwable;
		this.logPath = logPath;
		this.setDateTime(dateTime);
	}
	
	/**异常对象*/
	private Throwable throwable;
	
	/**存储地址*/
	private String logPath;
	
	/**日期时间*/
	private String dateTime;

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

}