package com.meiah.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;

import com.meiah.po.TaskLink;
import com.meiah.po.WebPage;

/**
 * 系统级别变量保存：每个任务当前启动的线程数及操作
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-04-24
 * 
 */

public class SysObject {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SysObject.class);
	
	public static Hashtable<String,String> ipTable = new Hashtable<String,String>();

	public static LinkQueue waitingQueue;

	public static Queue<WebPage> webPageQueue;

	static {
		waitingQueue = new LinkQueue();
		webPageQueue = new LinkedList<WebPage>();
	}

	public synchronized static void addPage(WebPage page) {
		webPageQueue.add(page);
	}

	public synchronized static WebPage getPage() {
		if (!webPageQueue.isEmpty())
			return webPageQueue.poll();
		else
			return null;
	}

	public synchronized static int getPageSize() {

		return webPageQueue.size();

	}

	public synchronized static void addLink(TaskLink link) {

		waitingQueue.put(link);
	}

	public synchronized static int getQueueSize() {

		return waitingQueue.getQueueSize();
	}

	public synchronized static TaskLink getLink() {
		if (!waitingQueue.isEmpty())
			return waitingQueue.pop();
		else
			return null;
	}

	// 任务线程数集合 域名，线程数量
	public static AtomicInteger crawlThreads = new AtomicInteger(0);
	public static AtomicInteger resloveThreads = new AtomicInteger(0);

	public static Map<Integer, Double> clickCoefficient = new HashMap<Integer, Double>();

	static {
		// 初始化整点点击系数
		clickCoefficient.put(0, 3.6);
		clickCoefficient.put(1, 2.2);
		clickCoefficient.put(2, 1.5);
		clickCoefficient.put(3, 1.1);
		clickCoefficient.put(4, 0.8);
		clickCoefficient.put(5, 0.7);
		clickCoefficient.put(6, 0.8);
		clickCoefficient.put(7, 1.3);
		clickCoefficient.put(8, 3.4);
		clickCoefficient.put(9, 5.6);
		clickCoefficient.put(10, 6.0);
		clickCoefficient.put(11, 6.0);
		clickCoefficient.put(12, 5.1);
		clickCoefficient.put(13, 5.5);
		clickCoefficient.put(14, 5.9);
		clickCoefficient.put(15, 6.2);
		clickCoefficient.put(16, 6.4);
		clickCoefficient.put(17, 5.9);
		clickCoefficient.put(18, 4.9);
		clickCoefficient.put(19, 4.9);
		clickCoefficient.put(20, 5.3);
		clickCoefficient.put(21, 5.8);
		clickCoefficient.put(22, 5.9);
		clickCoefficient.put(23, 5.2);
	}

	/**
	 * 每个任务对应的URL的CRC32列表用来过滤重复URL。
	 */
	private static Set<Long> urlLists = new HashSet<Long>();

	/**
	 * 检查传入url是否存在，返回标识能否写入URL列表文件
	 * 
	 * @param taskid
	 *            任务URL，KEY
	 * @param url
	 *            请求写入URL
	 * @return 是否可以写入URL列表文件
	 */
	public synchronized static boolean existsUrl(String url) {

		boolean result = true;
		CRC32 c = new CRC32();
		c.update(url.getBytes());
		// logger.debug("try add url:" + url);
		if (!urlLists.contains(c.getValue())) {
			urlLists.add(c.getValue());
			// logger.debug("add new url:" + url);
			result = false;
		}

		return result;
	}

	/**
	 * 是否包含给出URL
	 * 
	 * @param taskid
	 * @param url
	 * @return
	 */
	public synchronized static boolean isExistsUrl(String url) {
		// logger.debug("url: " + url);
		boolean result = false;
		CRC32 c = new CRC32();
		c.update(url.getBytes());
		if (urlLists.contains(c.getValue())) {
			result = true;
		}
		return result;
	}

	/**
	 * 任务是否运行过，若列表为空，认为没有运行过或正在等待运行
	 * 
	 * @param taskid
	 * @return
	 */
	public synchronized static boolean taskRuned() {
		if (urlLists.size() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 清空任务列表
	 * 
	 * @param taskid
	 */
	public synchronized static void cleanTask() {
		urlLists.clear();
	}

	/**
	 * 获取现在任务数量
	 * 
	 * @return
	 */
	public static int getUrllistCount() {
		return urlLists.size();
	}

}
