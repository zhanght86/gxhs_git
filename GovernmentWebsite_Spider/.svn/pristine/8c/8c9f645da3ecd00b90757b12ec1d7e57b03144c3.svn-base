package com.meiah.webCrawlers.SitePlugin.Gxhs.finance;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.FrameTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import com.meiah.exhtmlparser.IFrameTag;
import com.meiah.linkfilters.ExcludeStrFilter;
import com.meiah.linkfilters.FileExtensionFilter;
import com.meiah.linkfilters.LinkFilter;
import com.meiah.linkfilters.LinkFilterUtil;
import com.meiah.linkfilters.LocalLinkFilter;
import com.meiah.po.Link;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.JavaUtil;
import com.meiah.util.SysConstants;
import com.meiah.util.SysObject;
import com.meiah.webCrawlers.ClientCenter;
import com.meiah.webCrawlers.PageCrawler;

/**
 * 搜房网 定向采集类
 * 
 * @author dingrr
 * 
 */
public class PageCrawler_Dynews extends PageCrawler {
	
	private static Logger logger = Logger.getLogger(PageCrawler_Dynews.class);
	public PageCrawler_Dynews(TaskLink link, Task task) {
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
				link.setLinkType(detectLinkType(link, task));
				if(urlTobeAdd.contains("http://dynews.zjol.com.cn/dynews/photo") 
						|| urlTobeAdd.contains("http://dynews.zjol.com.cn/dynews/16040000")
						|| urlTobeAdd.contains("http://dynews.zjol.com.cn/dynews/16120000")) 
					continue;
				if(urlTobeAdd.contains("http://dynews.zjol.com.cn/dynews/zhuanti/2016") ||
						urlTobeAdd.contains("http://dynews.zjol.com.cn/dynews/system/2015/04/23/019261379.shtml")) {
					link.setLinkType(SysConstants.PAGETYPE_LIST);
				}
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
