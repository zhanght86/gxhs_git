package com.meiah.webCrawlers.SitePlugin.Gxhs.finance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.JavaUtil;
import com.meiah.util.SysConstants;
import com.meiah.util.SysObject;
import com.meiah.webCrawlers.ClientCenter;
import com.meiah.webCrawlers.PageCrawler;

public class PageCrawler_Viewcn extends PageCrawler {
	
	public static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	public PageCrawler_Viewcn(TaskLink link, Task task) {
		super(link, task);
	}
	
	/**
	 * 抓取html页面源代码，如果需要，设置cookie、验证转向
	 * 
	 * @return
	 */
	protected String getPageContent() {

		String webContent = "";
		String downloadUrl = link.getUrl();
		WebPageDownloadeGbk downloader = new WebPageDownloadeGbk(downloadUrl);
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
			if (logger.isDebugEnabled())
				logger.debug(webContent);
			if (link.getLevel() == SysConstants.INIT_LEVEL
					&& webContent.indexOf("t3_ar_guard()") != -1) {
				try {
					String[] cookieInfo = JavaUtil.match(webContent, "(?is)(ant_stream_.*?)\\|path\\|(\\d+)\\|(\\d+)");
					String cookie = cookieInfo[1] + "=" + cookieInfo[3] + "/" + cookieInfo[2];
					task.setUcookies(cookie);
					downloader.setCookieStr(cookie);
					webContent = "";
					webContent = downloader.getPageContent();
				} catch (Exception e) {
				}
			}
			String cookie = downloader.getCookieGot();
			if (cookie != null && cookie.length() > 0) {
				task.setUcookies(cookie);
				logger.info(link.getUrl() + ":设置cookie: " + task.getUcookies());
			}
			String redirectUrl = downloader.getRedirectUrl();
			// if (redirectUrl != null && !redirectUrl.equals("")
			// && SysObject.isExistsUrl(redirectUrl)
			// && !redirectUrl.equals(link.getUrl())) {
			// // 如果转向后的链接已经抓取过则忽略该链接
			// return "";
			// }
			if (redirectUrl != null && !redirectUrl.equals("")
					&& downloadUrl.equals(task.getUrl())) {// 如果在任务url（一般为网站的主页），存在跳转，则添加跳转后的超链接为任务的前缀
				link.setUrl(redirectUrl);
				if (redirectUrl.substring(9).indexOf("/") == -1)
					redirectUrl = redirectUrl + "/";
				String prefix = redirectUrl.substring(0, redirectUrl
						.lastIndexOf("/") + 1);
				if (task.getPrefix() != null && !task.getPrefix().equals("")) {
					task.setPrefix(task.getPrefix() + "," + prefix);
				} else {
					task.setPrefix(prefix);
				}
				logger.warn("任务添加前缀: " + prefix + "  在链接：" + downloadUrl);

			}
		} catch (Exception e) {
			logger.warn("下载网页 " + downloadUrl + " 出现异常：" + e.getMessage());
		}

		return webContent;
	}

	protected void addLink(String webContent) {
		if (this.link.getLevel() < this.task.getMaxLevel()) {
			//过滤需要进行抓取的链接
			ArrayList<TaskLink> urlList = extractTaskLinks(webContent, task, link);
			
			if (logger.isDebugEnabled()) {
				logger.debug("extractTaskLinks,size:" + urlList.size());
			}
			for (int i = 0; i < urlList.size(); i++) {
				TaskLink link = urlList.get(i);
				String urlTobeAdd = link.getUrl();
				if(urlTobeAdd.contains("http://www.viewcn.com/zhwh/whrd")||
				   urlTobeAdd.contains("http://www.huaxia.com/zhwh/whrd")||
				   urlTobeAdd.contains("http://www.viewcn.com/zk")||
				   urlTobeAdd.contains("http://www.huaxia.com/zk")
				   ) {
					continue;
				}
				
//				System.out.println(urlTobeAdd);
				link.setLinkType(detectLinkType(link, task));
				if (logger.isDebugEnabled()) {
					logger.debug("链接：" + link.getTitle() + ":" + urlTobeAdd + link.getLinkType());
				}
				if (task.getMaxLevel() == 1 && link.getLinkType() == SysConstants.PAGETYPE_LIST) {
					logger.debug("first lever list page,ignore!");
					continue;
				}
				if (!SysObject.existsUrl(urlTobeAdd)) {
					if (!ClientCenter.isNewsExist(link.getUrl())) {
						// 通过排重中心看看链接是不是已经下载过的新闻
						SysObject.addLink(link);
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("链接：" + link.getTitle() + ":" + urlTobeAdd + "抓取过,并且是正文页");
						}
					}
				}
			}
		}
		if (this.link.getUrl().equals(task.getUrl()) && this.link.getLevel() == SysConstants.INIT_LEVEL) {
			logger.info("任务页面添加链接数：" + SysObject.getQueueSize());
		}
	}

}
