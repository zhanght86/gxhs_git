package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.SysConstants;
import com.meiah.util.SysObject;
import com.meiah.util.WebPageDownloader;
import com.meiah.webCrawlers.ClientCenter;
import com.meiah.webCrawlers.PageCrawler;
/**
 * 中国环境科学院新闻采集
 * @author dingrr
 * @date 2015-10-21
 */
public class PageCrawler_Craes extends PageCrawler {
	
	public static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	public PageCrawler_Craes(TaskLink link, Task task) {
		super(link, task);
	}
	/**
	 * 按照提取规则获取新闻url
	 */
	protected void addLink(String webContent) {
		if (this.link.getLevel() < this.task.getMaxLevel()) {
			ArrayList<TaskLink> urlList = extractTaskLinks(webContent, task, link);
			if (logger.isDebugEnabled()) {
				logger.debug("extractTaskLinks,size:" + urlList.size());
			}
			getLink(urlList);
		}
		
		if (this.link.getUrl().equals(task.getUrl()) && this.link.getLevel() == SysConstants.INIT_LEVEL) {
			logger.info("任务页面添加链接数：" + SysObject.getQueueSize());
		}
	}
	/**
	 * 获得需要的版块下的新闻url
	 * @param i
	 */
	protected void getLink(ArrayList<TaskLink> urlList) {
		for (int i = 0; i < urlList.size(); i++) {
			TaskLink link = urlList.get(i);
			String urlTobeAdd = link.getUrl();
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
					try {
						if(urlTobeAdd.matches("http://www.craes.cn/cn/kycg/lunwen.html") ||
						   urlTobeAdd.matches("http://www.craes.cn/cn/gzdt/gzdt.html") ||
						   urlTobeAdd.matches("http://www.craes.cn/cn/zdrw/zdrw.html") ||
						   urlTobeAdd.matches("http://www.craes.cn/cn/kyxm/kyxm.html")) {
							WebPageDownloader downloader = new WebPageDownloader(urlTobeAdd);
							String webContent = downloader.getPageContent();
							addLink(webContent);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("链接：" + link.getTitle() + ":" + urlTobeAdd + "抓取过,并且是正文页");
					}
				}
			}
		}
	}
}
