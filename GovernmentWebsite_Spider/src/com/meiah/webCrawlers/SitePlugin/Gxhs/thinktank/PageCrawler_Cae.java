package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.JavaUtil;
import com.meiah.util.SysConstants;
import com.meiah.util.WebPageDownloader;
import com.meiah.webCrawlers.PageCrawler;

public class PageCrawler_Cae extends PageCrawler {
	
	public static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	public PageCrawler_Cae(TaskLink link, Task task) {
		super(link, task);
	}

	/**
	 * 抓取html页面源代码，如果需要，设置cookie、验证转向
	 * @return
	 */
	protected String getPageContent() {

		String webContent = "";
		String downloadUrl = link.getUrl();
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
		if(webContent.contains("class='left_subnavilist'")) {
			StringBuffer webCon = new StringBuffer();
			String right = "";
			String left = "";
			right = webContent.substring(webContent.indexOf("class='left_subnavilist'") , webContent.indexOf("class='tongy_md'"));
			webCon = webCon.append(right);
			left = webContent.substring(webContent.indexOf("class='lieb_news_md'") , webContent.lastIndexOf("class='foot'"));
			webCon = webCon.append(left);
			webContent = webCon.toString();
		}
		if(webContent.contains("class='neir_title'")) {
			StringBuffer webCon = new StringBuffer();
			String main = "";
			main = webContent.substring(webContent.lastIndexOf("政务公开") , webContent.indexOf("class='foot'"));
			webCon = webCon.append(main);
			webContent = webCon.toString();
		}
		return webContent;
	}
}