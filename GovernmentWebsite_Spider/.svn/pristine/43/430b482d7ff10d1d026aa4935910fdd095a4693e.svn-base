package com.meiah.webCrawlers;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.meiah.dao.BaseDao;
import com.meiah.dao.TaskDao;
import com.meiah.util.Config;

public class ClearHisNews extends Thread {
	private static Logger logger = Logger.getLogger(ClearHisNews.class);

	private static int lastr = 0;
	private static int delnum = 20000;
	// private static int deletedNum = 0;
	// private static AtomicInteger deletedNum = new AtomicInteger(0);
	private static AtomicInteger all = new AtomicInteger(0);
	static int keepDay = 0;
	static String savePath = "";
	static {
		try {
			keepDay = Integer.parseInt(Config.getDeldays());
		} catch (Exception e) {
			logger.error("获取保存天数异常，检查配置文件", e);
		}
		savePath = TaskDao.getInstance().getSavePath();
	}

	@Override
	public void run() {
		logger.debug("当前线程 ： " + lastr + " 线程数 " + all + " 获取完成!!");
		long t1 = System.currentTimeMillis();
		try {
			deleteNews();
		} catch (Exception e) {
			e.printStackTrace();
		}
		all.decrementAndGet();

		logger.debug("当前线程 ： " + lastr + " 线程数 " + all + " 退回完成!!,耗费时间："
				+ (System.currentTimeMillis() - t1) + " ms");

	}

	public synchronized void deleteNews() throws SQLException {
		BaseDao dao = new BaseDao();
		Object[] parasValue = new Object[] { lastr - delnum - 1, lastr, 0,
				keepDay };
		// String selectSql = "select taskid,filename from n_realtime where id>?
		// and ID<? and islist=? and datediff(dd,datepublished,getdate())>?";
		// List<Object[]> results = null;
		// try {
		// results = dao.query(selectSql, parasValue);
		// for (int i = 0; i < results.size(); i++) {
		// String taskid = results.get(i)[0].toString();
		// String filename = results.get(i)[1].toString();
		// deleteSnapShotFile(savePath, taskid, filename);
		// }
		// } catch (Exception e) {
		// }
		// if (results.size() > 0) {
		// String deleteSql = "delete from n_realtime where id>? and ID<? and
		// islist=? and datediff(dd,datepublished,getdate())>?";
		// dao.save(deleteSql, parasValue);
		// if (logger.isDebugEnabled())
		// logger.debug("---》当前删除 ： " + lastr + " success!!" + "，删除条数："
		// + results.size());
		// deletedNum.addAndGet(results.size());
		//
		// }
		String deleteSql = "delete from n_realtime where id>? and ID<?  and islist=? and datediff(dd,datepublished,getdate())>?";
		String sqlStrMysql = "delete from n_realtime where id>? and ID<?  and islist=? and DATEDIFF(curdate(),DatePublished) >?";
		if (Config.getIsLocalDBMysql() == 1)
			deleteSql = sqlStrMysql;

		dao.save(deleteSql, parasValue);
		if (logger.isDebugEnabled())
			logger.debug("---》当前删除 ： " + lastr + " success!!");
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}
	}

	public static void main(String[] args) {

		long timeConsumed = System.currentTimeMillis();
		Properties props = new Properties();
		try {

			InputStream istream = SiteCrawler.class
					.getResourceAsStream("/log4j.properties");
			props.load(istream);
			istream.close();
			String logPath = ClassLoader.getSystemResource("").getPath()
			+ File.separatorChar + "log" + File.separatorChar;
			props.setProperty("log4j.appender.logfile.File", logPath
					+ "clearHis_debug.log");
			props.setProperty("log4j.appender.logfile1.File", logPath
					+ "clearHis_info.log");

			// 重新配置后，日志会打到新的文件去。
			PropertyConfigurator.configure(props);// 装入log4j配置信息
		} catch (Exception e) {
			logger.error("装入属性文件异常 Exception ", e);
		}
		logger.info("开始清除数据库历史新闻， 保存时间为：" + keepDay + " 天(0天代表不删除历史数据)");
		if (keepDay <= 0)
			return;
		int x = 1;
		try {
			x = (Integer) (new BaseDao()
					.query("select max(id) from n_realtime").get(0)[0]);
			logger.info("maxid:" + x);
		} catch (Exception e1) {
			logger.error("获取最大id出现异常", e1);
		}

		while (true) {
			if (lastr > x && all.get() == 0)
				break;

			if (lastr < x && all.get() < 10) {
				all.addAndGet(1);
				ClearHisNews t = new ClearHisNews();
				t.start();
				lastr = lastr + delnum;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		File snapShotFolder = new File(savePath);
		DeleteSnapShot delete = new DeleteSnapShot();
		try {
			delete.deleteExpiredFile(snapShotFolder, keepDay);
		} catch (ParseException e) {
			logger.error("删除快照出现异常：", e);
		}
		// logger.info("删除条数：" + deletedNum.get());
		logger.info("删除历史数据花费时间：" + (System.currentTimeMillis() - timeConsumed)
				+ " ms");
	}

	public void deleteSnapShotFile(String savePath, String taskId,
			String fileName) {

		if (savePath.endsWith("\\") || savePath.endsWith("/"))
			savePath = savePath.substring(0, savePath.length()) + "/";
		else
			savePath = savePath + "/";
		savePath = savePath + taskId + "/";
		String yyyymmdd = fileName.split("\\.")[2];
		String hhmmsss = fileName.split("\\.")[3];
		String foldPath = yyyymmdd.substring(0, 4) + "/"
				+ yyyymmdd.substring(4, 6) + "/" + yyyymmdd.substring(6, 8)
				+ "/" + hhmmsss.substring(0, 2) + "/" + hhmmsss.substring(2, 3);
		String savaFolder = savePath + foldPath;
		// File savaFolder = new File(savePath + foldPath);
		File deleteFile = new File(savaFolder + "/" + fileName);
		try {
			if (deleteFile.exists()) {
				if (!deleteFile.delete()) {
					logger.error("无法删除文件：" + deleteFile.getAbsolutePath());
				}
			} else {
				logger.error("快照文件 " + deleteFile.getAbsolutePath() + "  不存在");
			}
		} catch (Exception e) {
			logger.error("删除快照文件出现异常：" + deleteFile.getAbsolutePath(), e);

		}

	}

}
