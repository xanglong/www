package com.xanglong.frame.config;

public class Packages {
	
	/**基础包路径*/
	private String base;

	/**控制层包路径*/
	private String[] controller;

	/**SQL文件包路径*/
	private String[] mapper;

	/**业务层包路径*/
	private String[] service;

	/**DAO层包路径*/
	private String[] dao;

	/**打了@MyComponent注解的类所在的包路径*/
	private String[] component;
	
	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}
	
	public String[] getController() {
		return controller;
	}

	public void setController(String[] controller) {
		this.controller = controller;
	}

	public String[] getMapper() {
		return mapper;
	}

	public void setMapper(String[] mapper) {
		this.mapper = mapper;
	}

	public String[] getService() {
		return service;
	}

	public void setService(String[] service) {
		this.service = service;
	}

	public String[] getDao() {
		return dao;
	}

	public void setDao(String[] dao) {
		this.dao = dao;
	}

	public String[] getComponent() {
		return component;
	}

	public void setComponent(String[] component) {
		this.component = component;
	}

}