package com.xanglong.frame.dao;

/**分页查询实体*/
public class PageParam {

	/**开始行*/
	private int start = 0;

	/**查询行数*/
	private int length = 1;

	/**是否统计总数*/
	private boolean isCount = false;
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean getIsCount() {
		return isCount;
	}

	public void setIsCount(boolean isCount) {
		this.isCount = isCount;
	}

}