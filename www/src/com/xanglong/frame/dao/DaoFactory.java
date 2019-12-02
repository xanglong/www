package com.xanglong.frame.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.Current;
import com.xanglong.frame.exception.BizException;
import com.xanglong.i18n.zh_cn.FrameException;

public class DaoFactory {
	
	/**
	 * MySQL批量执行SQL语句
	 * @param sqls 要批量执行的SQL语句数组
	 * @return 每条语句执行是否成功标记数组
	 * */
	protected static int[] executeBatchSQL4MySQL(String[] sqls) {
		Connection connection = Current.getConnection().getConnection();
		int[] rts = null;
		try (Statement statement = connection.createStatement();) {
			for (String sql : sqls) {
				statement.addBatch(sql);
			}
			rts = statement.executeBatch();
		} catch (SQLException e) {
			throw new BizException(e);
		}
		return rts;
	}
	
	/**
	 * MySQL执行SQL语句
	 * @param sql 要执行的SQL语句
	 * @return 语句执行是否成功的标记
	 * */
	protected static boolean executeSQL4MySQL(String sql) {
		Connection connection = Current.getConnection().getConnection();
		boolean success = false;
		try (Statement statement = connection.createStatement();) {
			success = statement.execute(sql);
		} catch (SQLException e) {
			throw new BizException(e);
		}
		return success;
	}
	
	/**
	 * 获取预编译SQL
	 * 说明：传入DAO操作的参数后，逐个解析SQL中的参数，并按照顺序存储参数名，最后返回
	 * @param daoParam DAO层操作参数
	 * @return 预编译SQL对象
	 * */
	private static PrepareSql getPrepareSql(DaoParam daoParam) {
		/*参数准备*/
		JSONObject webParams = daoParam.getWebParams();//前端入参
		PrepareSql prepareSql = new PrepareSql();//预编译SQL对象
		StringBuilder prepareSB = new StringBuilder();//预编译SQL
		StringBuilder executeSB = new StringBuilder();//可执行SQL
		StringBuilder keySB = new StringBuilder();//参数名称
		StringBuilder keyTempSB = new StringBuilder();//参数名称临时变量
		char[] sqlChars = daoParam.getSql().toCharArray();//SQL字符串数组
		/*开始解析*/
		for (int i = 0; i < sqlChars.length; i++) {
			char c = sqlChars[i];
			if (c == '#' || c == '$') {
				char start = c;
				i++;
				c = sqlChars[i];
				//#$后面不允许有空格，必须紧跟{
				if (c == '{') {
					A:while (true) {
						i++;
						c = sqlChars[i];
						if (c == '}') {
							//当匹配到了有参数名称时，则参数不能为空
							if (webParams == null || webParams.isEmpty()) {
								throw new BizException(FrameException.FRAME_SQL_PARAM_NULL);
							}
							//参数对象中必须包含指定参数名称的参数
							String key = keyTempSB.toString();
							if (!webParams.containsKey(key)) {
								throw new BizException(FrameException.FRAME_SQL_MISS_PARAM, key);
							}
							if (start == '#') {
								//如果是#开头的参数，则只需要把参数名称替换即可
								prepareSB.append("?");
								//缓存参数名称
								keySB.append(keyTempSB).append(",");
								//所有参数值都包裹上英文单引号符号，保证SQL语句不报类型错误
								executeSB.append("'").append(webParams.getString(keyTempSB.toString())).append("'");
							} else if (start == '$') {
								//这里面就不能再有#{}这种操作了，递归搞死人，然后校验也不做了，开发的时候自测即可
								String value = webParams.getString(key);
								//预编译SQL语句完整替换
								prepareSB.append(value);
								//可执行SQL语句完整替换
								executeSB.append(value);
							}
							//清空参数名称临时变量
							keyTempSB = new StringBuilder();
							//普配到右括号就可以退出匹配了
							break A;
						} else if (c != ' ') {
							//支持#{}，${}过滤括号里面字符串的空格
							keyTempSB.append(c);
						}
						if (i == sqlChars.length - 1) {
							//字符串匹配结束了都没匹配到右花括号，说明SQL写的有问题
							throw new BizException(FrameException.FRAME_SQL_MISS_RIGHT_CURLY_BRACE);
						}
					}
				} else {
					//没有匹配到#$后面的{则把字符串还原到SQL上
					prepareSB.append(sqlChars[i - 1]).append(c);
					executeSB.append(sqlChars[i - 1]).append(c);
				}
			} else {
				//普通SQL部分直接拼接
				prepareSB.append(c);
				executeSB.append(c);
			}
		}
		//提取预编译和可执行SQL
		daoParam.setExeSql(executeSB.toString());
		prepareSql.setPreSql(prepareSB.toString());
		//如果参数名称不为空，则提取参数名称数组
		if (keySB.length() > 0) {
			prepareSql.setKeys(keySB.substring(0, keySB.length() - 1).split(","));	
		} else {
			prepareSql.setKeys(new String[0]);
		}
		return prepareSql;
	}

