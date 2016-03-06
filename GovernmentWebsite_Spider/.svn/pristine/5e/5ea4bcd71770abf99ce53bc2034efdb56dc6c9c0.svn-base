package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.SysConstants;
import com.meiah.util.SysObject;
import com.meiah.webCrawlers.ClientCenter;
import com.meiah.webCrawlers.PageCrawler;

public class PageCrawler_Nisd extends PageCrawler {
	
	public static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	public PageCrawler_Nisd(TaskLink link, Task task) {
		super(link, task);
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
				if(urlTobeAdd.equals("http://www.nisd.cass.cn/cate/30.htm")) {
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
