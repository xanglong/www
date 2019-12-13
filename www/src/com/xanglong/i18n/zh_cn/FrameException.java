package com.xanglong.i18n.zh_cn;

import com.xanglong.frame.exception.IException;
import com.xanglong.i18n.I18n;

public enum FrameException implements IException, I18n {
	
	FRAME_USER_AGENT_NULL("找不到请求的用户代理"),
	FRAME_CONFIG_FOLDER_NULL("找不到配置文件夹{0}"),
	FRAME_CONFIG_FILE_INVALID("配置文件无效{0}"),
	FRAME_UNSUPPORTED_SOURCE_TYPE("不支持的请求资源类型{0},请求地址{1}"),
	FRAME_FILE_NULL("找不到请求的文件{0}"),
	FRAME_URL_ILLEGAL("URL地址非法{0}"),
	FRAME_CONTENT_TYPE_NULL("接口请求文档类型不能为空"),
	FRAME_REQUEST_PARAM_NULL("接口请求参数不能为空"),
	FRAME_PROXY_AUTHORIZATION_NULL("转发凭证不能为空"),
	FRAME_PROXY_AUTHORIZATION_INVALID("转发凭证无效{0}"),
	FRAME_PROXY_IP_INVALID("当前请求IP非法{0}"),
	FRAME_REQUEST_METHOD_INVALID("请求类型不支持{0}"),
	FRAME_DATABASE_TYPE_INVALID("不支持的数据库类型{0}"),
	FRAME_SQL_MISS_RIGHT_CURLY_BRACE("预编译SQL语句缺失右花括号"),
	FRAME_SQL_PARAM_NULL("SQL参数不能为空"),
	FRAME_SQL_MISS_PARAM("SQL缺失参数{0}"),
	FRAME_CLASS_MISS_ANNOTATION_MYREPOSITORY("{0}类缺少注解@MyRepository"),
	FRAME_CLASS_MISS_ANNOTATION_MYCONTROLLER("{0}类缺少注解@MyController"),
	FRAME_CLASS_MISS_ANNOTATION_MYREQUESTMAPPING("{0}类缺少注解@MyRequestMapping"),
	FRAME_METHOD_MISS_ANNOTATION_MYREQUESTMAPPING("{0}方法缺少注解@MyRequestMapping"),
	FRAME_METHOD_ANNOTATION_MYREQUESTMAPPING_REPEAT("{0}方法@yRequestMapping注解重复"),
	FRAME_CLASS_ANNOTATION_MYCONTROLLER_INVALID("{0}类不支持引入有@MyController注解的Bean"),
	FRAME_MAPPER_MISS_ROOT_NODE("{0}缺失根节点<mapper></mapper>"),
	FRAME_MAPPER_NAMESPACE_NULL("{0}的namespace不能为空"),
	FRAME_MAPPER_NAMESPACE_REPEAT("{0}的namespace不能重复 {1}"),
	FRAME_MAPPER_NODE_ATTRIBUTE_INVALID("{0}不支持的属性{1}"),
	FRAME_MAPPER_NODE_RESULT_TYPE_INVALID("{0}的{1}节点的返回类型{2}不支持"),
	FRAME_MAPPER_NODE_INVALID("{0}不支持的节点{1}"),
	FRAME_MAPPER_NODE_ATTRIBUTE_VALUE_NULL("{0}节点{1}属性值{2}不能为空"),
	FRAME_MAPPER_NAMESPACE_CLASS_NULL("{0}命名空间{1}对应的接口类不存在"),
	FRAME_MAPPER_NODE_ID_INVALID("{0}节点{1}的属性id值{2}不能包含点"),
	FRAME_MAPPER_NODE_REFID_NULL("{0}节点{1}的依赖refid值{2}不存在"),
	FRAME_MAPPER_NODE_REFID_FILE_NULL("{0}节点{1}的依赖refid值{2}找不到所属文件"),
	FRAME_MAPPER_NODE_RECURSION_CHECK_NULL("{0}的{1}节点赞未实现递归校验"),
	FRAME_MAPPER_NODE_CHECK_NULL("{0}的{1}节点赞未实现值校验"),
	FRAME_NAMESPACE_NULL("{0}命名空间不存在"),
	FRAME_NAMESPACE_SQL_NULL("{0}命名空间不存在id为{1}的SQL"),
	FRAME_MAPPER_NODE_SQL_INVALID("节点{0}暂未支持SQL解析"),
	FRAME_MAPPER_NODE_TYPE_INVALID("节点类型{0}暂未被支持解析"),
	FRAME_MAPPER_NAMESPACE_ID_REPEAT("{0}命名空间id为{1}的节点重复"),
	FRAME_REQUEST_URI_INVALID("无效的请求地址{0}"),
	FRAME_REQUEST_NOT_ARRAY_PARAMETER("请求前端入参不是数组格式{0}"),
	FRAME_REQUEST_ARRAY_PARAMETER_CONVERT_ERROR("请求的数组参数项转换对象失败"),
	FRAME_REQUEST_PARAMETER_INVALID("参数{0}非法，不支持的类型{1}"),
	FRAME_REQUEST_PARAMETER_NULL("参数{0}不能为空"),
	FRAME_VALIDATION_MAX_VALUE_CANNOT_BIGGER_THAN("{0}的最大值不能大于{1}"),
	FRAME_VALIDATION_MIN_VALUE_CANNOT_LESS_THAN("{1}的最小值不能小于{1}"),
	FRAME_VALIDATION_VALUE_CONNOT_BE_TRUE("{0}的值应为false"),
	FRAME_VALIDATION_VALUE_CONNOT_BE_FLASE("{0}的值应为true"),
	FRAME_VALIDATION_VALUE_CONNOT_BE_NULL("{0}的值不能为null"),
	FRAME_VALIDATION_MAX_LENGTH_CANNOT_BIGGER_THAN("{0}的长度不能大于{1}"),
	FRAME_VALIDATION_MIN_LENGTH_CANNOT_LESS_THAN("{0}的长度不能小于{1}"),
	FRAME_VALIDATION_VALUE_INVALID("{0}的值非法，不符合规则"),
	FRAME_COPY_FILE_EXIST("目标文件{0}已存在，但是是个文件夹"),
	FRAME_COPY_FILE_FAIL("从文件{0}复制到文件{1}失败"),
	FRAME_MYAUTOWIRED_CANNOT_BE_INTERFACE("@MyAutowired不允许自动注入类{0}，请换成实现类"),
	FRAME_METHOD_PARAMETER_INVALID("不支持的对象类型{0}"),
	FRAME_MOST_ONE_KEY_IN_THE_RESULT("查询结果最多只能有一个字段才可以正确返回集合结构"),
	;
	
	private String code;
	
	private String message;
	
	private FrameException(String message) {
		this.code = this.name();
		this.message = message;
	}

	public String getCode() {
		return this.code;
	}

	public String getMessage() {
		return this.message;
	}
	
	public String getName() {
		return this.message;
	}

}