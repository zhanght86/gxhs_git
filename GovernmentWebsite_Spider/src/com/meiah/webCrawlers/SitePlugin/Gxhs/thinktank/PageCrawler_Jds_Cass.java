package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.JavaUtil;
import com.meiah.util.SysConstants;
import com.meiah.webCrawlers.PageCrawler;

public class PageCrawler_Jds_Cass extends PageCrawler {
	
	public static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	public PageCrawler_Jds_Cass(TaskLink link, Task task) {
		super(link, task);
	}

	/**
	 * 抓取html页面源代码，如果需要，设置cookie、验证转向
	 * @return
	 */
	protected String getPageContent() {

		String webContent = "";
		String downloadUrl = link.getUrl();
		WebPageDownloaderJDS downloader = new WebPageDownloaderJDS(downloadUrl);
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
		webContent = webContent.replaceAll("\"", "'");
		if(webContent.contains("栏目循环列表开始")) {
			StringBuffer sdf = new StringBuffer();
			String main = "";
			main = webContent.substring(webContent.indexOf("栏目循环列表开始") , webContent.indexOf("栏目循环列表结束"));
			sdf = sdf.append(main);
			webContent = sdf.append(main).toString();
		}
		if(webContent.contains("class='fontzoomcon'")) {
			StringBuffer sdf = new StringBuffer();
			String main = "";
			main = webContent.substring(webContent.indexOf("class='fontzoomcon'") , webContent.lastIndexOf("class='bdsharebuttonbox'"));
			sdf = sdf.append(main);
			webContent = sdf.append(main).toString();
			webContent = webContent.replaceAll("(?is)<.*?>", "");
		}
		if(webContent.contains("class='fontzoomnews'")) {
			StringBuffer sdf = new StringBuffer();
			String main = "";
			main = webContent.substring(webContent.indexOf("class='fontzoomnews'") , webContent.lastIndexOf("pe.end.pagination"));
			sdf = sdf.append(main);
			webContent = sdf.append(main).toString();
		}
		return webContent;
	}
	
}
