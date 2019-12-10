package com.xanglong.frame.entity;

import java.util.Date;

public class BaseVo {

	/**主键*/
	private Long id;

	/**唯一主键*/
	private String uuid;

	/**创建时间*/
	private Date createTime;

	/**更新时间*/
	private Date updateTime;

	/**版本号*/
	private Long ver;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getVer() {
		return ver;
	}

	public void setVer(Long ver) {
		this.ver = ver;
	}
	
}