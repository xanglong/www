package com.xanglong.frame.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.Reflect;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.dao.Dao;
import com.xanglong.frame.entity.BasePo;
import com.xanglong.frame.entity.BaseSo;
import com.xanglong.frame.entity.BaseVo;
import com.xanglong.frame.entity.EntityValidation;
import com.xanglong.frame.entity.ResultVo;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.net.BodyParam;
import com.xanglong.frame.net.ContentType;
import com.xanglong.frame.net.HttpUtil;
import com.xanglong.frame.util.EntityUtil;
import com.xanglong.i18n.zh_cn.FrameException;

/**请求路由转发调度类*/
public class MyDispatcher extends HttpServlet {

	private static final long serialVersionUID = 5381797813487796314L;
	
	/**初始化*/
	public void init(){}
	
	/**销毁*/
	public void destroy(){}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		String requestURI = request.getRequestURI();
		Object controller = ControllerBean.getController(requestURI);
		if (controller == null) {
			response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
			throw new BizException(FrameException.FRAME_REQUEST_URI_INVALID, requestURI);
		}
		//获取请求对应的方法和方法定义的参数
		Method method = ControllerBean.getMethod(requestURI);
		//这是好参方法反射的参数
		Object[] args = getParameters(request, response, method);
		try {
			//执行反射方法调用controller的方法
			Object result = method.invoke(controller, args);
			//事务提交控制，如果没开启则不会做提交
			Dao.sysCommit();
			//约定，返回类型是void的不返回
			Class<?> returnType = method.getReturnType();
			if (returnType != void.class) {
				ResultVo resultVo = new ResultVo();
				resultVo.setData(result);
				String contentType = ContentType.JSON.getCode() + ";charset=" + Const.CHARSET_STR;
				response.setContentType(contentType);
				try (PrintWriter printWriter = response.getWriter();) {
					printWriter.append(JSONObject.toJSONString(resultVo));
				}
			}
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
			//如果有事务开启则要回滚事务
			Dao.rollback();
			throw new BizException(e);
		}
	}
	
	/**
	 * 解析请求参数到反射的方法中
	 * @param request 请求对象
	 * @param response 响应对象
	 * @param method 方法
	 * */
	private Object[] getParameters(HttpServletRequest request, HttpServletResponse response, Method method) {
		JSONObject bodyParams = null;
		JSONArray listBodyParams = null;
		//提取请求体参数
		BodyParam bodyParam = HttpUtil.getBodyParams(request);
		Parameter[] parameters = method.getParameters();
		Object[] args = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			Class<?> parameterType = parameter.getType();
			String paramName = parameter.getName();
			//如果Controller的入参是集合结构
			if (Collection.class.isAssignableFrom(parameterType)) {
				//当方法参数有多个的时候，不能每次都解析数组参数，做一个三目判断
				listBodyParams = listBodyParams == null ? bodyParam.getArray() : listBodyParams;
				if (listBodyParams == null) {
					throw new BizException(FrameException.FRAME_REQUEST_NOT_ARRAY_PARAMETER, paramName);
				}
				//如果是数组参数，则其一定有泛型类型，获取其泛型类型结构，我就不做成Spring那样强大了，只支持一维数组
				parameterType = Reflect.getParameterTypeClassList(method).get(0);
				List<Object> listParams = new ArrayList<>(listBodyParams.size());
				for (int j = 0, size = listBodyParams.size(); j < size; j++) {
					//只支持在数组中嵌套对象，嵌套数组这种复杂操作不支持
					Object param = getMethodParam(parameterType, listBodyParams.getJSONObject(j), paramName);
					if (param == null) {
						throw new BizException(FrameException.FRAME_REQUEST_ARRAY_PARAMETER_CONVERT_ERROR, paramName);
					}
					listParams.add(param);
				}
				args[i] = listParams;
				continue;
			} else {
				bodyParams = bodyParams == null ? bodyParam.getObejct() : bodyParams;
			}
			if (HttpServletRequest.class == parameterType) {
				args[i] = request;
			} else if (HttpServletResponse.class == parameterType) {
				args[i] = response;
			} else {
				args[i] = getMethodParam(parameter.getType(), bodyParams, paramName);
			}
		}
		return args;
	}
	
	/**
	 * 获取方法体映射的参数
	 * @param parameterType 参数类型
	 * @param bodyParams 单个参数实体数据
	 * @param paramName 参数名
	 * @return 参数实体对象
	 * */
	private Object getMethodParam(Class<?> parameterType, JSONObject bodyParams, String paramName) {
		if (BasePo.class.isAssignableFrom(parameterType)) {
			Object obj = EntityUtil.getPo(bodyParams, parameterType);
			if (obj == null) {
				throw new BizException(FrameException.FRAME_REQUEST_PARAMETER_NULL, paramName);
			}
			//实现参数校验
			EntityValidation.doVerificatio(obj);
			return obj;
		} else if (BaseVo.class.isAssignableFrom(parameterType)) {
			Object obj = EntityUtil.getVo(bodyParams, parameterType);
			if (obj == null) {
				throw new BizException(FrameException.FRAME_REQUEST_PARAMETER_NULL, paramName);
			}
			//实体参数校验
			EntityValidation.doVerificatio(obj);
			return obj;
		} else if (BaseSo.class.isAssignableFrom(parameterType)) {
			Object obj = EntityUtil.getSo(bodyParams, parameterType);
			if (obj == null) {
				throw new BizException(FrameException.FRAME_REQUEST_PARAMETER_NULL, paramName);
			}
			//实体参数校验
			EntityValidation.doVerificatio(obj);
			return obj;
		} else {
			Object baseValue = getBaseParam(parameterType, paramName, bodyParams);
			if (baseValue == null) {
				throw new BizException(FrameException.FRAME_REQUEST_PARAMETER_NULL, paramName);
			}
			return baseValue;
		}
	}
	
	/**
	 * 获取基础类型参数
	 * @param parameterType 参数的类
	 * @param paramName 参数名称
	 * @param bodyParams 前端入参
	 * @return 参数值对象
	 * */
	private Object getBaseParam(Class<?> parameterType, String paramName, JSONObject bodyParams) {
		if (String.class == parameterType) {
			return bodyParams.getString(paramName);
		} else if (int.class == parameterType) {
			return bodyParams.getIntValue(paramName);
		} else if (long.class == parameterType) {
			return bodyParams.getLongValue(paramName);
		} else if (boolean.class == parameterType) {
			return bodyParams.getBooleanValue(paramName);
		} else if (double.class == parameterType) {
			return bodyParams.getDoubleValue(paramName);
		} else if (Integer.class == parameterType) {
			return bodyParams.getInteger(paramName);
		} else if (Long.class == parameterType) {
			return bodyParams.getLong(paramName);
		}  else if (Boolean.class == parameterType) {
			return bodyParams.getBoolean(paramName);
		} else if (Double.class == parameterType) {
			return bodyParams.getDouble(paramName);
		} else if (float.class == parameterType) {
			return bodyParams.getFloatValue(paramName);
		} else if (Float.class == parameterType) {
			return bodyParams.getFloat(paramName);
		} else {
			throw new BizException(FrameException.FRAME_REQUEST_PARAMETER_INVALID, paramName, parameterType.getName());
		}
	}

}