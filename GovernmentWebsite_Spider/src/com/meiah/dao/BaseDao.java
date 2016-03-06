package com.meiah.dao;

import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meiah.util.Config;
import com.meiah.util.ConnectionsPool;
import com.meiah.util.ConnectionsPoolLocal;

/**
 * 基本DAO操作。
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-04-22
 * 
 */
public class BaseDao {

	private Logger logger = Logger.getLogger(BaseDao.class);

	public BaseDao() {
	}

	/**
	 * 更新或保存语句执行，此类语句一定有参数
	 * 
	 * @param sqlStr
	 * @param parasValue
	 * @return 影响行数
	 * @throws SQLException
	 * @throws SQLException
	 */
	public synchronized void save(String sqlStr, Object[] parasValue)
			throws SQLException {
		logger.debug("execute sql : '" + sqlStr + "' ");
		Connection baseCon = ConnectionsPoolLocal.getInstance().getConnection();
		PreparedStatement basePsm = null;
		try {
			baseCon.setAutoCommit(false);
			basePsm = baseCon.prepareStatement(sqlStr);
			logger.debug("execute save sql : '" + sqlStr + "' ");
			for (int i = 0; i < parasValue.length; i++) {
				basePsm.setObject(i + 1, parasValue[i]);
			}
			basePsm.executeUpdate();
			baseCon.commit();
		} catch (SQLException e) {
			baseCon.rollback();
			throw e;
		} finally {
			if (basePsm != null) {
				basePsm.close();
				basePsm = null;
			}
			if (baseCon != null) {
				ConnectionsPoolLocal.getInstance().giveBackConnectioni(baseCon);
			}
		}
	}

	/**
	 * 无参数保存，使用statement
	 * 
	 * @param sqlStr
	 * @return
	 * @throws SQLException
	 */
	public void save(String sqlStr) throws SQLException {
		logger.debug("execute sql : '" + sqlStr + "' ");
		Connection baseCon = ConnectionsPoolLocal.getInstance().getConnection();
		Statement basePsm = baseCon.createStatement();
		ResultSet baseResult = null;
		try {
			baseCon.setAutoCommit(false);
			basePsm.executeUpdate(sqlStr);
			baseCon.commit();
		} catch (SQLException e) {
			baseCon.rollback();
			throw e;
		} finally {
			if (baseResult != null) {
				baseResult.close();
				baseResult = null;
			}
			if (basePsm != null) {
				basePsm.close();
				basePsm = null;
			}
			if (baseCon != null) {
				ConnectionsPoolLocal.getInstance().giveBackConnectioni(baseCon);
			}
		}
	}

	/**
	 * 调用存储过程无参数保存，使用statement
	 * 
	 * @param sqlStr
	 * @return
	 * @throws SQLException
	 */
	public void call(String sqlStr) throws SQLException {
		Connection baseCon = ConnectionsPoolLocal.getInstance().getConnection();
		CallableStatement basePsm = baseCon.prepareCall(sqlStr);
		ResultSet baseResult = null;
		try {
			basePsm.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (baseResult != null) {
				baseResult.close();
				baseResult = null;
			}
			if (basePsm != null) {
				basePsm.close();
				basePsm = null;
			}
			if (baseCon != null) {
				ConnectionsPoolLocal.getInstance().giveBackConnectioni(baseCon);
			}
		}
	}

	/**
	 * 传入一个对象数组的数组，以实现批量更新
	 * 
	 * @param sqlStr
	 * @param parasValues
	 * @throws SQLException
	 */
	public synchronized void save(String sqlStr, List<Object[]> parasValues)
			throws SQLException {
		//logger.debug("execute sql : '" + sqlStr + "' ");
		// if(baseParams.containsKey(sqlStr)){
		// baseParams.get(sqlStr).addAll(parasValues);
		// }else{
		// baseParams.put(sqlStr,parasValues);
		// }
		//		
		// // 若参数列表达到指定批处理数量才处理，否则跳出
		// if(baseParams.get(sqlStr).size()<batchSize)
		// return;
		// logger.debug("execute batch save sql : '" + sqlStr + "' with params "
		// + parasValues.size());
		Connection baseCon = ConnectionsPoolLocal.getInstance().getConnection();
		baseCon.setAutoCommit(true);
		PreparedStatement basePsm = null;
		try {
			basePsm = baseCon.prepareStatement(sqlStr);
			for (int i = 0; i < parasValues.size(); i++) {
				Object[] parasValue = parasValues.get(i);
				for (int j = 0; j < parasValue.length; j++) {
					basePsm.setObject(j + 1, parasValue[j]);
//					if(parasValue[j] != null) {
//						
//						System.out.println(parasValue[j].toString().length() + "长度 --------------------------------" + parasValue[j].toString());
//					}
				}
				basePsm.addBatch();
			}
			try {
				basePsm.executeBatch();
				// baseCon.commit();
			} catch (BatchUpdateException e) {
				throw e;
			}
			// baseParams.remove(sqlStr);
		} catch (SQLException e) {
			throw e;
		} finally {
			if (basePsm != null) {
				basePsm.close();
				basePsm = null;
			}
			if (baseCon != null) {
				ConnectionsPoolLocal.getInstance().giveBackConnectioni(baseCon);
			}
		}
	}

