package com.meiah.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meiah.po.Alarm;

/**
 * 报警DAO操作，加载报警列表
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-08-10
 * 
 */
public class AlarmDao {

	private Logger logger = Logger.getLogger(AlarmDao.class);
	private BaseDao baseDao = new BaseDao();

	private static AlarmDao dao;

	private AlarmDao() {
	}

	public static AlarmDao getInstance() {
		if (dao == null)
			dao = new AlarmDao();

		return dao;
	}

	/**
	 * 从数据库中加载内容报警信息，内容报警类型为0
	 * 
	 * @param taskid
	 * @return
	 */
	public List<Alarm> getContentAlarms(String taskid) {
		String sqlStr = "select id,alarmname,alarmexp from n_alarm "
				+ "where alarmtype=0 and status=1 ";
		List<Alarm> AlarmList = new ArrayList<Alarm>();
		List<Object[]> alarmResults;
		try {
			alarmResults = baseDao.query(sqlStr);

			for (int i = 0; i < alarmResults.size(); i++) {
				Alarm alarm = new Alarm();
				alarm.setAlermid(Integer.valueOf(alarmResults.get(i)[0]
						.toString()));
				alarm.setAlermname(alarmResults.get(i)[1].toString());
				alarm.setAlermexp(alarmResults.get(i)[2].toString());
				// alarm.setAlermbelong(alarmResults.get(i)[3].toString());
				AlarmList.add(alarm);
			}
		} catch (SQLException e) {
			logger.error("内容报警策略获取列表时异常", e);
		}
		return AlarmList;
	}

	/**
	 * 从数据库中加载用户报警信息，用户报警类型为1
	 * 
	 * @param taskid
	 * @return
	 */
	public List<Alarm> getUserAlarms(String taskid) {
		String sqlStr = "select id,alarmname,alarmexp from n_alarm "
				+ "where status=1 ";
		List<Alarm> AlarmList = new ArrayList<Alarm>();
		List<Object[]> alarmResults;
		try {
			alarmResults = baseDao.query(sqlStr);
			for (int i = 0; i < alarmResults.size(); i++) {
				Alarm alarm = new Alarm();
				alarm.setAlermid(Integer.valueOf(alarmResults.get(i)[0]
						.toString()));
				alarm.setAlermname(alarmResults.get(i)[1].toString());
				alarm.setAlermexp(alarmResults.get(i)[2].toString());
				// alarm.setAlermbelong(alarmResults.get(i)[3].toString());
				AlarmList.add(alarm);
			}
		} catch (SQLException e) {
			logger.error("用户报警策略获取列表时异常", e);
		}
		return AlarmList;
	}
}
