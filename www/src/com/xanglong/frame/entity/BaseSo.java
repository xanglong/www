package com.xanglong.frame.entity;

/**搜索实体*/
public class BaseSo {

	/**主键*/
	private Long id;

	/**唯一主键*/
	private String uuid;

	/**创建时间开始*/
	private String createTimeStart;
	
	/**创建时间结束*/
	private String createTimeEnd;

	/**更新时间开始*/
	private String updateTimeStart;
	
	/**更新时间结束*/
	private String updateTimeEnd;

	/**版本号开始*/
	private Long verStart;
	
	/**版本号结束*/
	private Long verEnd;

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

	public String getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getUpdateTimeStart() {
		return updateTimeStart;
	}

	public void setUpdateTimeStart(String updateTimeStart) {
		this.updateTimeStart = updateTimeStart;
	}

	public String getUpdateTimeEnd() {
		return updateTimeEnd;
	}

	public void setUpdateTimeEnd(String updateTimeEnd) {
		this.updateTimeEnd = updateTimeEnd;
	}

	public Long getVerStart() {
		return verStart;
	}

	public void setVerStart(Long verStart) {
		this.verStart = verStart;
	}

	public Long getVerEnd() {
		return verEnd;
	}

	public void setVerEnd(Long verEnd) {
		this.verEnd = verEnd;
	}

}