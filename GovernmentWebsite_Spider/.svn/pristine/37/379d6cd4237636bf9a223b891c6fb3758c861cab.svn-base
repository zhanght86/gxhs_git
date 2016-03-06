package com.meiah.dao;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.xalan.xsltc.runtime.Hashtable;

import com.meiah.po.RunningTask;
import com.meiah.po.Task;
import com.meiah.trs.NewsTrsDo;
import com.meiah.util.Config;
import com.meiah.util.Converter;
import com.meiah.util.JavaUtil;

public class TaskDao {
	private Logger logger = Logger.getLogger(TaskDao.class);
	public static Hashtable taskMap = new Hashtable();
	/**
	 * 实例
	 */
	private static TaskDao dao = null;
	private CenterBaseDao baseDao = new CenterBaseDao();
//	private Map<String,String> taskMap ;
//
//	public Map<String, String> getTaskMap() {
//		return taskMap;
//	}
//
//	public void setTaskMap(Map<String, String> taskMap) {
//		this.taskMap = taskMap;
//	}

	public List<Task> getTaskList() {
		return this.addAutoTaskList();
	}

	public Task getTask(String taskid) {
		return this.addSingleTask(taskid);
	}

	/**
	 * 构造函数
	 */
	private TaskDao() {
//		taskMap = getAllIp();
	}

	/**
	 * 获取实例
	 * 
	 * @return TaskDao实例
	 */
	public static TaskDao getInstance() {
		if (dao == null)
			dao = new TaskDao();

		return dao;
	}

