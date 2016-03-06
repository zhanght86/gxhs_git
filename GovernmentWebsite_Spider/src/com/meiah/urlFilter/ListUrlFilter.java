package com.meiah.urlFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.meiah.dao.BaseDao;
import com.meiah.po.TaskLink;
import com.meiah.util.Config;

public class ListUrlFilter {
	private static Logger logger = Logger.getLogger(ListUrlFilter.class);
	private static Socket nowSocket = null;
	public static AtomicInteger errorCount = new AtomicInteger();
	public static final int EXIST = 1;
	public static final int NOEXIST = 0;
	public static final int NOREADY = 2;
	public static final int SUCCESS = 1;
	public static final int FAIL = 0;
	public static boolean readerToClose = false;
	private static AtomicBoolean serverCenterNotReadery = new AtomicBoolean(
			false);

	/**
	 * @param link
	 * @return 设置在server缓存的此次任务新下载的页面链接的类别，针对链接不是从数据库提取
	 */
	public static int addLink(TaskLink link) {
		if (!serverCenterNotReadery.get()) {
			if (readerToClose) {
				return ListUrlFilter.FAIL;
			}
			String request = link.getUrl();
			String cmd = ListUrlFilter.getReturnCmd(request,
					ServerCenter._ADD_LINK);
			String res = null;
			try {
				res = ListUrlFilter.HandleSocket(cmd).replaceAll(
						"\\[[^\\]]*\\]", "").trim();
			} catch (Exception e) {
				errorCount.incrementAndGet();
				if (logger.isDebugEnabled())
					logger.debug("排重socket出现错误次数：" + errorCount.get());
			}
			if ("0".equals(res) || res == null || res.trim().length() == 0) {
				return ListUrlFilter.FAIL;
			} else if ("1".equals(res)) {
				return ListUrlFilter.SUCCESS;
			} else if ("4".equals(res)) {
				logger.error("命令格式错误:" + cmd);
				return ListUrlFilter.NOREADY;
			} else
				return ListUrlFilter.NOREADY;
		} else {
			return ListUrlFilter.FAIL;
		}

	}

	/**
	 * @param link
	 * @return 检查当前下载的url是否已经下载过
	 */
	public static int isLinkExist(TaskLink link) {
		if (!serverCenterNotReadery.get()) {
			if (readerToClose) {
				return ListUrlFilter.EXIST;
			}
			String request = link.getUrl();
			String cmd = ListUrlFilter.getReturnCmd(request,
					ServerCenter._IS__LINK_EXIST);
			String res = null;
			try {
				res = ListUrlFilter.HandleSocket(cmd).replaceAll(
						"\\[[^\\]]*\\]", "").trim();
			} catch (Exception e) {
				errorCount.incrementAndGet();
				if (logger.isDebugEnabled())
					logger.debug("排重socket出现错误次数：" + errorCount.get());
			}

			if ("0".equals(res) || res == null || res.trim().length() == 0) {
				return ListUrlFilter.NOEXIST;
			} else if ("1".equals(res)) {
				return ListUrlFilter.EXIST;
			} else if ("4".equals(res)) {
				logger.error("命令格式错误:" + cmd);
				return ListUrlFilter.NOREADY;
			} else
				return ListUrlFilter.NOREADY;
		} else {
			return ListUrlFilter.NOEXIST;
		}

	}

	/**
	 * 客户端构造请求命令
	 * 
	 * @param result
	 * @return
	 */
	private static String getReturnCmd(String request, int cmd) {
		return "[1][" + new Date().getTime() + "][" + cmd + "]" + request
				+ "\r\n";
	}

	private static Socket getSocket() {
		int errors = 0;
		while (ListUrlFilter.nowSocket == null
				|| !ListUrlFilter.nowSocket.isConnected()) {
			try {
				ListUrlFilter.nowSocket = new Socket();
				// ListUrlFilter.nowSocket = new Socket(Config.getServerIp(),
				// Config.getServerPort());
				// nowSocket.i
				ListUrlFilter.nowSocket.connect(new InetSocketAddress(
						InetAddress.getByName(Config.getLocalServerIp()),
						Config.getLocalServerPort()), 2000);
				ListUrlFilter.nowSocket.setSoTimeout(3000);
				Thread.sleep(1);
			} catch (Exception e) {
				errors++;
				logger.error("连接列表url排重中心第 " + errors + " 次出现错误："
						+ Config.getLocalServerIp() + ":"
						+ Config.getLocalServerPort() + "！请检查配置文件!  "
						+ e.getMessage());
			}
			if (errors > 4) {
				errors = 0;
				endSocket();
				logger.error("列表url排重中心【" + Config.getLocalServerIp()
						+ "】没有正常启动，此次任务将以一种效率比较低下的方式运行，请检查config.ini的配置！");
				serverCenterNotReadery.set(true);
				break;
				// System.exit(-1);

			}
		}
		return ListUrlFilter.nowSocket;
	}

	/**
	 * 任务完成后发送结束命令
	 */
	public static void endSocket() {

		try {
			if (nowSocket != null)
				nowSocket.close();

		} catch (IOException e) {
			logger.error("关闭客户端socket错误", e);
		}
	}

	/**
	 * 发送socket请求
	 * 
	 * @param request
	 * 
	 * @return
	 * @throws IOException
	 */
	private synchronized static String HandleSocket(String request)
			throws IOException {
		Socket socket = null;
		OutputStream socketOut = null;
		BufferedReader br = null;
		String msg = null;
		if (ListUrlFilter.nowSocket != null)
			socket = ListUrlFilter.nowSocket;
		else
			socket = ListUrlFilter.getSocket();
		socketOut = socket.getOutputStream();
		socketOut.write(request.getBytes());

		// 接收服务器的反馈
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		msg = br.readLine();

		return msg;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TaskLink link = new TaskLink();

		link.setUrl("http://news.qq.com/health2012/hdcp/hospital/");
		// int state = ListUrlFilter.isLinkExist(link);
		//
		// System.out.println(state);
		// if (state == ListUrlFilter.NOEXIST) {
		//
		// System.out.println("未访问 : ");
		//
		// } else {
		// System.out.println("访问过 : ");
		// }
//		System.out
//				.println(ListUrlFilter.isLinkExist(link) == ListUrlFilter.EXIST);

		// System.out.println(ListUrlFilter.isLinkExist(link));
		// ListUrlFilter.endSocket();
	}

	public static void testFilter() {
		String sqlStr = "select   taskid,page_url,islist from n_realtime ";
		BaseDao baseDao = new BaseDao();
		int t = 0, f = 0;
		try {
			logger.info("sql:" + sqlStr);
			List<Object[]> taskResults = baseDao.query(sqlStr);
			for (int i = 0; i < taskResults.size(); i++) {
				String taskid = taskResults.get(i)[0].toString();
				String page_url = taskResults.get(i)[1].toString();
				// String linkType = taskResults.get(i)[2].toString();
				// String url = taskid + page_url;
				TaskLink l = new TaskLink();
				l.setTaskid(taskid);
				l.setUrl(page_url);
				if (ListUrlFilter.isLinkExist(l) == ListUrlFilter.EXIST) {
					f++;
					logger.debug("link:" + l.getTaskid() + ":" + l.getUrl());
				}

			}
			// float ratio = (float) t / (float) taskResults.size();
			logger.info("正确判断个数：" + t + ":" + f);
		} catch (SQLException e) {
			logger.error("加载所有的新闻url缓存获取失败", e);
		}
	}
}
