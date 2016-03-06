package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.meiah.po.SiteConfig;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.JavaUtil;
import com.meiah.util.SysConstants;
import com.meiah.webCrawlers.PageCrawler;

public class PageCrawler_Cf40 extends PageCrawler {
	
	public static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	public PageCrawler_Cf40(TaskLink link, Task task) {
		super(link, task);
	}

	/**
	 * @param 传入链接
	 * @return 是否正文页面
	 */
	protected int detectLinkType(TaskLink link, Task task) {
		
		String url = link.getUrl();
		SiteConfig siteConfig = task.getSiteConfig();
		int ret = SysConstants.PAGETYPE_UNKNOWN;
		if (siteConfig != null) {
			// 插件模式
			String newsUrlPatterns = null;
			if (StringUtils.equalsIgnoreCase(link.getRefererUrl(), "http://www.cf40.org.cn/plus/f_list.php?pageno=0")) {
				newsUrlPatterns = siteConfig.getContentUrlRegex();
			} else if (StringUtils.equalsIgnoreCase(link.getRefererUrl(), "http://www.cf40.org.cn/plus/list.php?tid=54")) {
				newsUrlPatterns = "/html/CF40dongtai/luntanxinwen/\\d{6}/\\d+-\\d+.html";
			} else {
				newsUrlPatterns = siteConfig.getContentUrlRegex();
			}
			if (newsUrlPatterns != null && !newsUrlPatterns.trim().equals("")) {
				String[] patterns = newsUrlPatterns.split(SiteConfig.SPLITER);
				for (String pattern : patterns) {
					if (JavaUtil.isAllMatch(url, pattern.trim()))
						ret = SysConstants.PAGETYPE_CONTENT;
				}
				if (ret != SysConstants.PAGETYPE_CONTENT)
					ret = SysConstants.PAGETYPE_LIST;
			}
		} else if (task.getPageType() != null && task.getPageType().trim().length() > 0) {
			// 非插件模式，但是有配置pageType
			if (isContentPage(link.getUrl(), task.getPageType()))
				ret = SysConstants.PAGETYPE_CONTENT;
			else
				ret = SysConstants.PAGETYPE_LIST;
		}
		return ret;
	};

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
		String url = link.getUrl();
		if (StringUtils.equalsIgnoreCase(url, "http://www.cf40.org.cn/plus/list.php?tid=105")
				|| StringUtils.equalsIgnoreCase(url, "http://www.cf40.org.cn/plus/list.php?tid=106")
				|| StringUtils.equalsIgnoreCase(url, "http://www.cf40.org.cn/plus/list.php?tid=107")
				|| StringUtils.equalsIgnoreCase(url, "http://www.cf40.org.cn/plus/list.php?tid=8")) {
			String regex = "<strong>.*/plus/view.php\\?aid=\\d+.*</strong>";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(webContent);
			
			String regex1 = "href='(.*/plus/view.php\\?aid=\\d+.*)' title='(.*?)'";
			Pattern p1 = Pattern.compile(regex1);
			Matcher m1 = null;
			String strongUrl = null;
			
			while (m.find()) {
				try {
					strongUrl = m.group();
					m1 = p1.matcher(strongUrl);
					while (m1.find()) {
						TaskLink tl = new TaskLink();
						tl.setTaskid(task.getTaskid());
						tl.setLevel(link.getLevel() + 1);
						tl.setRefererUrl(link.getUrl());
						tl.setTitle(m1.group(2));
						tl.setUrl(task.getPrefix() + m1.group(1));
						urlList.add(tl);
					}
					
				} catch (Exception e) {
					logger.error("获取和讯滚动新闻链接出现异常", e);
				}
			}
			return urlList;
		} else {
			return super.extractTaskLinks(webContent, task, link);
		}
	}
	
}