	/**
	 * 程序运行结束后，将批处理中未提交的内容提交<br>
	 * 由于map中的key排序不明，而thread中必须delete方法先执行，此处无法保证，所以在每页保存时直接提交了
	 * 
	 * @throws SQLException
	 */
	/*
	 * public void commitBatch() throws SQLException{ Connection baseCon =
	 * ConnectionsPoolLocal.getInstance().getConnection(); PreparedStatement
	 * basePsm = null; try { for(Iterator<String>
	 * it=baseParams.keySet().iterator();it.hasNext();){ String
	 * sqlStr=it.next();
	 * if(baseParams.get(sqlStr)==null||baseParams.get(sqlStr).size()==0)
	 * continue; basePsm=baseCon.prepareStatement(sqlStr); for(int i=0;i<baseParams.get(sqlStr).size();i++){
	 * Object[] parasValue=baseParams.get(sqlStr).get(i); for(int j=0;j<parasValue.length;j++){
	 * basePsm.setObject(j+1, parasValue[j]); } basePsm.addBatch(); }
	 * 
	 * try { basePsm.executeBatch(); }catch (BatchUpdateException e) { } } }
	 * catch (SQLException e) { throw e; } finally { if(basePsm!=null){
	 * basePsm.close(); basePsm=null; } if(baseCon!=null){
	 * ConnectionsPoolLocal.getInstance().giveBackConnectioni(baseCon); } }
	 *  }
	 * 
	 * public void savTest() throws SQLException{ logger.debug(""); }
	 */

	/**
	 * 有参数查询，尽量使用投影查询，以便确定字段位置。
	 * 
	 * @param sqlStr
	 * @param parasValue
	 * @return 获取结果数组
	 * @throws SQLException
	 */
	public synchronized List<Object[]> query(String sqlStr, Object[] parasValue)
			throws SQLException {
		// logger.debug("execute sql : '"+sqlStr+"' ");
		Connection baseCon = ConnectionsPoolLocal.getInstance().getConnection();
		PreparedStatement basePsm = null;
		ResultSet baseResult = null;
		try {
			baseCon.setAutoCommit(false);
			// this.basePsm=this.baseCon.prepareStatement(sqlStr,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);//
			// 允许结果集前后滚动
			basePsm = baseCon.prepareStatement(sqlStr);
			for (int i = 0; i < parasValue.length; i++) {
				basePsm.setObject(i + 1, parasValue[i]);
			}
			// logger.debug("execute query sql : '"+sqlStr+"' ");
			baseResult = basePsm.executeQuery();
			baseCon.commit();
			// this.baseResult.last();
			// logger.debug("get size "+this.baseResult.getRow());
			// this.baseResult.beforeFirst();
			ResultSetMetaData rmd = baseResult.getMetaData();
			List<Object[]> results = new ArrayList<Object[]>();
			for (; baseResult.next();) {
				Object[] result = new Object[rmd.getColumnCount()];
				for (int i = 0; i < rmd.getColumnCount(); i++) {
					result[i] = baseResult.getObject(i + 1);
				}
				results.add(result);
			}
			return results;
		} catch (SQLException e) {
			baseCon.rollback();
			throw e;
		} finally {
			if (baseResult != null) {
				baseResult.close();
				baseResult = null;
			}
			if (basePsm != null) {
				basePsm.close();
				basePsm = null;
			}
			if (baseCon != null) {
				ConnectionsPoolLocal.getInstance().giveBackConnectioni(baseCon);
			}
		}
	}

