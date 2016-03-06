package com.meiah.webCrawlers.SitePlugin.Gxhs.finance;
import java.io.IOException;
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
import com.meiah.webCrawlers.PageCrawler;
import com.meiah.webCrawlers.PageResolver;
import com.meiah.webCrawlers.PluginFactory;


public class PageCrawler_Huafei118 extends PageCrawler {
	
	protected static Map<String,String> columnUrl = new HashMap<String,String>();
	
	public PageCrawler_Huafei118(TaskLink link, Task task) {
		super(link, task);
	}
	
	
	protected void mainPro() {
		for(int i=1;i<36;i++){
		columnUrl.put("http://www.huafei118.com/article/list.do?pageNum="+i+"&typeID=6501&kw=", "今日聚焦");
		columnUrl.put("http://www.huafei118.com/article/list.do?pageNum="+i+"&typeID=6505&kw=", "国内新闻");
		columnUrl.put("http://www.huafei118.com/article/list.do?pageNum="+i+"&typeID=6182&kw=", "行业动态");
		columnUrl.put("http://www.huafei118.com/article/list.do?pageNum="+i+"&typeID=6512&kw=", "市场评论");
		columnUrl.put("http://www.huafei118.com/article/list.do?pageNum="+i+"&typeID=6513&kw=", "统计数据");
		columnUrl.put("http://www.huafei118.com/article/list.do?pageNum="+i+"&typeID=6572&kw=", "政策法规");
		
		}
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
}
