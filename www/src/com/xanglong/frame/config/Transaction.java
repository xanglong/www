package com.xanglong.frame.config;

public class Transaction {
	
	/**在控制层*/
	private boolean onController;
	
	/**在业务层*/
	private boolean onService;
	
	/**在业务层方法上*/
	private boolean onServiceMethod;

	public boolean getOnController() {
		return onController;
	}

	public void setOnController(boolean onController) {
		this.onController = onController;
	}

	public boolean getOnService() {
		return onService;
	}

	public void setOnService(boolean onService) {
		this.onService = onService;
	}

	public boolean getOnServiceMethod() {
		return onServiceMethod;
	}

	public void setOnServiceMethod(boolean onServiceMethod) {
		this.onServiceMethod = onServiceMethod;
	}

}