package com.xanglong.frame.dao;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.util.BaseUtil;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**XML标签表达式解析*/
public class DaoOgnl {
	
	/**
	 * test表达式
	 * @param express 表达式
	 * @param params 参数
	 * @return 是否为真
	 * */
	public static boolean test(String express, JSONObject params) {
		boolean success = false;
		OgnlContext context = new OgnlContext(null, null, new DaoMemberAccess(true));
		context.setRoot(params);
		try {
			Object ognl = Ognl.parseExpression(express);
			success = (boolean)Ognl.getValue(ognl, context, context.getRoot());
		} catch (OgnlException e) {
			throw new BizException(e);
		}
		return success;
	}
	
	/**
	 * forEach表达式
	 * @param express 表达式
	 * @param params 参数
	 * @param attributes 属性枚举
	 * @return 拼接后的片段SQL
	 * */
	public static String foreach(String express, JSONObject params, NamedNodeMap attributes) {
		String collection = null, index = null, open = null, separator = null, close = null, item = null;
		for (int i = 0, length = attributes.getLength(); i< length; i++) {
			Node attribute = attributes.item(i);
			String nodeName = attribute.getNodeName();
			if (MapperAttribute.COLLECTION.getCode().equals(nodeName)) {
				collection = attribute.getNodeValue();
				continue;
			} else if (MapperAttribute.INDEX.getCode().equals(nodeName)) {
				index = attribute.getNodeValue();
				continue;
			} else if (MapperAttribute.OPEN.getCode().equals(nodeName)) {
				open = attribute.getNodeValue();
				continue;
			} else if (MapperAttribute.SEPARATOR.getCode().equals(nodeName)) {
				separator = attribute.getNodeValue();
				continue;
			} else if (MapperAttribute.CLOSE.getCode().equals(nodeName)) {
				close = attribute.getNodeValue();
				continue;
			} else if (MapperAttribute.ITEM.getCode().equals(nodeName)) {
				item = attribute.getNodeValue();
				continue;
			}
		}
		if (params == null || params.isEmpty()) {
			return open + "''" + close;
		}
		JSONArray datas = params.getJSONArray(collection);
		if (datas == null || datas.size() == 0) {
			return open + "''" + close;
		}
		StringBuilder stringBuilder = new StringBuilder(open);
		for (int i = 0; i < datas.size(); i++) {
			JSONObject data = new JSONObject();
			data.put(index, i);
			data.put(collection + "[" + i + "]", datas.getString(i));
			data.put(item, datas.getString(i));
			express = express.trim();
			String text = format("${", "}", express, data);
			text = format("#{", "}", text, data);
			stringBuilder.append("'").append(text).append("'");
			if (i != datas.size() - 1) {
				stringBuilder.append(separator);
			}
		}
		return stringBuilder.append(close).toString();
	}
	
	/**
	 * #{}和${}占位符格式化
	 * @param open 开始
	 * @param close 结束
	 * @param text 字符串文本
	 * @param data 参数
	 * */
	public static String format(String open, String close, String text, JSONObject data) {
		if (data == null || data.isEmpty()) {
			return text;
		}
		StringBuilder textSB = new StringBuilder();
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == open.charAt(0)) {
				String tempOpen = "";
				boolean isMatchOpen = false;
				for (int j = 0; j < open.length(); j++) {
					if (c == open.charAt(j)) {
						tempOpen += c;
						isMatchOpen = true;
					} else {
						textSB.append(tempOpen);
						break;
					}
					i++;
					if (i == chars.length) {
						isMatchOpen = false;
						textSB.append(tempOpen);
						break;
					}
					c = chars[i];
				}
				if (!isMatchOpen) {
					continue;
				}
				String key = "";
				boolean isMatchClose = false;
				char close0 = close.charAt(0);
				String tempClose = "";
				W:while (true) {
					if (c == close0) {
						for (int j = 0; j < close.length(); j++) {
							if (c == close.charAt(j)) {
								tempClose += c;
								isMatchClose = true;
							} else {
								textSB.append(open).append(key).append(tempClose);
								isMatchClose = false;
								break W;
							}
							if (j == close.length() - 1) {
								break;
							}
							i++;
							if (i == chars.length) {
								isMatchClose = false;
								textSB.append(open).append(key).append(tempClose);
								break W;
							}
							c = chars[i];
						}
						if (isMatchClose) {
							break;
						}
					} else {
						key += c;
						i++;
						if (i == chars.length) {
							isMatchClose = false;
							textSB.append(open).append(key);
							break;
						}
						c = chars[i];
					}
				}
				if (!isMatchClose) {
					continue;
				}
				Object value = BaseUtil.getChainValue(data, key);
				textSB.append(value == null ? "" : value);
			} else {
				textSB.append(c);
			}
		}
        return textSB.toString();
	}

}