	/**
	 * MySQL删除
	 * @param daoParam DAO层操作参数
	 * @return 删除是否成功标记
	 * */
	protected static int delete4MySQL(DaoParam daoParam) {
		Connection connection = Current.getConnection().getConnection();
		PrepareSql prepareSql = getPrepareSql(daoParam);
		String preSql = prepareSql.getPreSql();
		String[] keys = prepareSql.getKeys();
		JSONObject webParams = daoParam.getWebParams();
		int rt = -1;
		try (PreparedStatement preparedStatement = connection.prepareStatement(preSql);) {
			for (int i = 0; i < keys.length; i++) {
				preparedStatement.setObject(i + 1, webParams.get(keys[i]));
			}
			rt = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new BizException(e);
		}
		return rt;
	}
	
	/**
	 * MySQL更新
	 * @param daoParam DAO层操作参数
	 * @return 更新是否成功标记
	 * */
	protected static int update4MySQL(DaoParam daoParam) {
		Connection connection = Current.getConnection().getConnection();
		PrepareSql prepareSql = getPrepareSql(daoParam);
		String preSql = prepareSql.getPreSql();
		String[] keys = prepareSql.getKeys();
		JSONObject webParams = daoParam.getWebParams();
		int rt = -1;
		try (PreparedStatement preparedStatement = connection.prepareStatement(preSql);) {
			for (int i = 0; i < keys.length; i++) {
				preparedStatement.setObject(i + 1, webParams.get(keys[i]));
			}
			rt = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new BizException(e);
		}
		return rt;
	}

	/**
	 * MySQL新增
	 * @param daoParam DAO层操作参数
	 * @return 新增是否成功标记
	 * */
	protected static int insert4MySQL(DaoParam daoParam) {
		Connection connection = Current.getConnection().getConnection();
		PrepareSql prepareSql = getPrepareSql(daoParam);
		String preSql = prepareSql.getPreSql();
		String[] keys = prepareSql.getKeys();
		JSONObject webParams = daoParam.getWebParams();
		int rt = -1;
		try (PreparedStatement preparedStatement = connection.prepareStatement(preSql);) {
			for (int i = 0; i < keys.length; i++) {
				preparedStatement.setObject(i + 1, webParams.get(keys[i]));
			}
			rt = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new BizException(e);
		}
		return rt;
	}
	
	/**
	 * MySQL查询
	 * @param daoParam DAO层操作参数
	 * @return 查询结果数据
	 * */
	protected static DaoData select4MySQL(DaoParam daoParam) throws SQLException {
		DaoData daoVo = new DaoData();
		Connection connection = Current.getConnection().getConnection();
		PrepareSql prepareSql = getPrepareSql(daoParam);
		String preSql = prepareSql.getPreSql();
		String[] keys = prepareSql.getKeys();
		PageParam pageParam = daoParam.getPageParam();
		JSONObject webParams = daoParam.getWebParams();
		//分页参数不为空，则要处理分页查询数据
		if (pageParam != null && pageParam.getLength() != 0) {
			//如果统计总条数
			if (pageParam.getIsCount()) {
				String upperPreSql = Pattern.compile("\r|\n").matcher(preSql.toUpperCase()).replaceAll("");
				//考虑到ORDER BY中间可以写空格，所以这里只能用ORDER快捷处理
				int indexOfOrder = upperPreSql.lastIndexOf("ORDER");
				if (indexOfOrder != -1) {
					String lastPartPreSql = upperPreSql.substring(indexOfOrder);
					//替换ORDER后面字符串中的空格
					lastPartPreSql = lastPartPreSql.replaceAll("\\s", "");
					//如果是ORDER BY起头的字符串，且不是在''单引号值内的ORDER BY字符串，则判定为是ORDER BY语句
					if (lastPartPreSql.startsWith("ORDERBY") && lastPartPreSql.indexOf("'") == -1) {
						upperPreSql = upperPreSql.substring(0, indexOfOrder);
					}
				}
				//统计一下总条数
				String countSql = "SELECT COUNT(1) TOTALCOUNT  FROM (" + upperPreSql + ") T";
				try (PreparedStatement preparedStatement = connection.prepareStatement(countSql);){
					for (int i = 0; i < keys.length; i++) {
						preparedStatement.setObject(i + 1, webParams.get(keys[i]));
					}
					ResultSet resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						daoVo.setTotalCount(resultSet.getInt("TOTALCOUNT"));
					}
				}
				preSql += " LIMIT " + pageParam.getStart() + " , " + pageParam.getLength();
			}
		}
		try (PreparedStatement preparedStatement = connection.prepareStatement(preSql);) {
			for (int i = 0; i < keys.length; i++) {
				//注意是i+1
				preparedStatement.setObject(i + 1, webParams.get(keys[i]));
			}
			JSONArray datas = null;
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				//指针指向尾部
				resultSet.last();
				//初始化数组大小，减少动态扩容的性能损耗
				datas = new JSONArray(resultSet.getRow());
				//指针指向头部
				resultSet.beforeFirst();
			}
			while (resultSet.next()) {
				//获取元数据
				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				//获取列字段数
				int columnCount = resultSetMetaData.getColumnCount();
				JSONObject rowData = new JSONObject();
				for (int i = 0; i < columnCount; i++) {
					String columnLabel = resultSetMetaData.getColumnLabel(i + 1);
					//字段名小写赋值，注意这个字段名是取的标签别名，为了避免重复字段问题
					rowData.put(columnLabel.toLowerCase(), resultSet.getObject(columnLabel));
				}
				datas.add(rowData);
			}
			//所有查询格式都返回数组格式数据
			daoVo.setDatas(datas);
		}
		return daoVo;
	}


}