	/**
	 * 无参数查询，使用statement
	 * 
	 * @param sqlStr
	 * @return
	 * @throws SQLException
	 */
	public synchronized List<Object[]> query(String sqlStr) throws SQLException {
		// logger.debug("execute sql : '"+sqlStr+"' ");
		Connection baseCon = ConnectionsPoolLocal.getInstance().getConnection();
		Statement basePsm = baseCon.createStatement();
		ResultSet baseResult = null;
		try {
			baseCon.setAutoCommit(false);
			baseResult = basePsm.executeQuery(sqlStr);
			baseCon.commit();
			ResultSetMetaData rmd = baseResult.getMetaData();
			List<Object[]> results = new ArrayList<Object[]>();
			for (; baseResult.next();) {
				Object[] result = new Object[rmd.getColumnCount()];
				for (int i = 0; i < rmd.getColumnCount(); i++) {
					result[i] = baseResult.getObject(i + 1);
				}
				results.add(result);
			}
			return results;
		} catch (SQLException e) {
			baseCon.rollback();
			throw e;
		} finally {
			if (baseResult != null) {
				baseResult.close();
				baseResult = null;
			}
			if (basePsm != null) {
				basePsm.close();
				basePsm = null;
			}
			if (baseCon != null) {
				ConnectionsPoolLocal.getInstance().giveBackConnectioni(baseCon);
			}
		}
	}

	/**
	 * 特殊的无参数查询（List中首个数组保存所有的栏位名词），使用statement
	 * 
	 * @param sqlStr
	 * @return
	 * @throws SQLException
	 */
	public synchronized List<Object[]> query(String sqlStr, boolean cols)
			throws SQLException {
		// logger.debug("execute sql : '"+sqlStr+"' ");
		Connection baseCon = ConnectionsPoolLocal.getInstance().getConnection();
		Statement basePsm = baseCon.createStatement();
		ResultSet baseResult = null;
		try {
			baseCon.setAutoCommit(false);
			baseResult = basePsm.executeQuery(sqlStr);
			baseCon.commit();
			ResultSetMetaData rmd = baseResult.getMetaData();
			List<Object[]> results = new ArrayList<Object[]>();

			// 获取栏位名词
			Object[] col = new Object[rmd.getColumnCount()];
			for (int i = 0; i < rmd.getColumnCount(); i++) {
				col[i] = rmd.getColumnName(i + 1);
			}
			results.add(col);

			for (; baseResult.next();) {
				Object[] result = new Object[rmd.getColumnCount()];
				for (int i = 0; i < rmd.getColumnCount(); i++) {
					result[i] = baseResult.getObject(i + 1);
				}
				results.add(result);
			}
			return results;
		} catch (SQLException e) {
			baseCon.rollback();
			throw e;
		} finally {
			if (baseResult != null) {
				baseResult.close();
				baseResult = null;
			}
			if (basePsm != null) {
				basePsm.close();
				basePsm = null;
			}
			if (baseCon != null) {
				ConnectionsPoolLocal.getInstance().giveBackConnectioni(baseCon);
			}
		}
	}

	public void clearLogs() {
		try {
			Connection baseCon = ConnectionsPoolLocal.getInstance()
					.getConnection();
			Statement basePsm = baseCon.createStatement();
			ResultSet baseResult = null;
			try {
				baseCon.setAutoCommit(false);
				basePsm.executeUpdate(" DUMP TRANSACTION " + Config.getDbname()
						+ " WITH NO_LOG  BACKUP LOG " + Config.getDbname()
						+ " WITH NO_LOG ");
				baseCon.commit();
			} catch (SQLException e) {
				baseCon.rollback();
				throw e;
			} finally {
				if (baseResult != null) {
					baseResult.close();
					baseResult = null;
				}
				if (basePsm != null) {
					basePsm.close();
					basePsm = null;
				}
				if (baseCon != null) {
					ConnectionsPoolLocal.getInstance().giveBackConnectioni(
							baseCon);
				}
			}
		} catch (SQLException e) {
			logger.error("清除日志失败");
		}
	}
	/**
	 * 无参数保存，使用statement
	 * @param sqlStr
	 * @return
	 * @throws SQLException
	 */
	public void saveProxyUrl(String sqlStr) throws SQLException {		
		logger.debug("execute sql : '"+sqlStr+"' ");
		Connection baseCon = ConnectionsPool.getInstance().getConnection();
		Statement basePsm = baseCon.createStatement();
		ResultSet baseResult=null;
		try {
			baseCon.setAutoCommit(false);
			basePsm.executeUpdate(sqlStr);		
			baseCon.commit();
		} catch (SQLException e) {
			baseCon.rollback();
			throw e;
		} finally {
			if(baseResult!=null){
				baseResult.close();
				baseResult=null;
			}
			if(basePsm!=null){
				basePsm.close();
				basePsm=null;
			}
			if(baseCon!=null){
				ConnectionsPool.getInstance().giveBackConnectioni(baseCon);
			}		
		}
	}
}
