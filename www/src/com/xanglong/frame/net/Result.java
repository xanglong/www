package com.xanglong.frame.net;

/**返回前端的数据对象*/
public class Result {

	/**状态码*/
	private String code = "200";

	/**状态值*/
	private boolean success = true;

	/**消息*/
	private String message = "成功";

	/**数据对象*/
	private Object data = null;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}