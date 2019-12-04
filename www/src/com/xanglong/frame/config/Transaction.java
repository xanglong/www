package com.xanglong.frame.config;

public class Transaction {
	
	/**在控制层*/
	private boolean onController;
	
	/**在业务层*/
	private boolean onService;

	public boolean isOnController() {
		return onController;
	}

	public void setOnController(boolean onController) {
		this.onController = onController;
	}

	public boolean isOnService() {
		return onService;
	}

	public void setOnService(boolean onService) {
		this.onService = onService;
	}

}