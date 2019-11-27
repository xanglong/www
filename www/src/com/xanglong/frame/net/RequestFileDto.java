package com.xanglong.frame.net;

public class RequestFileDto {

	/**文件名称key*/
	private String name;
	
	/**文件数据*/
	private byte[] bytes;
	
	/**文件名称value*/
	private String fileName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}