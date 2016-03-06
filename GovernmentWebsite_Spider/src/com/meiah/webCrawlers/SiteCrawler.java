package com.meiah.webCrawlers;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.meiah.dao.TaskDao;
import com.meiah.htmlParser.LinkExtractor;
import com.meiah.po.Link;
import com.meiah.po.RunningTask;
import com.meiah.po.SiteConfig;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.JavaUtil;
import com.meiah.util.SysConstants;
import com.meiah.util.SysObject;
import com.meiah.util.WebPageDownloader;

public class SiteCrawler extends Thread {

	private static Logger logger = Logger.getLogger(SiteCrawler.class);

	public static Task task;

	public static String topDomain;

	private static final int MAX_PAGES = 800;
	// public static String webDomain;

	public static String fileSavePath = "";

	public static SiteConfig siteConfig = null;

	// public static Map<Long, String> conformMap = null;

	public static Set<String> homePageUrls;

	public SiteCrawler(String taskid) {
		
		task = TaskDao.getInstance().getTask(taskid);
		if (task.getPages() <= 0)
			initTaskMaxDownlosdPages(task);

		initTaskMaxThreads(task);

		task.setPrefix(getPrefix());

		if (task.getUrl().indexOf("/", 9) == -1)
			task.setUrl(task.getUrl() + "/");// 补全

		topDomain = JavaUtil.getHost1(task.getUrl());

		fileSavePath = TaskDao.getInstance().getSavePath();
		task.pluginMode = PluginFactory.getInstance().getPluginMode(task);
		if (task.pluginMode == true) {
			logger.info("任务运行模式：插件模式!");
			SiteConfig conf = PluginFactory.getInstance().getSiteConfig(task);
			String contentUrlRex = task.getPageType();
			String contentUrlRexXML = conf.getContentUrlRegex();
			if (contentUrlRex != null && !contentUrlRex.trim().equals("")) {
				contentUrlRex = contentUrlRex.replace("content:", "");
				if (contentUrlRexXML != null && !contentUrlRexXML.trim().equals(""))
					contentUrlRex = "((" + contentUrlRex + ")|(" + conf.getContentUrlRegex() + "))";
				conf.setContentUrlRegex(contentUrlRex);
			}
			task.setSiteConfig(conf);
			String crawlerClassname = conf.getCrawlerClassname();
			if (crawlerClassname != null && !crawlerClassname.trim().equals(""))
				logger.info("爬取插件:" + crawlerClassname);
			else
				logger.info("默认爬取方式");
			String resolverClassname = conf.getResolverClassname();
			if (resolverClassname != null && !resolverClassname.trim().equals(""))
				logger.info("解析插件:" + resolverClassname);
			else {
				logger.info("解析插件不存在!请检查配置文件:typeMapping.xml");
			}

		} else {
			logger.info("任务运行模式：自动模式!");

		}

	}

	private Set<String> getHomePageUrls() {
		Set<String> urls = null;
		String homePageUrl = TaskDao.getInstance().getHomePageUrl(
				task.getTaskid());
		Link link = new Link();
		link.setUrl(homePageUrl);
		if (homePageUrl != null && !homePageUrl.equals("")) {
			WebPageDownloader downloader = new WebPageDownloader(homePageUrl);
			downloader.setCheckCookie(true);
			downloader.setCheckRedirect(true);
			if (task.getProxyurl() != null && task.getProxyurl().length() > 0) {
				downloader.setUseProxyIf(true);
				downloader.setProxyUrl(task.getProxyurl());
				downloader.setProxyPort(task.getProxyport());
				downloader.setProxyUser(task.getProxyuser());
				downloader.setProxyPwd(task.getProxypwd());
			}
			String webContent = "";
			try {
				webContent = downloader.getPageContent();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("下载主页出现异常：" + homePageUrl, e);
			}
			ArrayList<Link> urlList = LinkExtractor.getUrlsByParser(link, webContent);
			if (urlList.size() <= 0)
				return null;
			urls = new HashSet<String>();
			for (int i = 0; i < urlList.size(); i++) {
				String url = urlList.get(i).getUrl();
				if (url != null && !url.equals(""))
					urls.add(url);
			}

		}
		return urls;
	}

