package com.meiah.webCrawlers;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.meiah.dao.BaseDao;
import com.meiah.dao.CenterBaseDao;
import com.meiah.dao.RunningTaskDao;
import com.meiah.dao.TaskDao;
import com.meiah.po.RunningTask;
import com.meiah.po.Task;
import com.meiah.util.Config;
import com.meiya.uas.client.CheckAuthorization;

public class CopyOfTaskStartAuto extends Thread {
	private static Logger logger = Logger.getLogger(CopyOfTaskStartAuto.class);

	private int flushTime = 30000;// 更新时间 ms

	private String MAX_MEM = "-Xmx256m";
	public static final String processName = "newsics";
	private String Pre;
	public static String classPath = "\\lib\\";

	private int maxProc = Config.getMaxproc();
	public static int nowProc;

	private boolean exit = false;// 

	/**
	 * 线程入口，留空，可能修改时需要在主体方法前后增加操作
	 */
	@Override
	public void run() {
		this.mainPro();
	}

	/**
	 * 构造单个任务线程，控制任务执行状态。使用队列，每隔3分钟检查任务状态。 获取新的任务列表，检查任务线程状态。移除完成线程，根据新列表产生任务。
	 * 已经存在的任务线程不新启动。
	 */
	private void mainPro() {
		logger.info("NEWSICS开始运行");
		cleanTemps();

		Runtime taskRuntime = Runtime.getRuntime();
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			Pre = processName + " -classpath " + classPath + " " + MAX_MEM;
		} else {
			Pre = "javalog " + processName + " -classpath " + classPath + " "
					+ MAX_MEM;
		}
		// logger.info("启动进程前缀 " + Pre);

		while (true) {
			// 结束程序
			if (exit) {
				break;
			}
			if (!new File("~runing_ServerCente.now").exists()
					|| new File("~runing_ServerCente.now").delete()) {
				try {
					String taskServerCenter = Pre.replaceAll("Xmx256m",
							"Xmx1024m")
							+ " com.meiah.urlFilter.ServerCenter";
					taskRuntime.exec(taskServerCenter);
					logger.info(" 启动 列表url排重 进程成功 " + taskServerCenter);
				} catch (IOException e1) {
					logger.error("启动 列表url排重 进程失败 ", e1);
				}
			}
			// 统计进程
			if (!new File("~runing_Count.now").exists()
					|| new File("~runing_Count.now").delete()) {
				try {
					String taskCounter = Pre
							+ " com.meiah.webCrawlers.CountTimer";
					taskRuntime.exec(taskCounter);
					logger.info(" 启动 统计 进程成功 ");
					// logger.info(" 任务命令： " + taskCounter);
				} catch (Exception e) {
					logger.error("启动 统计 进程失败 ", e);
				}
			}

			// 获取任务列表，将任务列表中不存在任务开启线程，并加入任务线程列表
			List<Task> tasks = TaskDao.getInstance().getTaskList();
			nowProc = TaskDao.getInstance().getNowProcess();
			if (tasks != null) {

				for (Iterator<Task> it = tasks.iterator(); it.hasNext();) {
					if (nowProc < maxProc) {
						Task nowTask = it.next();
						// 此处单个任务以进程形式启动，只传任务id，到单个任务运行再提取完整信息
						try {

							String taskCrawler = Pre
									+ " com.meiah.webCrawlers.SiteCrawler "
									+ nowTask.getTaskid();
							RunningTaskDao.getInstance().addRunningTask(
									(RunningTask) nowTask);
							taskRuntime.exec(taskCrawler);
							nowProc++;
							logger.info(" 启动任务 " + nowTask.getTaskid()
									+ " 进程成功 ");

						} catch (Exception e) {
							if (e.getMessage().indexOf("IX_n_RunningTasks") != -1)
								logger.error("启动任务 " + nowTask.getTaskid()
										+ " 进程失败 ", e);
							else {
								logger.info("任务已经在运行：" + nowTask.getTaskid());
							}
						}
					} else {
						break;
					}
				}
			}

			try {
				Thread.sleep(flushTime);
			} catch (InterruptedException e) {
				logger.info(e.getMessage());
			}
		}

