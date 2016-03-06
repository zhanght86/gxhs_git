package com.meiah.webCrawlers.SitePlugin.Gxhs.finance;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import com.meiah.po.RunningTask;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.po.WebPage;
import com.meiah.util.Config;
import com.meiah.util.JavaUtil;
import com.meiah.util.SysConstants;
import com.meiah.util.SysObject;
import com.meiah.webCrawlers.ClientCenter;
import com.meiah.webCrawlers.PageCrawler;
import com.meiah.webCrawlers.PageResolver;
import com.meiah.webCrawlers.PluginFactory;

/**
 * 新浪财经,公司简介,财务报表 定向采集类
 * @author liubb
 *
 */
public class PageCrawler_Hldhouse extends PageCrawler {
	
	protected static Map<String,String> columnUrl = new HashMap<String,String>();
	
	public PageCrawler_Hldhouse(TaskLink link, Task task) {
		super(link, task);
	}
	
	
	protected void mainPro() {
		columnUrl.put("http://www.hldhouse.com/news/","咨讯");
		for(Entry<String,String> entry : columnUrl.entrySet()) {
			if (!SysObject.existsUrl(entry.getKey())) {
				TaskLink tl = new TaskLink();
				tl.setUrl(entry.getKey());
				tl.setTitle(entry.getValue());
				tl.setLevel(link.getLevel());
				SysObject.addLink(tl);
			}
		}
		pageCount.incrementAndGet();
		int save_BatchCount = Config.getSave_BatchCount();
		rtask = (RunningTask) task;
		long starttime = System.currentTimeMillis();
		String webContent = getPageContent();
		long useTime = System.currentTimeMillis() - starttime;
		totalTime.addAndGet(useTime);
		if (webContent.length() < 100) {
			logger.warn("网页内容过短？！:" + link.getUrl());
			return;
		}
		long t = System.currentTimeMillis();
		addLink(webContent);
		if (logger.isDebugEnabled())
			logger.debug("add link took: " + (System.currentTimeMillis() - t) + " ms");
		WebPage page = new WebPage(link, webContent);
		if (link.getLevel() > SysConstants.INIT_LEVEL)
			SysObject.addPage(page);// 将爬取后的页面信息，加入的缓存队列
		if (SysObject.getPageSize() >= save_BatchCount) {
			// 如果缓存队列中的页面数超过指定的数目则 启动解析线程进行解析
			if (task.pluginMode == true) {
				PageResolver reslover;
				try {
					reslover = PluginFactory.getInstance().getPageResolver(task);
					reslover.start();
				} catch (Exception e) {
					logger.error("", e);
				}

			} else {
				new PageResolver(task).start();// 任务结束时有可能还有缓存的页面未解析，此次保存
			}

		}
		rtask.setDownloadPages(rtask.getDownloadPages() + 1);
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
				if(urlTobeAdd.contains("http://www.hldhouse.com/fcsp/")||
				   urlTobeAdd.contains("http://www.hldhouse.com/bbs/")||
				   urlTobeAdd.contains("http://www.hldhouse.com/houseonline/")||
				   urlTobeAdd.contains("http://www.hldhouse.com/fangyuan/")||
				   urlTobeAdd.contains("http://www.hldhouse.com/info")||
				   urlTobeAdd.contains("http://www.hldhouse.com/kanfangtuan/")||
				   urlTobeAdd.contains("http://www.hldhouse.com/houseonline/")||
				   urlTobeAdd.contains("http://www.hldhouse.com/newhouse/")||
				   urlTobeAdd.contains("http://www.hldhouse.com/tejiafang/")||
				   urlTobeAdd.contains("http://www.hldhouse.com/member/")||
				   urlTobeAdd.contains("http://www.hldhouse.com/need/")||
				   urlTobeAdd.contains("http://www.hldhouse.com//search")||
				   urlTobeAdd.contains("http://www.hldhouse.com/homedeco/")
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
