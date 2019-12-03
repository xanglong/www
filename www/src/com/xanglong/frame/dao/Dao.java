package com.xanglong.frame.dao;

import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.Current;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Config;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.util.DateUtil;
import com.xanglong.i18n.zh_cn.FrameException;

public class Dao {
	
	private Dao() {}

	/**开启事务*/
	public static void begin() {
		Config config = Sys.getConfig();
		String type = config.getDatabase().getType();
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			DaoConnection daoConnection = Current.getConnection();
			if (daoConnection.getIsBegin()) {
				return;
			}
			daoConnection.setIsBegin(true);
			String[] sqls = new String[2];
			sqls[0] = "SET AUTOCOMMIT = 0";
			sqls[1] = "START TRANSACTION";
			try {
				DaoFactory.executeBatchSQL4MySQL(sqls);
				if (config.getIsDebug()) {
					System.out.println(DateUtil.getDateTime() + " 开启事务");
				}
			} catch (Exception e) {
				if (config.getIsDebug()) {
					e.printStackTrace();
				}
				rollback();
				throw new BizException(e);
			}
		} else {
			throw new BizException(FrameException.FRAME_DATABASE_TYPE_INVALID);
		}
	}

	/**业务级别提交事务，用于在业务中提交事务，提交后数据库连接不释放*/
	public static void commit() {
		//如果事务没有开启，则不做提交和释放操作
		DaoConnection daoConnection = Current.getConnection();
		if (!daoConnection.getIsBegin()) {
			return;
		}
		Config config = Sys.getConfig();
		String type = config.getDatabase().getType();
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			String[] sqls = new String[2];
			if (!daoConnection.getIsError()) {
				sqls[0] = "COMMIT";
			}
			//阶段性提交事务之后，继续开启事务，当前连接不变
			sqls[1] = "START TRANSACTION";
			try {
				DaoFactory.executeBatchSQL4MySQL(sqls);
				if (config.getIsDebug()) {
					System.out.println(DateUtil.getDateTime() + " 手动提交事务");
				}
			} catch (Exception e) {
				if (config.getIsDebug()) {
					e.printStackTrace();
				}
				rollback();
				throw new BizException(e);
			}
			Current.getConnection().setIsError(false);
		} else {
			throw new BizException(FrameException.FRAME_DATABASE_TYPE_INVALID);
		}
	}

	/**系统级别提交事务,用于系统级别事务控制，提交后数据库连接释放*/
	public static void sysCommit() {
		//如果事务没有开启，则不做提交和释放操作
		if (!Current.getConnection().getIsBegin()) {
			return;
		}
		Config config = Sys.getConfig();
		String type = config.getDatabase().getType();
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			Current.getConnection().setIsBegin(false);
			String[] sqls = new String[2];
			if (Current.getConnection().getIsError()) {
				sqls[0] = "COMMIT";
			}
			//设置自动提交事务，如果查询也开启了事务，那么会卡死
			sqls[1] = "SET AUTOCOMMIT = 1";
			try {
				DaoFactory.executeBatchSQL4MySQL(sqls);
				if (config.getIsDebug()) {
					System.out.println(DateUtil.getDateTime() + " 系统提交事务");
				}
			} catch (Exception e) {
				if (config.getIsDebug()) {
					e.printStackTrace();
				}
				rollback();
				throw new BizException(e);
			}
			Current.getConnection().setIsError(false);
			freeConn();
		} else {
			throw new BizException(FrameException.FRAME_DATABASE_TYPE_INVALID);
		}
	}

	/**释放连接*/
	public static void freeConn() {
		if (Current.getConnection() != null) {
			DaoManager.freeConnection(Current.getConnection());
		}
	}

	/**回滚事务*/
	public static void rollback() {
		Config config = Sys.getConfig();
		Current.getConnection().setIsError(true);
		String type = config.getDatabase().getType();
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			try {
				DaoFactory.executeSQL4MySQL("ROLLBACK");
				//事务回滚之后，标记没有错误
				Current.getConnection().setIsError(false);
			} catch (Exception e) {
				if (config.getIsDebug()) {
					e.printStackTrace();
				}
				throw new BizException(e);
			}
		} else {
			throw new BizException(FrameException.FRAME_DATABASE_TYPE_INVALID);
		}
	}
	
	/**
	 * 打印
	 * @param start SQL运行开始时间
	 * @param daoParam DAO层操作参数
	 * */
	private static void console(long start, DaoParam daoParam) {
		long end = System.currentTimeMillis();
		String dateTime = DateUtil.getDateTime();
		System.out.println(dateTime + " 耗时：" + (end - start) + "毫秒");
		StringBuilder sqlSB1 = new StringBuilder();
		StringBuilder sqlSB2 = new StringBuilder("\n");
		String[] lines = daoParam.getSql().split("\\r?\\n");
		for (String line : lines) {
			String newLine = line.trim();
			if (newLine.length() != 0) {
				sqlSB1.append(newLine).append(" ");
				sqlSB2.append(line).append("\n");
			}
		}
		StringBuilder sqlSB3 = new StringBuilder("\n"); 
		StringBuilder sqlSB4 = new StringBuilder(""); 
		String[] lines2 = daoParam.getExeSql().split("\\r?\\n");
		for (String line : lines2) {
			String newLine = line.trim();
			if (newLine.length() != 0) {
				sqlSB4.append(newLine).append(" ");
				sqlSB3.append(line).append("\n");
			}
		}
		JSONObject webParams = daoParam.getWebParams();
		System.out.println(dateTime + " 参数：" + (webParams == null ? null : webParams.toJSONString()));
		System.out.println(dateTime + " 原始：" + sqlSB1.toString());
		System.out.println(dateTime + " 执行：" + sqlSB4.toString());
		System.out.print(dateTime + " 原始：" + sqlSB2.toString());
		System.out.print(dateTime + " 执行：" + sqlSB3.toString());
	}
	
	/**
	 * 查询
	 * @param sql SQL语句
	 * @return 查询数据结果
	 * */
	public static DaoData select(String sql) {
		DaoParam daoParam = new DaoParam();
		daoParam.setSql(sql);
		return select(daoParam);
	}

	/**
	 * 查询
	 * @param daoParam DAO层操作参数
	 * @return 查询数据结果
	 * */
	public static DaoData select(DaoParam daoParam) {
		DaoData daoVo = new DaoData();
		if (Current.getConnection().getIsError()) {
			return daoVo;
		}
		Config config = Sys.getConfig();
		String type = config.getDatabase().getType();
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			long start = System.currentTimeMillis();
			try {
				daoVo = DaoFactory.select4MySQL(daoParam);
			} catch (Exception e) {
				if (config.getIsDebug()) {
					e.printStackTrace();
					console(start, daoParam);
				}
				rollback();
				throw new BizException(e);
			}
			if (config.getIsDebug()) {
				console(start, daoParam);
			}
		} else {
			throw new BizException(FrameException.FRAME_DATABASE_TYPE_INVALID);
		}
		return daoVo;
	}

	/**
	 * 新增
	 * @param sql SQL语句
	 * @return 是否成功标志
	 * */
	public static int insert(String sql) {
		DaoParam daoParam = new DaoParam();
		daoParam.setSql(sql);
		return insert(daoParam);
	}
	
	/**
	 * 新增
	 * @param daoParam DAO层操作参数
	 * @return 是否成功标志 
	 * */
	public static int insert(DaoParam daoParam) {
		int rt = -1;
		if (Current.getConnection().getIsError()) {
			return rt;
		}
		begin();
		Config config = Sys.getConfig();
		String type = config.getDatabase().getType();
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			long start = System.currentTimeMillis();
			try {
				rt = DaoFactory.insert4MySQL(daoParam);
			} catch (Exception e) {
				if (config.getIsDebug()) {
					e.printStackTrace();
					console(start, daoParam);
				}
				rollback();
				throw new BizException(e);
			}
			if (config.getIsDebug()) {
				console(start, daoParam);
			}
		} else {
			throw new BizException(FrameException.FRAME_DATABASE_TYPE_INVALID);
		}
		return rt;
	}

	/**
	 * 编辑
	 * @param sql SQL语句
	 * @return 是否成功标志
	 * */
	public static int update(String sql) {
		DaoParam daoParam = new DaoParam();
		daoParam.setSql(sql);
		return update(daoParam);
	}
	
	/**
	 * 编辑
	 * @param daoParam DAO层操作参数
	 * @return 是否成功标志
	 * */
	public static int update(DaoParam daoParam) {
		int rt = -1;
		if (Current.getConnection().getIsError()) {
			return rt;
		}
		begin();
		Config config = Sys.getConfig();
		String type = config.getDatabase().getType();
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			long start = System.currentTimeMillis();
			try {
				rt = DaoFactory.update4MySQL(daoParam);
			} catch (Exception e) {
				if (config.getIsDebug()) {
					e.printStackTrace();
					console(start, daoParam);
				}
				rollback();
				throw new BizException(e);
			}
			if (config.getIsDebug()) {
				console(start, daoParam);
			}
		} else {
			throw new BizException(FrameException.FRAME_DATABASE_TYPE_INVALID);
		}
		return rt;
	}

	/**
	 * 删除
	 * @param sql SQL语句
	 * @return 是否成功标志
	 * */
	public static int delete(String sql) {
		DaoParam daoParam = new DaoParam();
		daoParam.setSql(sql);
		return delete(daoParam);
	}
	
	/**
	 * 删除
	 * @param daoParam DAO层操作参数
	 * @return 是否成功标志
	 * */
	public static int delete(DaoParam daoParam) {
		int rt = -1;
		if (Current.getConnection().getIsError()) {
			return rt;
		}
		begin();
		Config config = Sys.getConfig();
		String type = config.getDatabase().getType();
		if (DatabaseType.MYSQL.getCode().equals(type)) {
			long start = System.currentTimeMillis();
			try {
				rt = DaoFactory.delete4MySQL(daoParam);
			} catch (Exception e) {
				if (config.getIsDebug()) {
					e.printStackTrace();
					console(start, daoParam);
				}
				rollback();
				throw new BizException(e);
			}
			if (config.getIsDebug()) {
				console(start, daoParam);
			}
		} else {
			throw new BizException(FrameException.FRAME_DATABASE_TYPE_INVALID);
		}
		return rt;
	}

}