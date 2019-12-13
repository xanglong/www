package com.xanglong.frame.mvc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.Reflect;
import com.xanglong.frame.dao.Dao;
import com.xanglong.frame.dao.DaoData;
import com.xanglong.frame.dao.DaoMapper;
import com.xanglong.frame.dao.DaoParam;
import com.xanglong.frame.dao.MapperSql;
import com.xanglong.frame.dao.SqlType;
import com.xanglong.frame.entity.BasePo;
import com.xanglong.frame.entity.BaseSo;
import com.xanglong.frame.entity.EntityValidation;
import com.xanglong.frame.entity.PageSo;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.util.EntityUtil;
import com.xanglong.i18n.zh_cn.FrameException;

public class RepositoryHandler implements InvocationHandler {
	
	@Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//如果是实现类方法调用，那就按照实现类的方法执行反射
		Class<?> clazz = method.getDeclaringClass();
		if (Object.class.equals(clazz)) {
			return method.invoke(this, args);
		}
		//获取DAO接口入参，分页参数则从so对象里面抓取参数
		DaoParam daoParam = getDaoParam(method, args);
		//获取解析后的SQL,节点类型不支持会抛出异常
		MapperSql mapperSql = DaoMapper.getMapperSql(clazz.getName(), method.getName(), daoParam.getParams());
		mapperSql.setPreSql(mapperSql.getPreSql());
		SqlType sqlType = mapperSql.getSqlType();
		if (SqlType.SELECT == sqlType) {
			DaoData daoData = Dao.select(daoParam);
			Class<?> returnType = method.getReturnType();
			if (DaoData.class == returnType) {
				return daoData;
			}
			if (JSONArray.class == returnType) {
				return daoData.getDatas();
			}
			return getData(method, daoData.getDatas());
		} else if (SqlType.INSERT == sqlType ) {
			int rt = Dao.insert(daoParam);
			return rt;
		} else if (SqlType.UPDATE == sqlType) {
			int rt = Dao.update(daoParam);
			return rt;
		} else if (SqlType.DELETE == sqlType) {
			int rt = Dao.delete(daoParam);
			return rt;
		}
		return null;
    }
	
	/**
     * 获取基本类型值，在DAO层不需要考虑多层嵌套结构
     * @param method 方法
     * @param datas 数据
     * @return data 反射数据对象
     * */
    private Object getData(Method method, JSONArray datas) {
    	Class<?> returnType = method.getReturnType();
    	if (Collection.class.isAssignableFrom(returnType) || returnType.isArray()) {
    		if (datas == null || datas.size() == 0) {
				return new ArrayList<>();
			}
    		//获取泛型参数的实际参数对象类型
    		List<Class<?>> typeList = Reflect.getReturnTypes(method);
    		//只支持一维数组，嵌套多维数组没有多大意义
    		Class<?> type = typeList.get(0);
    		boolean isBaseType = EntityUtil.isBaseType(type);
    		boolean isArray = returnType.isArray();
    		List<Object> typeDatas = new ArrayList<>(datas.size());
    		Object[] objectDatas = new Object[datas.size()];
			for (int i = 0; i < datas.size(); i++) {
				JSONObject data = datas.getJSONObject(i);
				if (isBaseType) {
					//如果是基础类，只需要获取一个字段名就行
					Set<String> keySet = data.keySet();
					if (keySet.size() > 1) {
		    			throw new BizException(FrameException.FRAME_MOST_ONE_KEY_IN_THE_RESULT);
		    		}
					String key = keySet.iterator().next();
					Object value = EntityUtil.getBaseValue(data, type, key);
					if (isArray) objectDatas[i] = value; else typeDatas.add(value);
				} else {
					Object value = EntityUtil.getDaoBean(data, type);
					if (isArray) objectDatas[i] = value; else typeDatas.add(value);
				}
			}
			return isArray ? objectDatas : typeDatas;
    	}
    	//反射生成单对象结果
		if (datas == null || datas.size() == 0) {
    		return EntityUtil.getDefaultValue(returnType);
    	}
		//支持返回JSON对象
		JSONObject data = datas.getJSONObject(0);
		if (JSONObject.class == returnType) {
			return data;
		}
		//如果是基础类，只需要获取一个字段名就行
		Set<String> keySet = data.keySet();
		if (keySet.size() > 1) {
			throw new BizException(FrameException.FRAME_MOST_ONE_KEY_IN_THE_RESULT);
		}
		String key = keySet.iterator().next();
		boolean isBaseType = EntityUtil.isBaseType(returnType);
		return isBaseType ? EntityUtil.getBaseValue(data, returnType, key) : EntityUtil.getDaoBean(data, returnType);
    }
    
	
	/**
	 * 根据方法定义提取所需要的参数
	 * @param method 方法
	 * @param args 参数列表
	 * @return DAO层所需要的参数
	 * */
    private DaoParam getDaoParam(Method method, Object[] args) {
    	DaoParam daoParam = new DaoParam();
		Parameter[] parameters = method.getParameters();
		JSONObject params = new JSONObject();
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			Parameter parameter = parameters[i];
			String paramName = parameter.getName();
			Class<?> parameterType = parameter.getType();
			if (BasePo.class.isAssignableFrom(parameterType)) {
				//很多业务逻辑下入参是VO对象，最终入库的是PO对象，所以入库前需要做一次校验
				EntityValidation.doVerificatio(arg);
				params.putAll((JSONObject) JSON.toJSON(arg));
			} else if (BaseSo.class.isAssignableFrom(parameterType)) {
				params.putAll(getBaseSoJson(arg));
			} else if (PageSo.class.isAssignableFrom(parameterType)) {
				PageSo pageSo = (PageSo) arg;
				//要把分页查询参数的搜索参数塞到查询参数中去
				params.putAll(getBaseSoJson(pageSo.getSo()));
			} else if (EntityUtil.isBaseType(parameterType) || List.class == parameterType) {
				//支持基础类型类参数和List类型参数,批量操作的时候会用到List参数
				params.put(paramName, arg);
			} else {
				throw new BizException(FrameException.FRAME_METHOD_PARAMETER_INVALID, parameterType.getName());
			}
		}
		daoParam.setParams(params);
		return daoParam;
    }
    
    /**
     * 获取需要转换的SO搜索对象
     * @param arg 参数对象
     * @return JSON对象
     * */
    private JSONObject getBaseSoJson(Object arg) {
    	if (arg == null) {
    		return new JSONObject();
    	}
    	JSONObject soJson = (JSONObject) JSON.toJSON(arg);
		if (soJson != null) {
			Set<String> keySet = soJson.keySet();
			for (String key : keySet) {
				Object val = soJson.get(key);
				//在数据库中布尔类型的查询只能用0或者1，这里需要做一次值转换
				if (val != null && (Boolean.class == val.getClass() || boolean.class == val.getClass())) {
					soJson.put(key, soJson.getBooleanValue(key) ? 1 : 0);
				}
			}
		}
		return soJson;
    }

}