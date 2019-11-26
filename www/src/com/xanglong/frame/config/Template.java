package com.xanglong.frame.config;

public class Template {

	/**启用网页模板*/
	private boolean useHtml;
	
	/**启用样式模板*/
	private boolean useCss;
	
	/**启用脚本模板*/
	private boolean useJs;

	public boolean getUseHtml() {
		return useHtml;
	}

	public void setUseHtml(boolean useHtml) {
		this.useHtml = useHtml;
	}

	public boolean getUseCss() {
		return useCss;
	}

	public void setUseCss(boolean useCss) {
		this.useCss = useCss;
	}

	public boolean getUseJs() {
		return useJs;
	}

	public void setUseJs(boolean useJs) {
		this.useJs = useJs;
	}

}