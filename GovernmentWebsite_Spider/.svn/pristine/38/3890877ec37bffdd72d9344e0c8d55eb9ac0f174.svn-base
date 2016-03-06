package com.meiah.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.meiah.po.RunningTask;
import com.meiah.po.Task;
import com.meiah.util.Config;

public class RunningTaskDao {

	private Logger logger = Logger.getLogger(RunningTaskDao.class);
	private CenterBaseDao baseDao = new CenterBaseDao();

	private static RunningTaskDao dao;

	private RunningTaskDao() {
	}

	public static RunningTaskDao getInstance() {
		if (dao == null)
			dao = new RunningTaskDao();

		return dao;
	}

	/**
	 * 新加一个运行任务
	 * 
	 * @param rtask
	 * @return 添加成功则继续，否则当做当前任务正在运行中
	 * @throws Exception
	 */
	public void addRunningTask(RunningTask rtask) throws Exception {
		String runip = "";
		try {
			runip = Config.getLocalIp().trim();
		} catch (Exception e) {
		}
		String sqlStr = "insert n_runningtasks (taskid,runstate,runip) values (?,?,?)";
		Object[] parasValue = new Object[] { rtask.getTaskid(), 0, runip };
		if (logger.isDebugEnabled())
			logger.debug(sqlStr);
		try {
			baseDao.save(sqlStr, parasValue);
		} catch (SQLException e) {
			logger.error("新加一个运行任务异常", e);
			throw new Exception("任务重复运行");
		}

	}

	/**
	 * 更新运行任务状态
	 * 
	 * @param rtask
	 */
	public void updateRunningTask(RunningTask rtask) {
		String sqlStr = "update n_runningtasks set nowurl=?,runstate=?,DownloadPages=?,SavePages=?,threadnums=?,"
				+ "maxspeed=?,minspeed=?,avgspeed=? where taskid=?";
		Object[] parasValue = new Object[] { rtask.getNowurl(),
				rtask.getRunstate(), rtask.getDownloadPages(),
				rtask.getSavePages(), rtask.getThreadnums(),
				rtask.getMaxspeed(), rtask.getMinspeed(), rtask.getAvgspeed(),
				rtask.getTaskid() };

		try {
			baseDao.save(sqlStr, parasValue);
		} catch (SQLException e) {
			logger.error("更新运行任务状态异常， 异常提示：" + e.getMessage(), e);
		}
	}

	/**
	 * 删除运行任务
	 * 
	 * @param rtask
	 */
	public void deleteRunningTask(Task rtask) {
		String sqlStr = "delete from n_runningtasks where taskid="
				+ rtask.getTaskid();
		// Object[] parasValue = new Object[] { rtask.getTaskid() };

		try {
			baseDao.save(sqlStr);
		} catch (SQLException e) {
			logger.error("删除运行任务异常， 异常提示：" + e.getMessage(), e);
		}
	}

	/**
	 * 获取任务当前状态
	 * 
	 * @param rtask
	 * @return
	 */
	public int getNowState(RunningTask rtask) {
		String sqlStr = "select runstate from n_runningtasks where taskid=?";
		Object[] parasValue = new Object[] { rtask.getTaskid() };
		int result = 0;
		try {
			List<Object[]> results = baseDao.query(sqlStr, parasValue);
			result = Integer.parseInt(results.get(0)[0].toString());
		} catch (SQLException e) {
			logger.error("查询运行任务 " + rtask.getTaskid() + " 状态异常， 异常提示："
					+ e.getMessage(), e);
		} catch (IndexOutOfBoundsException e) {
			logger.info("运行任务已经删除， 异常提示：" + e.getMessage());
		}

		return result;
	}

	/**
	 * 清空任务运行表
	 */
	public void clear() {
		String runip = "";
		try {
			runip = Config.getLocalIp().trim();
		} catch (Exception e) {
		}
		String sqlStr = "delete from n_runningtasks where runip='" + runip
				+ "'";
		logger.debug(sqlStr);
		try {
			baseDao.save(sqlStr);
		} catch (SQLException e) {
			logger.error("清空任务运行表异常， 异常提示：" + e.getMessage(), e);
		}

	}

	/**
	 * 更新任务运行时间
	 */
	public void runTime(String taskid) {
		String sqlStr = "update n_runningtasks set starttime=getdate() where taskid="
				+ taskid;
		try {
			baseDao.save(sqlStr);
		} catch (SQLException e) {
			logger.error("更新任务运行时间异常， 异常提示：" + e.getMessage(), e);
		}
	}

	public static void main(String[] agrs) {
		RunningTaskDao.getInstance().clear();
	}

}