	public void mainPro() {
		logger.info("任务：url: " + task.getUrl() + "启动");
		if (task.getMaxLevel() == 1) {
			logger.info("任务抓取方式：单层下载");
		} else {
			logger.info("任务抓取方式：逐层下载");
		}
		TaskMonitor monitor = new TaskMonitor(task);//
		// 运行任务维护
		monitor.setDaemon(true);
		monitor.start();
		injectInitUrls(task);
		if (logger.isDebugEnabled())
			logger.debug("injectInitUrls,size:" + SysObject.getQueueSize());
		homePageUrls = getHomePageUrls();
		// 循环读取URL
		while (true) {
			RunningTask rtask = (RunningTask) SiteCrawler.task;
			if (rtask.getRunstate() == Task.PAUSE || rtask.getRunstate() == Task.STOP) {// 暂停
				try {
					Thread.sleep(1000);
					continue;
				} catch (InterruptedException e) {
				}
			}

			TaskLink link1 = null;
			try {
				link1 = SysObject.getLink();
			} catch (Exception e1) {
				logger.error("读取URL错误，继续读取下一个", e1);
				continue;
			}

			if (link1 == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				continue;
			}

			// 线程最大数量超出，休眠
			while (SysObject.crawlThreads.get() >= SiteCrawler.task.getMaxthread()) {
				try {
					Thread.sleep(200);
					continue;
				} catch (InterruptedException e) {
				}
			}
			try {
				if (task.pluginMode == true) {
					PageCrawler crawler = PluginFactory.getInstance().getPageCrawler(link1, task);
					if (crawler != null)
						crawler.start();
					else {
						new PageCrawler(link1, task).start();
					}
				} else {
					new PageCrawler(link1, task).start();
				}
				((RunningTask) task).setThreadnums(SysObject.crawlThreads.get());
				// 设置当前线程程数量
				((RunningTask) task).setNowurl(link1.getUrl());

			} catch (Exception e) {
				logger.error("下载单个URL失败 ", e);
			}

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}
	}

	public void run() {
		mainPro();
	}

	/**
	 * @param task
	 * @func 设置任务的最大下载页数，規則：
	 *       <ol>
	 *       <li>任务最大页数1000，不能超过</li>
	 *       <li>
	 *       在<b>不限定</b>只下载单层页面的条件下,任务的初始最大下载页数500，每个添加一个第一层页面（子版块页面），则增加下载页面100
	 *       </li>
	 *       <li>
	 *       在<b>限定</b>只下载单层页面的条件下，任务的初始最大下载页数300，每个添加一个第一层页面（子版块页面），则增加下载页面100</li>
	 * 
	 *       </ol>
	 * 
	 */
	private void initTaskMaxDownlosdPages(Task task) {
		int maxPages = 500;
		int subBoardCounts = task.getSubBoardUrl().split(
				SysConstants.SUB_SPLITER).length;
		if (task.getMaxLevel() == 1) {
			maxPages += subBoardCounts * 100;
		} else {
			maxPages = 300;
			maxPages += subBoardCounts * 100;
		}
		maxPages = maxPages > MAX_PAGES ? MAX_PAGES : maxPages;
		task.setPages(maxPages);
	}

	/**
	 * @param task
	 * @func 设置任务的最大下载线程数，規則：
	 *       <ol>
	 *       <li>在通过境外代理下载的条件下,任务的最大下载线程数2</li>
	 *       <li>在非代理条件下，任务的最大下载线程数3</li>
	 * 
	 *       </ol>
	 * 
	 */
	private void initTaskMaxThreads(Task task) {
		// int maxThreads = 3;
		// if (task.getProxyurl() != null && task.getProxyurl().length() > 0) {
		// maxThreads = 2;
		// }
		if (task.getMaxthread() > 20 || task.getMaxthread() <= 0)
			task.setMaxthread(20);
	}

	private void injectInitUrls(Task task) {
		// 插入任务的url
		TaskLink link = new TaskLink();
		link.setUrl(task.getUrl());
		link.setLevel(SysConstants.INIT_LEVEL);
		link.setVisited(true);
		link.setTaskid(task.getTaskid());
		SysObject.existsUrl(task.getUrl());
		SysObject.addLink(link);

		// 插入任务板块的url
		String subs = task.getSubBoardUrl();
		if (subs != null && !subs.replaceAll("[\\s　]+", "").equals("")) {
			String[] subUrls = task.getSubBoardUrl().split(SysConstants.SUB_SPLITER);
			for (String subUrl : subUrls) {
				TaskLink tl = new TaskLink();
				tl.setUrl(subUrl);
				tl.setLevel(SysConstants.INIT_LEVEL);
				tl.setVisited(true);
				tl.setTaskid(task.getTaskid());
				SysObject.existsUrl(subUrl);
				SysObject.addLink(tl);
			}
		}
		if (task.getTurl().indexOf("news.baidu.com") != -1) {
			String webContent = getPageContent(task.getTurl());
			// 加入热词搜索新闻页面
			String patternHot = "href=\"((http://news.baidu.com/.*?sp=hotquery.*?)|(http://news.baidu.com/z/.*?/zhuanti.html))\"";
			Pattern p = Pattern.compile(patternHot);
			Matcher m = p.matcher(webContent);
			while (m.find()) {
				String url = m.group(1);
				// System.out.println(url);
				TaskLink tl = new TaskLink();
				tl.setUrl(url);
				tl.setLevel(1);
				tl.setVisited(true);
				tl.setTaskid(task.getTaskid());
				SysObject.existsUrl(url);
				SysObject.addLink(tl);
			}
		}
	}

