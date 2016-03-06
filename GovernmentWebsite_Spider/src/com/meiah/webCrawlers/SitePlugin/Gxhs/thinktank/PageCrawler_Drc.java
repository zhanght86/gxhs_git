package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.JavaUtil;
import com.meiah.util.PDFPaserUtil;
import com.meiah.util.SysConstants;
import com.meiah.util.WebPageDownloaderUseProxy;
import com.meiah.webCrawlers.PageCrawler;

public class PageCrawler_Drc extends PageCrawler {
	
	public static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	public PageCrawler_Drc(TaskLink link, Task task) {
		super(link, task);
	}

	/**
	 * 过滤提取在当前html页面下需要进行抓取的链接：规则
	 * <ol>
	 * <li>站内url，或者在任务前缀下的url（任务url为默认前缀）
	 * <li>不包含排除字符串的url，如：print.html
	 * <li>以其他文档类型为结束的ur 如：.txt,.doc,.mp3等等
	 * </ol>
	 * 
	 * @param webContent
	 *            超文本内容
	 * @param task
	 *            任务设置
	 * @param link
	 *            当前页面链接信息
	 */
	protected ArrayList<TaskLink> extractTaskLinks(String webContent, Task task, TaskLink link) {

		ArrayList<TaskLink> urlList = new ArrayList<TaskLink>();

		String regex = "\"title\":\"(.*?)\",\"subject\":\".*?\",\"link\":\"(.*?.htm)\"";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(webContent);

		while (m.find()) {
			try {
				String url = m.group(2);
				String title = m.group(1);
				TaskLink tl = new TaskLink();
				tl.setTaskid(task.getTaskid());
				tl.setLevel(link.getLevel() + 1);
				tl.setRefererUrl(link.getUrl());
				tl.setTitle(title);
				tl.setUrl(task.getPrefix() + url);
				urlList.add(tl);
			} catch (Exception e) {
				logger.error("获取和讯滚动新闻链接出现异常", e);
			}
		}
		return urlList;
	}
	/**
	 * 抓取html页面源代码，如果需要，设置cookie、验证转向
	 * 
	 * @return
	 */
	protected String getPageContent() {

		String webContent = "";
		String downloadUrl = link.getUrl();
		if(downloadUrl.endsWith(".pdf")) {
			webContent = PDFPaserUtil.pasePDF2HTMLByHttp(downloadUrl);
		} else {
			WebPageDownloaderUseProxy downloader = new WebPageDownloaderUseProxy(downloadUrl);
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
		}
		return webContent;
	}
}