	public String getSavePath() {
		String path = "";
		String sqlStr = "select filepath from n_para ";
		try {
			List<Object[]> results = new BaseDao().query(sqlStr);
			path = results.get(0)[0].toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;

	};

	/**
	 * 获取任务的Map映射
	 * 
	 * @return
	 */
	public Map<String, Task> getAllTaskMap() {
		String sqlStr = "select  taskid,turl,tname from n_tasks ";

		Map<String, Task> TaskList = new HashMap<String, Task>();
		List<Object[]> taskResults;
		try {
			taskResults = baseDao.query(sqlStr);

			for (int i = 0; i < taskResults.size(); i++) {
				Task task = new Task();
				task.setTaskid(taskResults.get(i)[0].toString());
				task.setUrl(initUrl(taskResults.get(i)[1].toString()).trim());
				task.setTname(taskResults.get(i)[2].toString());

				TaskList.put(task.getTaskid(), task);
			}
		} catch (SQLException e) {
			logger.error("全局主进程获取自动任务列表时异常", e);
		}
		return TaskList;
	}
//	public Map getAllIp() {
//		Map<String, Task> ts = getAllTaskMap();
//		if(null != ts && ts.size() > 0) {
//			for (Entry<String, Task> entry : ts.entrySet()) {
//				String ipUrl = entry.getValue().getTurl();
////			    String ipUrl = "http://www.audit.gov.cn/n1992130/index.html";
//				String site = JavaUtil.matchWeak(ipUrl, "http://([^/]*)")[1];
//				InetAddress a = null;
//				try {
//					a = InetAddress.getByName(site);
//				} catch (UnknownHostException e) {
//					e.printStackTrace();
//				}
//				if(null != a) {
//					site = a.getHostAddress();
//				}
//				String ipName = NewsTrsDo.getAddressByIP(site);
//				taskMap.put(ipUrl, ipName);
//			}
//		}
//		System.out.println("ieeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" + taskMap.size());
//		return taskMap;
//	}
	/**
	 * 提取自动任务，符合条件，自动运行
	 */
	private List<Task> addAutoTaskList() {

		// int maxpro = Config.getMaxproc();
		// maxpro = Config.getMaxproc();
		// int nowpro = getNowProcess();
		// if (nowpro >= maxpro)// 若当前运行进程数量已经到达或大于最大数量
		// return null;
		//		
		// maxpro = maxpro - nowpro;// 若当前运行数量不足，则只能补

		// String topsql = " top " + maxpro + " ";

		// 服务器是否是境外任务专用服务器，如果是只获取代理任务，否则只获取非代理任务
		String isForeign = "";
		if (Config.getIsProxyTaskServer() == 1)
			isForeign = " and isForeign=1 ";
		else
			isForeign = " and (isForeign<>1 or isForeign is null) ";
		String sqlStr = "select top 1000  taskid from n_tasks "
				+ "where datediff(s,NextRunTime,getdate())>0 and status=1 " + isForeign
				+ " and not exists (select 1 from n_runningtasks a where a.taskid=n_tasks.taskid)  "
				+ " order by NextRunTime asc ";
		String sqlStrMysql = "select taskid from n_tasks "
				+ "where now()>nextruntime and status=1  " + isForeign+
						" and not exists (select 1 from n_runningtasks a where a.taskid=n_tasks.taskid)  "
				+ "order by NextRunTime asc limit 1000";
		if (Config.getIsTaskDBMysql() == 1)
			sqlStr = sqlStrMysql;
		if (logger.isDebugEnabled())
			logger.debug(sqlStr);
		List<Task> TaskList = new ArrayList<Task>();
		List<Object[]> taskResults;
		try {
			taskResults = baseDao.query(sqlStr);

			for (int i = 0; i < taskResults.size(); i++) {
				Task task = new RunningTask();// 选取一个任务时以运行任务的角色启动
				task.setTaskid(taskResults.get(i)[0].toString());
				TaskList.add(task);

			}
		} catch (SQLException e) {
			logger.error("全局主进程获取自动任务列表时异常", e);
		}
		return TaskList;
	}

	/**
	 * 加载单个任务
	 * 
	 * @throws SQLException
	 */
	private Task addSingleTask(String taskid) {

		String sqlStr = "select taskid,turl,tname,isnull(ttype,'none')ttype,isnull(SleepTime,0)[SleepTime],"
				+ "isnull(proxyurl,'')[proxyurl],isnull(proxyport,'')[proxyport],"
				+ "isnull(proxyuser,'')[proxyuser],isnull(ProxyPassword,'')[ProxyPassword],"
				+ "isnull(maxthread,20)[maxthread],isnull(Pages,500)[Pages],isnull(Layers,3)[Layers],"
				+ "isnull(Prefix,'')[Prefix],isnull(Substr,'')[Substr],"
				+ "isnull(BeginTitle,'')[BeginTitle],isnull(EndTitle,'')[EndTitle],"
				+ "isnull(BeginCode,'')[BeginCode],isnull(EndCode,'')[EndCode],"
				+ "isnull(Timeout,2)[Timeout],isnull(Status,1)[Status],isnull(Userid,0)[Userid],"
				+ "isnull(dateformat,'')[dateformat],isnull(datepos,1)[datepos],isnull(dateprefix,'')[dateprefix],"
				+ "isnull(pagetype,'')[pagetype],"
				+ "isnull(beginSource,'')[beginSource],isnull(endSource,'')[endSource],"
				+ "isnull(beginAuthor,'')[beginAuthor],isnull(endAuthor,'')[endAuthor],"
				+ "isnull(beginColumnName,'')[beginColumnName],isnull(endColumnName,'')[endColumnName],"
				+ "isnull(beginDocumentName,'')[beginDocumentName],isnull(endDocumentName,'')[endDocumentName],"
				+ "isnull(beginImgUrl,'')[beginImgUrl],isnull(endImgUrl,'')[endImgUrl],"
				+ "newsType,"
				+ "isnull(subBoardUrl,'')[subBoardUrl] " + " from n_tasks where taskid=" + taskid;
		String sqlStrMysql = "select taskid,turl,tname,isnull(ttype,'none')ttype,isnull(SleepTime,0)[SleepTime],"
				+ "isnull(proxyurl,'')[proxyurl],isnull(proxyport,'')[proxyport],"
				+ "isnull(proxyuser,'')[proxyuser],isnull(ProxyPassword,'')[ProxyPassword],"
				+ "isnull(maxthread,20)[maxthread],isnull(Pages,500)[Pages],isnull(Layers,3)[Layers],"
				+ "isnull(Prefix,'')[Prefix],isnull(Substr,'')[Substr],"
				+ "isnull(BeginTitle,'')[BeginTitle],isnull(EndTitle,'')[EndTitle],"
				+ "isnull(BeginCode,'')[BeginCode],isnull(EndCode,'')[EndCode],"
				+ "isnull(Timeout,2)[Timeout],isnull(Status,1)[Status],isnull(Userid,0)[Userid],"
				+ "isnull(dateformat,'')[dateformat],isnull(datepos,1)[datepos],isnull(dateprefix,'')[dateprefix],"
				+ "isnull(pagetype,'')[pagetype],"
				+ "isnull(beginSource,'')[beginSource],isnull(endSource,'')[endSource],"
				+ "isnull(beginAuthor,'')[beginAuthor],isnull(endAuthor,'')[endAuthor],"
				+ "isnull(beginColumnName,'')[beginColumnName],isnull(endColumnName,'')[endColumnName],"
				+ "isnull(beginDocumentName,'')[beginDocumentName],isnull(endDocumentName,'')[endDocumentName],"
				+ "isnull(beginImgUrl,'')[beginImgUrl],isnull(endImgUrl,'')[endImgUrl],"
				+ "newsType,"
				+ "isnull(subBoardUrl,'')[subBoardUrl] " + " from n_tasks where taskid=" + taskid;
		if (Config.getIsTaskDBMysql() == 1)
			sqlStr = sqlStrMysql;
		List<Object[]> taskResults;
		// logger.info(sqlStr);
		Task task = new RunningTask();// 选取一个任务时以运行任务的角色启动
		try {
			taskResults = baseDao.query(sqlStr);
			// logger.info("query end");
			task.setTaskid(taskResults.get(0)[0].toString());
			task.setUrl(initUrl(taskResults.get(0)[1].toString()));
			task.setTname(taskResults.get(0)[2].toString());
			task.setType(taskResults.get(0)[3] == null ? null : taskResults.get(0)[3].toString());
			task.setSleeptime(Integer.valueOf(taskResults.get(0)[4].toString()));
			task.setProxyurl(taskResults.get(0)[5].toString());
			task.setProxyport(taskResults.get(0)[6].toString());
			task.setProxyuser(taskResults.get(0)[7].toString());
			task.setProxypwd(taskResults.get(0)[8].toString());
			task.setMaxthread(Integer.valueOf(taskResults.get(0)[9].toString()));
			task.setPages(Integer.valueOf(taskResults.get(0)[10].toString()));
			task.setLayers(Integer.valueOf(taskResults.get(0)[11].toString()));
			if (task.getLayers() == 0)
				task.setLayers(100);
			task.setPrefix(taskResults.get(0)[12].toString());
			task.setSubstr(taskResults.get(0)[13].toString());
			task.setBeginTitle(taskResults.get(0)[14].toString());
			task.setEndTitle(taskResults.get(0)[15].toString());
			task.setBeginCode(taskResults.get(0)[16].toString());
			task.setEndCode(taskResults.get(0)[17].toString());
			task.setTimeout(Integer.valueOf(taskResults.get(0)[18].toString()));
			task.setStatus(Integer.valueOf(taskResults.get(0)[19].toString()));
			task.setUserid(Integer.valueOf(taskResults.get(0)[20].toString()));
			task.setDateFormat(taskResults.get(0)[21].toString());
			task.setDatePos(Integer.valueOf(taskResults.get(0)[22].toString()));
			task.setDatePrefix(taskResults.get(0)[23].toString());
			task.setPageType(taskResults.get(0)[24].toString());
			task.setBeginSource(taskResults.get(0)[25].toString());
			task.setEndSource(taskResults.get(0)[26].toString());
			task.setBeginAuthor(taskResults.get(0)[27].toString());
			task.setEndAuthor(taskResults.get(0)[28].toString());
			task.setSubBoardUrl(taskResults.get(0)[36].toString());
			task.setBeginColumnName(taskResults.get(0)[29].toString());
			task.setEndColumnName(taskResults.get(0)[30].toString());
			task.setBeginDocumentName(taskResults.get(0)[31].toString());
			task.setEndDocumentName(taskResults.get(0)[32].toString());
			task.setBeginImgUrl(taskResults.get(0)[33].toString());
			task.setEndImgUrl(taskResults.get(0)[34].toString());
			task.setNewsType(taskResults.get(0)[35].toString());
		} catch (SQLException e) {
			logger.error("加载单个任务时异常", e);
		}
		// ;logger.info("task get sucess");
		return task;
	}

	/**
	 * 更新任务下次启动时间
	 * 
	 * @param task
	 */
	public void updateNextStarttime(Task task) {
		String sqlStr = "update n_tasks set NextRunTime=dateadd(s,sleeptime,getdate()) where taskid=?";
		String sqlMysql = "update n_tasks set NextRunTime=DATE_ADD(now(),INTERVAL sleeptime SECOND) where taskid=?";
		if (Config.getIsTaskDBMysql() == 1)
			sqlStr = sqlMysql;
		Object[] parasValue = new Object[] { task.getTaskid() };

		try {
			baseDao.save(sqlStr, parasValue);
		} catch (SQLException e) {
			logger.error("更新任务下次启动时间异常， 异常提示：" + e.getMessage(), e);
		}
	}

	// 规范化URL，前加HTTP 后加/
	private String initUrl(String url) {
		if (!url.startsWith("http")) {
			url = "http://" + url;
		}

		if (url.substring(8).indexOf("/") == -1 || (url.indexOf(".") == -1 && !url.endsWith("/"))) {
			url += "/";
		}

		return url;
	}

	// /**
	// * 获取设置中的最大进程数量
	// *
	// * @return
	// */
	// private int getMaxProcess() {
	// String sqlStr = "select top 1 abs(isnull(processnum,"
	// + Config.getMaxproc() + ")) from n_para";
	//
	// try {
	// return Integer.valueOf(baseDao.query(sqlStr).get(0)[0].toString());
	// } catch (Exception e) {
	// // logger.error("获取设置中的最大进程数量异常， 异常提示："+e.getMessage()+", 返回默认值5");
	// }
	// return 20;
	// }

	/**
	 * 获取当前运行的进程数量
	 * 
	 * @return
	 */
	public int getNowProcess() {
		String runip = "";
		try {
			runip = Config.getLocalIp().trim();
		} catch (Exception e) {
		}
		String sqlStr = "select count(1) from n_runningtasks where runip='" + runip + "'";

		try {
			return Integer.valueOf(baseDao.query(sqlStr).get(0)[0].toString());
		} catch (Exception e) {
			logger.error("获取设置中的最大进程数量异常， 异常提示：" + e.getMessage() + ",  返回默认值5");
		}
		return 0;
	}

	/**
	 * 重置所有任务的插件类型
	 */
	public void resetTtype() {
		String sqlStr = "update n_tasks set ttype=null where autorun=1";
		try {
			baseDao.save(sqlStr);
		} catch (SQLException e) {
			logger.error("重置所有任务的插件类型出错", e);
		}
	}

	/**
	 * 更新任务解析插件类型
	 * 
	 * @param task
	 */
	public void setTtype(Task task) {
		String sqlStr = null;
		if (task.getType() != null)
			sqlStr = "update n_tasks set ttype='" + task.getType() + "' where taskid='" + task.getTaskid() + "'";
		else
			sqlStr = "update n_tasks set ttype=null where taskid='" + task.getTaskid() + "'";
		try {
			baseDao.save(sqlStr);
		} catch (SQLException e) {
			logger.error("更新任务类型出错", e);
		}
	}

	public String getHomePageUrl(String taskid) {
		String url = null;
		String sqlStr = "select HomePageUrl from n_tasks where taskid=" + taskid;
		try {
			List<Object[]> result = baseDao.query(sqlStr);
			if (result.size() > 0)
				url = result.get(0)[0].toString();
		} catch (Exception e) {
		}
		return url;
	}

	public static void main(String[] args) {
//		getAllIp();
		List<Task> tasklist = TaskDao.getInstance().addAutoTaskList();
		for (Task a : tasklist) {
//			System.out.println(a.getTaskid() + ":" + a.getUrl());
		}
		
		// Task task = TaskDao.getInstance().addSingleTask("3783967046");
		// System.out.println(task.getTname() + ": " + task.getTurl());
		// Task a = t.getTask("2");
		// long time = System.currentTimeMillis();
		// HashMap<Long, String> set = TaskDao.getInstance().getConformMap(
		// "4242927329");
		// System.out.println("花费时间：" + (System.currentTimeMillis() - time)
		// + " ms");
		// System.out.println("集合大小：" + set.size() + ":" +
		// set.get(3549863491L));
		// System.out.println(a);
		// int maxpro = Config.getMaxproc();
		// System.out.println(maxpro);
	}
}
