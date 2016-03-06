package com.meiah.webCrawlers.SitePlugin.Gxhs.govermentwebsite;

import java.util.ArrayList;

import com.meiah.htmlParser.LinkExtractor;
import com.meiah.linkfilters.ExcludeStrFilter;
import com.meiah.linkfilters.FileExtensionFilter;
import com.meiah.linkfilters.LinkFilter;
import com.meiah.linkfilters.LinkFilterUtil;
import com.meiah.linkfilters.LocalLinkFilter;
import com.meiah.po.Link;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.JavaUtil;
import com.meiah.util.PDFPaserUtil;
import com.meiah.util.SysConstants;
import com.meiah.util.WebPageDownloaderUseProxy;
import com.meiah.webCrawlers.PageCrawler;

public class PageCrawler_ModGOV extends PageCrawler{

	public PageCrawler_ModGOV(TaskLink link, Task task) {
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
		if (logger.isDebugEnabled()) {
			logger.debug("extracting TaskLinks");
		}
		ArrayList<TaskLink> urlList = new ArrayList<TaskLink>();

		String prefix = task.getPrefix();
		String[] localSitePrefixs = prefix.split(",");
		LinkFilter LocalLinkFilter = new LocalLinkFilter(localSitePrefixs);// 过滤器，保留站点内和任务前缀下的超链接

		String[] fileExtetions = ".xls,.xml,.txt,.jpg,.mp3,.mp4,.doc,.mpg,.mpeg,.jpeg,.gif,.png,.js,.zip,.rar,.exe,.swf,.rm,.ra,.asf,.css,.bmp,.pdf,.z,.gz,.tar,.cpio,.class".split(",");
		LinkFilter fileExtetionFilter = new FileExtensionFilter(fileExtetions);
		LinkFilter noFileExtetionFilter = LinkFilterUtil.not(fileExtetionFilter);// 过滤器，过滤掉非文本的一些的超链接

		String excludeStr = task.getSubstr();
		LinkFilter excludeStrFilter = new ExcludeStrFilter(excludeStr);
		LinkFilter noExcludeStrFilter = LinkFilterUtil.not(excludeStrFilter);// 过滤器，保留不包含排除字符串的超链接
		LinkFilter[] filters = new LinkFilter[] { LocalLinkFilter, noFileExtetionFilter, noExcludeStrFilter };

		LinkFilter taskLinkFilter = LinkFilterUtil.and(filters);
		Link l = (Link) link;

		ArrayList<Link> links = LinkExtractor.getPageUrlListByParser(l, webContent, taskLinkFilter);
		for (int i = 0; i < links.size(); i++) {
			// if (links.get(i).getUrl().indexOf("c1069") != -1)
			// logger.debug("debug");
			TaskLink tl = new TaskLink(links.get(i));
			// if (tl.getTitle().indexOf("c1069") != -1)
			// logger.debug("debug");
			String title = fixTitle(link, tl.getTitle(), webContent);// 对于超链接标题的一个处理
			tl.setTitle(title);
			tl.setTaskid(task.getTaskid());
			tl.setLevel(link.getLevel() + 1);
			urlList.add(tl);
		}
		for (int i = 0; i < urlList.size(); i++) {
			if(urlList.get(i).getUrl().contains("http://www.mod.gov.cn/video")) {
				urlList.remove(i);
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