	/**
	 * 抓取html页面源代码，如果需要，设置cookie、验证转向
	 * 
	 * @return
	 */
	protected String getPageContent(String url) {
		String webContent = "";
		String downloadUrl = url;
		WebPageDownloader downloader = new WebPageDownloader(downloadUrl);
		downloader.setCheckCookie(true);
		downloader.setCookieStr(task.getUcookies());
		downloader.setCheckRedirect(true);
		if (task.getProxyurl() != null && task.getProxyurl().length() > 0) {
			downloader.setUseProxyIf(true);
			downloader.setProxyUrl(task.getProxyurl());
			downloader.setProxyPort(task.getProxyport());
			downloader.setProxyUser(task.getProxyuser());
			downloader.setProxyPwd(task.getProxypwd());
		}

		try {
			webContent = downloader.getPageContent();
			String cookie = downloader.getCookieGot();
			if (cookie != null && cookie.length() > 0) {
				task.setUcookies(cookie);
				logger.info(url + ":设置cookie: " + task.getUcookies());
			}
			String redirectUrl = downloader.getRedirectUrl();
			if (redirectUrl != null && !redirectUrl.equals("")
					&& SysObject.isExistsUrl(redirectUrl)) {
				// 如果转向后的链接已经抓取过则忽略该链接
				return "";
			}

		} catch (Exception e) {
			logger.warn("下载网页 " + downloadUrl + " 出现异常：" + e.getMessage());
		}
		return webContent;
	}

	private String getPrefix() {
		String sitePrefix = task.getUrl().substring(0,
				task.getUrl().indexOf("/", 9));

		String prefix = task.getPrefix();

		if (prefix != null && !prefix.equals("")) {
			prefix = sitePrefix + "," + prefix;
		} else {
			prefix = sitePrefix;
		}
		String subPefix = task.getSubBoardUrl();
		if (subPefix != null && !subPefix.trim().equals("")) {
			String[] subPrefixs = task.getSubBoardUrl().split(
					SysConstants.SUB_SPLITER);
			for (int i = 0; i < subPrefixs.length; i++) {
				String subPrefix = subPrefixs[i];
				subPrefix = subPrefix.substring(0,
						subPrefix.indexOf("/", 9) + 1);
				prefix = prefix + "," + subPrefix;
			}
		}
		if (task.getTurl().indexOf("news.baidu.com") != -1) {
			// task.setPrefix("http");
			prefix = "http";
			task.setSubstr("baidu.com");
			task.setMaxLevel(1);
		}
		return prefix;

	}

	public static void main(String[] args) {
		
//		System.out.println("************************************");
		Properties props = new Properties();
		try {

			InputStream istream = SiteCrawler.class
					.getResourceAsStream("/log4j.properties");
			props.load(istream);
			istream.close();
			String logPath = ClassLoader.getSystemResource("").getPath()
					+ File.separatorChar + "log" + File.separatorChar;
			props.setProperty("log4j.appender.logfile.File", logPath + "task_"
					+ args[0] + "_info.log");
			props.setProperty("log4j.appender.logfile1.File", logPath + "task_"
					+ args[0] + "_error.log");

			// 重新配置后，日志会打到新的文件去。
			PropertyConfigurator.configure(props);// 装入log4j配置信息

		} catch (Exception e) {
			logger.error("装入属性文件异常 Exception ", e);
		}
		try {
			String taskid = args[0];
			// logger.info("任务id：" + taskid);
			SiteCrawler c = new SiteCrawler(taskid);
			// logger.info("获取任务成功");
			c.start();
		} catch (Exception e) {
			logger.error("主线程运行异常", e);
			System.exit(0);
		}

	}
}
