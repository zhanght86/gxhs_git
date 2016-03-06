package com.meiah.webCrawlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.meiah.dao.BaseDao;
import com.meiah.dao.CenterBaseDao;
import com.meiah.dao.RunningTaskDao;
import com.meiah.dao.TaskDao;
import com.meiah.po.RunningTask;
import com.meiah.po.Task;
import com.meiah.util.Config;

public class TaskStartAuto extends Thread {
	private static Logger logger = Logger.getLogger(TaskStartAuto.class);

	private int flushTime = 5000;// 更新时间 ms
	public static final String COUNT_SUFFIX = "countTimer";
	public static final String SERVER_SUFFIX = "serverCenter";
	private String MAX_MEM = "-Xmx256m";

	private String Pre;
	private String sep = ";";
	public static String classPath = File.separatorChar + "lib" + File.separatorChar;

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
	private void startProcesses() {
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
//			if (!new File("~runing_ServerCente.now").exists()
//					|| new File("~runing_ServerCente.now").delete()) {
//				try {
//					String taskServerCenter = Pre.replaceAll("Xmx256m",
//							"Xmx1024m")
//							+ " com.meiah.urlFilter.ServerCenter";
//					Runtime.getRuntime().exec(taskServerCenter);
//					logger.info(" 启动 列表url排重 进程成功 " + taskServerCenter);
//				} catch (IOException e1) {
//					logger.error("启动 列表url排重 进程失败 ", e1);
//				}
//			}
			// 统计进程
			if (!new File("~runing_Count.now").exists() || new File("~runing_Count.now").delete()) {
				try {
					String taskCounter = Pre + " com.meiah.webCrawlers.CountTimer";
					Runtime.getRuntime().exec(taskCounter);
					logger.info(" 启动 统计 进程成功 ");
					// logger.info(" 任务命令： " + taskCounter);
				} catch (Exception e) {
					logger.error("启动 统计 进程失败 ", e);
				}
			}

		} else {
//			if (!checkProcessExist(SERVER_SUFFIX)) {
//				try {
//					String taskServerCenter = Pre.replaceAll("Xmx256m",
//							"Xmx1024m")
//							+ " com.meiah.urlFilter.ServerCenter";
//					Runtime.getRuntime().exec(taskServerCenter);
//					logger.info(" 启动 列表url排重 进程成功 " + taskServerCenter);
//				} catch (IOException e1) {
//					logger.error("启动 列表url排重 进程失败 ", e1);
//				}
//			}
			// 统计进程
			if (!checkProcessExist(COUNT_SUFFIX)) {
				try {
					String taskCounter = Pre + " com.meiah.webCrawlers.CountTimer";
					Runtime.getRuntime().exec(taskCounter);
					logger.info(" 启动 统计 进程成功 ");
					// logger.info(" 任务命令： " + taskCounter);
				} catch (Exception e) {
					logger.error("启动 统计 进程失败 ", e);
				}
			}
		}
	}

	private void mainPro() {
		logger.info("NEWSICS开始运行");
		cleanTemps();

		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			String processName = ClassLoader.getSystemResource("newsics.exe").getPath();
			Pre = processName + " -classpath " + classPath + " " + MAX_MEM;
			//  /D:/workspace_ec/NewsicsDis_VHttpclient/bin/newsics.exe -classpath /bin/ -Xmx256m
		} else {
			Pre = "java " + " -classpath " + classPath + " " + MAX_MEM;
		}
		// logger.info("启动进程前缀 " + Pre);

		while (true) {
			// 结束程序
			if (exit) {
				break;
			}
			startProcesses() ;
			// 获取任务列表，将任务列表中不存在任务开启线程，并加入任务线程列表
			List<Task> tasks = TaskDao.getInstance().getTaskList();
			nowProc = TaskDao.getInstance().getNowProcess();
			if (tasks != null) {

				for (Iterator<Task> it = tasks.iterator(); it.hasNext();) {
					if (nowProc < maxProc) {
						Task nowTask = it.next();
						// 此处单个任务以进程形式启动，只传任务id，到单个任务运行再提取完整信息
						try {

							String taskCrawler = Pre + " com.meiah.webCrawlers.SiteCrawler " + nowTask.getTaskid();
							RunningTaskDao.getInstance().addRunningTask((RunningTask) nowTask);
//							System.out.println(taskCrawler);
							Runtime.getRuntime().exec(taskCrawler);
							nowProc++;
							logger.info(" 启动任务 " + nowTask.getTaskid() + " 进程成功 ");

						} catch (Exception e) {
							if (e.getMessage().indexOf("IX_n_RunningTasks") != -1)
								logger.error("启动任务 " + nowTask.getTaskid() + " 进程失败 ", e);
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
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			sep = ";";
		} else {
			sep = ":";
		}
		String t_classPath = classPath;
		String getClassRootPath = ClassLoader.getSystemResource("lib")
				.getPath();

		classPath = getClassRootPath;

		File dirOfJar = new File(classPath);

		if (dirOfJar.isDirectory()) {
			String[] filenames = dirOfJar.list();
			classPath = "." + sep;
			for (String filename : filenames) {
				if (filename.endsWith(".jar"))
					classPath += "." + t_classPath + filename + sep;
			}
			if (logger.isDebugEnabled())
				logger.debug("classPath=" + classPath);
		}
	}

	private void clearListURL() {
		try {
			new BaseDao().save("truncate table n_listUrls");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("清理列表页出现异常！", e);
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

		String[] sqls = new String[] {};

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
		String sql = "if  not exists (select crawlerip from n_crawlerInfo where crawlerip='"
				+ runip
				+ "') insert into n_crawlerInfo (crawlerName,crawlerIp,addtime,state) values ('"
				+ runip + "','" + runip + "',getdate(),1)";
		String sqlStrMysql = "insert into n_crawlerInfo (crawlerName,crawlerIp,addtime,state) values ('"
				+ runip + "','" + runip + "',now(),1)";

		if (Config.getIsTaskDBMysql() == 1)
			sql = sqlStrMysql;
		logger.info(sql);

		try {
			if (!checkExist("select crawlerip from n_crawlerInfo where crawlerip='"
					+ runip + "'"))
				cdao.save(sql);
		} catch (SQLException e) {
			logger.error("更新分布式爬虫信息出现异常！系统退出" + e.getMessage());
			System.exit(-1);
		}
	}

	private boolean checkExist(String sql) {
		boolean ret = false;
		String sqlStr = sql;

		List<Object[]> taskResults;
		try {
			taskResults = new CenterBaseDao().query(sqlStr);
			if (taskResults != null && taskResults.size() > 0)
				ret = true;
		} catch (SQLException e) {
			logger.error("执行sql 出现异常", e);
		}

		return ret;

	}

	private void registerCrawlerInfo() {
		CenterBaseDao cdao = new CenterBaseDao();
		String runip = "";
		try {
			runip = Config.getLocalIp().trim();
			String url = Config.getLocalurl();
			if (url.indexOf("mysql") != -1) {
				// jdbc:mysql://192.168.74.1:3306/newsicsdis?characterEncoding=utf-8
				// 192.168.74.1:1433

			}

			int port = 3306;
			try {
				port = Integer.parseInt(match(url,
						"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:(\\d+)")[1]);
			} catch (Exception e) {
				logger.error(Config.getLocalurl() + "不是用IP?");
			}
			String sql = "if  not exists (select spip from sp_conn where spip='"
					+ runip
					+ "' and sptype='news'"
					+ ") insert into sp_conn (spip,sptype,spdbname,spdbuser,spdbpwd,port,driver) values ('"
					+ runip
					+ "','news','"
					+ Config.getDbname()
					+ "','"
					+ Config.getLocaluser()
					+ "','"
					+ Config.getLocalpassword()
					+ "'," + port + ",'" + Config.getLocaldriver() + "')";
			String sqlStrMysql = "insert into sp_conn (spip,sptype,spdbname,spdbuser,spdbpwd,port,driver) values ('"
					+ runip
					+ "','news','"
					+ Config.getDbname()
					+ "','"
					+ Config.getLocaluser()
					+ "','"
					+ Config.getLocalpassword()
					+ "'," + port + ",'" + Config.getLocaldriver() + "')";
			if (Config.getIsTaskDBMysql() == 1)
				sql = sqlStrMysql;
			logger.info(sql);
			if (!checkExist("select spip from sp_conn where spip='" + runip
					+ "' and sptype='news'"))
				cdao.save(sql);
		} catch (Exception e) {
			logger.warn("注册登记分布式爬虫信息出现异常：", e);
			System.exit(-1);
		}
	}

	/**
	 * @param suffix
	 *            进程文件名后缀
	 * @return
	 */
	public boolean checkProcessExist(final String suffix) {
		boolean ret = false;
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File ff) {
				if (ff.getName().endsWith(suffix)) {
					return true;
				}
				return false;
			}
		};
		// logger.info("root:"+ClassLoader.getSystemResource("").getPath());
		File[] ffL = new File(ClassLoader.getSystemResource("").getPath())
				.listFiles(fileFilter);
		// logger.info("ffL.length:"+ffL.length);
		int matchCount = 0;
		if (ffL.length > 0) {
			for (int i = 0; i < ffL.length; i++) {
				File pidFile = ffL[i];
				try {
					String pid = match(pidFile.getName(), "\\d+")[0];
					if (checkPidExist(pid)) {
						matchCount++;
						ret = true;
					} else {
						logger.info(pid + " 进程不存在");
						if (!pidFile.delete())
							logger.error("无法删除文件！" + pidFile.getName());
					}
				} catch (Exception e) {
					logger.error("error pid file:" + pidFile.getAbsolutePath());
				}
			}
		} else {
			logger.info("不存在[" + suffix + "]文件！");
		}
		if (matchCount > 1)
			logger.error("more than a process exist!!");

		return ret;
	}

	/**
	 * 正则匹配
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static String[] match(String s, String pattern) {
		Matcher m = Pattern.compile(pattern).matcher(s);

		while (m.find()) {
			int n = m.groupCount();
			String[] ss = new String[n + 1];
			for (int i = 0; i <= n; i++) {
				ss[i] = m.group(i);
			}
			return ss;
		}
		return null;
	}

	public static boolean checkPidExist(String pid) {
		boolean ret = false;
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			StringBuilder sb = new StringBuilder();
			String line;
			int trytime = 3;
			for (int i = 0; i < trytime; i++) {
				int lineNum = 0;
				BufferedReader input = null;
				Process proc = null;
				try {
					String cmd_checkPidExist = "tasklist /fi \"pid eq " + pid
							+ "\"";
					proc = Runtime.getRuntime().exec(cmd_checkPidExist);
					input = new BufferedReader(new InputStreamReader(proc
							.getInputStream()));
					while ((line = input.readLine()) != null) {
						sb.append(line);
						lineNum++;
					}
					if (lineNum > 1) {
						ret = true;
						break;
					} else {
						if (i == trytime - 1) {
							logger.error("pid:" + pid + " not exits,return:"
									+ sb.toString() + " cmd:"
									+ cmd_checkPidExist + ",exitvalue:"
									+ proc.exitValue());
						}
					}
					sb.delete(0, sb.length());
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					if (proc != null)
						proc.destroy();
					if (input != null)
						try {
							input.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		} else {
			String line;
			int lineNum = 0;
			try {
				Process proc = Runtime.getRuntime().exec("ps -p " + pid);
				BufferedReader input = new BufferedReader(
						new InputStreamReader(proc.getInputStream()));
				//				
				while ((line = input.readLine()) != null) {
					lineNum++;
				}

				input.close();
				if (lineNum > 1)
					ret = true;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

		}
		return ret;
	}

	public static void main(String[] args) {
		// String licenseFilePath = "D:\\pico\\FI-2100\\License.dat";
		// File licenseFile = new File(licenseFilePath);
		// if (!licenseFile.exists()) {
		// logger.error("找不到授权文件：" + licenseFilePath + ",系统无法启动！");
		// System.exit(-1);
		// }
		// int result = CheckAuthorization.CheckLicense(licenseFilePath);
		// if (result == CheckAuthorization.SUCCESS) {
		// logger.info("返回机器码:" + result + ",服务可以正常启动!");
		TaskStartAuto t = new TaskStartAuto();
		t.registerCrawlerInfo();
		t.checkInCrawlerInfo();
		t.clearListURL();
		t.initLib();
		t.AlterDB();
		t.AlterCenterDB();
		t.start();
		// } else {
		// logger.error("返回的机器码:" + result + "服务启动失败!");
		// }

	}
}
