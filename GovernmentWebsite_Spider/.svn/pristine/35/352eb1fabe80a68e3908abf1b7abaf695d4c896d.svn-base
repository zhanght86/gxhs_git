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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 中国战略文化促进会处理异步加载问题
 * @author dingrr
 * @date 2015-10-19
 */
public class PageCrawler_ForPeace extends PageCrawler {
	
	public static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	public PageCrawler_ForPeace(TaskLink link, Task task) {
		super(link, task);
	}
	protected void addLink(String webContent) {
		if (this.link.getLevel() < this.task.getMaxLevel()) {
			//过滤需要进行抓取的链接
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
	 * 获得url链接
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
					//处理异步加载，拼接异步加载新闻url
					try {
						if(urlTobeAdd.matches("http://www.for-peace.org.cn:80/toList/zh\\?fid=\\d+&mid=\\d+")) {
							String cateId = urlTobeAdd.substring(urlTobeAdd.lastIndexOf("=") + 1);
							String AsynUrl = "http://www.for-peace.org.cn/findContentList/zh?cateId=" + cateId + "&title=&start=0&limit=100";
							WebPageDownloader downloader = new WebPageDownloader(AsynUrl);
							String json = downloader.getPageContent();
							JSONObject obj = JSONObject.fromObject(json);
							JSONArray newsArray = obj.getJSONArray("rows");
							//解析json内容
							ArrayList<TaskLink> realUrlList = new ArrayList<TaskLink>();
							for (int j = 0; j < newsArray.size(); j++) {
								int id = Integer.parseInt(newsArray.getJSONObject(j).getString("id"));
								int type = Integer.parseInt(newsArray.getJSONObject(j).getString("type"));
								String realUrl = "http://www.for-peace.org.cn/goDetail/" + id + "/" + type + "/" + cateId + "/zh";
								TaskLink tl = new TaskLink();
								tl.setTaskid(task.getTaskid());
								tl.setLevel(link.getLevel() + 1);
								tl.setRefererUrl(link.getUrl());
								tl.setUrl(realUrl);
								
								realUrlList.add(tl);
							}
							getLink(realUrlList);
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
