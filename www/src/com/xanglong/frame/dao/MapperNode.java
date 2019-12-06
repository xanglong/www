package com.xanglong.frame.dao;

/**XML文件节点与属性定义枚举类*/
public enum MapperNode {
	
	MAPPER_NAMESPACE(MapperTag.MAPPER, MapperAttribute.NAMESPACE, true),
	SQL_ID(MapperTag.SQL, MapperAttribute.ID, true),
	INCLUDE_REFID(MapperTag.INCLUDE, MapperAttribute.REFID, true),
	SELECT_ID(MapperTag.SELECT, MapperAttribute.ID, true),
	INSERT_ID(MapperTag.INSERT, MapperAttribute.ID, true),
	UPDATE_ID(MapperTag.UPDATE, MapperAttribute.ID, true),
	DELETE_ID(MapperTag.DELETE, MapperAttribute.ID, true),
	SELECTKEY_KEYPROPERTY(MapperTag.SELECTKEY, MapperAttribute.KEYPROPERTY, true),
	SELECTKEY_ORDER(MapperTag.SELECTKEY, MapperAttribute.ORDER, false),
	SELECTKEY_RESULTTYPE(MapperTag.SELECTKEY, MapperAttribute.RESULTTYPE, true),
	IF_TEST(MapperTag.IF, MapperAttribute.TEST, true),
	FOREACH_OPEN(MapperTag.FOREACH, MapperAttribute.OPEN, false),
	FOREACH_CLOSE(MapperTag.FOREACH, MapperAttribute.CLOSE, false),
	FOREACH_SEPARATOR(MapperTag.FOREACH, MapperAttribute.SEPARATOR, false),
	FOREACH_COLLECTION(MapperTag.FOREACH, MapperAttribute.COLLECTION, true),
	FOREACH_ITEM(MapperTag.FOREACH, MapperAttribute.ITEM, false),
	FOREACH_INDEX(MapperTag.FOREACH, MapperAttribute.INDEX, true),
	;

	MapperNode(MapperTag tag, MapperAttribute attribute, boolean isRequire) {
		this.tag = tag;
		this.attribute = attribute;
		this.isRequire = isRequire; 
	}

	private MapperTag tag;
	
	private MapperAttribute attribute;
	
	private boolean isRequire;

	public MapperTag getTag() {
		return tag;
	}

	public MapperAttribute getAttribute() {
		return attribute;
	}

	public boolean getIsRequire() {
		return isRequire;
	}

}