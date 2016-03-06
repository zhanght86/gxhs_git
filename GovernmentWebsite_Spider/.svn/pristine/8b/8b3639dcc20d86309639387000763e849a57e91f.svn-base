package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.meiah.htmlParser.LinkExtractor;
import com.meiah.linkfilters.ExcludeStrFilter;
import com.meiah.linkfilters.FileExtensionFilter;
import com.meiah.linkfilters.LinkFilter;
import com.meiah.linkfilters.LinkFilterUtil;
import com.meiah.linkfilters.LocalLinkFilter;
import com.meiah.po.Link;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.webCrawlers.PageCrawler;

public class PageCrawler_IjsCass extends PageCrawler {
	
	public static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	public PageCrawler_IjsCass(TaskLink link, Task task) {
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
			if(!(tl.getUrl().contains("japanese"))) {
				urlList.add(tl);
			}
		}
		
		return urlList;
	}
}