		logger.info("NEWSICS结束运行");
	}

	/**
	 * 清理上次结束时留下的无用文件
	 */
	private void cleanTemps() {
		RunningTaskDao.getInstance().clear();
		if (logger.isDebugEnabled())
			logger.debug("清理运行任务表结束");
	}

	private void initLib() {

		String t_classPath = classPath;
		String getClassRootPath = ClassLoader.getSystemResource("").getPath();

		classPath = getClassRootPath + classPath;

		File dirOfJar = new File(classPath);

		if (dirOfJar.isDirectory()) {
			String[] filenames = dirOfJar.list();
			classPath = ".;";
			for (String filename : filenames) {
				if (filename.endsWith(".jar"))
					classPath += "." + t_classPath + filename + ";";
			}
			if (logger.isDebugEnabled())
				logger.debug("classPath=" + classPath);
		}
	}

	private void AlterDB() {
		BaseDao dao = new BaseDao();

		String[] sqls = new String[] {
				"alter table n_tasks add  homePageUrl varchar(500)",
				"alter table n_realtime add  isHomePageUrl tinyint ",
				"alter table n_realtime add  author varchar(100) ",
				"alter table n_realtime add  sourceSite varchar(100) ",
				"alter table n_tasks add  beginSource varchar(100) ",
				"alter table n_tasks add  endSource varchar(100) ",
				"alter table n_tasks add  beginAuthor varchar(100) ",
				"alter table n_tasks add  endAuthor varchar(100) ",
				"alter table n_realtime add  pageUrlMD5 varchar(50) " };

		for (int i = 0; i < sqls.length; i++) {
			String sql = sqls[i];
			try {
				dao.save(sql);
			} catch (SQLException e) {
				logger.warn("更新数据库出现异常：" + e.getMessage());
			}
		}
	}

	private void AlterCenterDB() {
		CenterBaseDao dao = new CenterBaseDao();

		String[] sqls = new String[] {
				"IF NOT EXISTS (SELECT 1 FROM dbo.syscolumns WHERE [name]= 'subBoardUrl ' AND  [id]=OBJECT_ID( 'n_tasks ')) alter table n_tasks add  subBoardUrl varchar(5000)",
				"IF NOT EXISTS (SELECT 1 FROM dbo.syscolumns WHERE [name]= 'isForeign ' AND  [id]=OBJECT_ID( 'n_tasks ')) alter table n_tasks add  isForeign int" };

		for (int i = 0; i < sqls.length; i++) {
			String sql = sqls[i];
			try {
				dao.save(sql);
			} catch (SQLException e) {
				logger.warn("更新任务中心数据库出现异常：" + e.getMessage());
			}
		}
	}

	private void checkInCrawlerInfo() {
		CenterBaseDao cdao = new CenterBaseDao();
		String runip = Config.getLocalIp();
		if (runip == null || runip.equals("")) {
			logger.error("更新分布式爬虫信息出现异常!系统退出");
			System.exit(-1);
		}
		String sql = new String(
				"if  not exists (select crawlerip from n_crawlerInfo where crawlerip='"
						+ runip
						+ "') insert into n_crawlerInfo (crawlerName,crawlerIp,addtime,state) values ('"
						+ runip + "','" + runip + "',getdate(),1)");
		try {
			cdao.save(sql);
		} catch (SQLException e) {
			logger.error("更新分布式爬虫信息出现异常！系统退出" + e.getMessage());
			System.exit(-1);
		}
	}

	private void registerCrawlerInfo() {
		CenterBaseDao cdao = new CenterBaseDao();
		String runip = "";
		try {
			runip = Config.getLocalIp().trim();

			String sql = "if  not exists (select spip from sp_conn where spip='"
					+ runip
					+ "' and sptype='news'"
					+ ") insert into sp_conn (spip,sptype,spdbname,spdbuser,spdbpwd) values ('"
					+ runip
					+ "','news','"
					+ Config.getDbname()
					+ "','"
					+ Config.getLocaluser()
					+ "','"
					+ Config.getLocalpassword()
					+ "')";
			logger.info(sql);
			cdao.save(sql);
		} catch (Exception e) {
			logger.warn("注册登记分布式爬虫信息出现异常：", e);
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
//		String licenseFilePath = "E:\\pico\\FI-2100\\License.dat";
//		File licenseFile = new File(licenseFilePath);
//		if (!licenseFile.exists()) {
//			logger.error("找不到授权文件：" + licenseFilePath + ",系统无法启动！");
//			System.exit(-1);
//		}
//		int result = CheckAuthorization.CheckLicense(licenseFilePath);
//		if (result == CheckAuthorization.SUCCESS) {
			//logger.info("返回机器码:" + result + ",服务可以正常启动!");
			CopyOfTaskStartAuto t = new CopyOfTaskStartAuto();
			t.registerCrawlerInfo();
			t.checkInCrawlerInfo();
			t.initLib();
			t.AlterDB();
			t.AlterCenterDB();
			t.start();
		/*
		 * } else { logger.error("返回的机器码:" + result + "服务启动失败!"); }
		 */

	}
}
