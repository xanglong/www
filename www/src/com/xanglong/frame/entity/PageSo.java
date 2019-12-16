package com.xanglong.frame.entity;

public class PageSo {
	
	/**分页参数*/
	private BasePage page;
	
	/**搜索参数*/
	private BaseSo so;

	public BasePage getPage() {
		return page;
	}

	public void setPage(BasePage page) {
		this.page = page;
	}

	public BaseSo getSo() {
		return so;
	}

	public void setSo(BaseSo so) {
		this.so = so;
	}

}