package com.meiah.webCrawlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.meiah.util.Config;
import com.meiah.util.MD5Utils;

/**
 * @author huhb
 * @time Dec 13, 2011
 * @for 新闻爬虫的排重请求客户端，链接排重中心（mongoDB查询代理）排重
 */
public class ClientCenter {
	private static Logger logger = Logger.getLogger(ClientCenter.class);
	private static Socket nowSocket = null;
	public static AtomicInteger errorCount = new AtomicInteger();
	public static final boolean EXIST = true;
	public static final boolean NOEXIST = false;
	public static final int NOREADY = 2;
	public static final int SUCCESS = 1;
	public static final int FAIL = 0;
	public final static int _IS_CONTENT_LINK_EXIST = 1;// 排重检查是否正文页
	private static AtomicBoolean serverCenterNotReadery = new AtomicBoolean(
			false);

	/**
	 * 检查下载中的url是否是已经下载过的正文页</b>
	 * 
	 * @param urlMd5
	 *            :新闻网页的URL
	 * @return true：存在，fasle不存在
	 */
	public static boolean isMd5Exist(String urlMd5) {
		String request = urlMd5;
		String cmd = ClientCenter.getReturnCmd(request, _IS_CONTENT_LINK_EXIST);
		String res = null;
		try {
			res = ClientCenter.HandleSocket(cmd)
					.replaceAll("\\[[^\\]]*\\]", "").trim();
		} catch (Exception e) {
			errorCount.incrementAndGet();
			if (logger.isDebugEnabled())
				logger.debug("排重socket出现错误次数：" + errorCount.get());
		}

		if ("0".equals(res) || res == null || res.trim().length() == 0) {
			return ClientCenter.NOEXIST;
		} else if ("1".equals(res)) {
			return ClientCenter.EXIST;
		} else {
			return ClientCenter.NOEXIST;
		}

	}

	/**
	 * 检查下载中的url是否是已经下载过的正文页</b>
	 * 
	 * @param url
	 *            :新闻网页的URL
	 * @return true：存在，fasle不存在
	 */
	public static boolean isNewsExist(String url) {
		if (!serverCenterNotReadery.get()) {
			String request = "";
			try {
				request = MD5Utils.getMD5(url.getBytes("gbk"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String cmd = ClientCenter.getReturnCmd(request,
					_IS_CONTENT_LINK_EXIST);
			String res = null;
			try {
				res = ClientCenter.HandleSocket(cmd).replaceAll(
						"\\[[^\\]]*\\]", "").trim();
			} catch (Exception e) {
				errorCount.incrementAndGet();
				if (logger.isDebugEnabled())
					logger.debug("排重socket出现错误次数：" + errorCount.get(), e);
//				System.out.printf("排重socket出现错误次数：" + errorCount.get(), e);
				if (errorCount.get() > 30) {
					logger.error("排重功能可能失效！！排重socket出现错误次数大于：" + 30, e);
					serverCenterNotReadery.set(true);

				}
			}

			if ("0".equals(res) || res == null || res.trim().length() == 0) {
				return ClientCenter.NOEXIST;
			} else if ("1".equals(res)) {
				return ClientCenter.EXIST;
			} else {
				return ClientCenter.NOEXIST;
			}
		} else {
			return false;
		}

	}

	/**
	 * 客户端构造请求命令
	 * 
	 * @param
	 * @return result ：构造完成的命令
	 */
	private static String getReturnCmd(String request, int cmd) {
		return "[" + cmd + "]" + request + "\r\n";
	}

	private synchronized static Socket getSocket() {
		int errors = 0;
		while (ClientCenter.nowSocket == null
				|| !ClientCenter.nowSocket.isConnected()) {
			try {
				ClientCenter.nowSocket = new Socket();
				// ClientCenter.nowSocket = new Socket(Config.getServerIp(),
				// Config.getServerPort());
				// nowSocket.i
				ClientCenter.nowSocket.connect(new InetSocketAddress(Config
						.getServerIp(), Config.getServerPort()), 2000);
				ClientCenter.nowSocket.setSoTimeout(3000);
				Thread.sleep(1);
			} catch (Exception e) {

				errors++;
				logger.error("连接新闻排重中心第 " + errors + " 次出现错误："
						+ Config.getServerIp() + ":" + Config.getServerPort()
						+ "！请检查配置文件!  " + e.getMessage());
			}
			if (errors > 2) {
				errors = 0;
				endSocket();
				logger.error("排重中心【" + Config.getServerIp()
						+ "】没有正常启动，新闻无法排重，将以一种效率比较低下的方式运行，请检查排重中心的配置！");
				serverCenterNotReadery.set(true);
				break;
				// System.exit(-1);

			}
		}
		return ClientCenter.nowSocket;
	}

	/**
	 * 任务完成后发送结束命令
	 */
	public static void endSocket() {

		try {

			if (ClientCenter.nowSocket != null
					&& ClientCenter.nowSocket.isConnected())
				ClientCenter.nowSocket.close();
		} catch (IOException e) {
		}

		ClientCenter.nowSocket = null;

	}

	/**
	 * 发送socket请求
	 * 
	 * @param request
	 *            synchronized
	 * @return
	 */
	private synchronized static String HandleSocket(String request)
			throws Exception {
		Socket socket = null;
		OutputStream socketOut = null;
		BufferedReader br = null;
		String msg = null;
		if (ClientCenter.nowSocket != null)
			socket = ClientCenter.nowSocket;
		else
			socket = ClientCenter.getSocket();
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
//		System.out
//				.println(ClientCenter
//						.isNewsExist("http://www.xmnn.cn/dzbk/xmrb/20130423/201304/t20130423_3271590.htm"));
	}
}
