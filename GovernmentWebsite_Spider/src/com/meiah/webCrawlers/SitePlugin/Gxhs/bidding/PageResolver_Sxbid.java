package com.meiah.webCrawlers.SitePlugin.Gxhs.bidding;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gxhs.mongodb.entry.news.NewsGeneric;
import com.meiah.dao.TaskDao;
import com.meiah.po.SiteConfig;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.po.WebPage;
import com.meiah.trs.NewsTrsDo;
import com.meiah.util.Config;
import com.meiah.util.JavaUtil;
import com.meiah.util.MD5Utils;
import com.meiah.util.SysConstants;
import com.meiah.util.SysObject;
import com.meiah.webCrawlers.SiteCrawler;
import com.meiah.webCrawlers.SitePlugin.PageResolver_General;

public class PageResolver_Sxbid extends PageResolver_General {
	
	public PageResolver_Sxbid(Task task) {
		super(task);
	}

	protected NewsGeneric resloveNewsPage(WebPage page) {
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		Document doc = null;
		try {
			doc = JavaUtil.getDocument(webContent);
		} catch (Exception e) {
			logger.error("解析新闻页面出现异常！" + link.getUrl());
			return null;
		}
		webContent = clearHtml(webContent);
		NewsGeneric ne = new NewsGeneric();
		ne.setTask_id(task.getTaskid());
		ne.setPage_url(link.getUrl());
		ne.set_id(MD5Utils.getMD5(ne.getPage_url().getBytes()));
		String fileName = "";
		if (Config.getIsSaveSnapShot() == 1) {
			fileName = savePageSnapShot(page);
		}
		if(StringUtils.isNotBlank(fileName))
			ne.setPage_snapshot(fileName);
//		ne.setIslist(SysConstants.PAGETYPE_CONTENT);
//		ne.setSpot_code(Config.getSpotID());
		String webdomain = ne.getPage_url();
		String domain = webdomain.substring(webdomain.indexOf("://") + 3);
		domain = domain.substring(0, domain.indexOf("/"));
		ne.setWebsite_domain(SiteCrawler.topDomain);
		String prefixUrl = link.getRefererUrl().substring(0,link.getRefererUrl().indexOf("://") + 3);
		ne.setWebsite_url(prefixUrl + domain + "/");
		ne.setPage_size(String.valueOf(webContent.getBytes().length));
		Map<String, Task> ts = TaskDao.getInstance().getAllTaskMap();
		String siteName = ((Task) ts.get(ne.getTask_id())).getTname().replaceAll("\r\n", "");
		ne.setWebsite_name(siteName);
		ne.setTask_name(siteName);
		String ipUrl = ((Task) ts.get(ne.getTask_id())).getUrl();
		String ipName = "";
		String ip = JavaUtil.matchWeak(ipUrl, "http://([^/]*)")[1];
		if (NewsTrsDo.ips == null)
			NewsTrsDo.ips = new HashMap<String, String>();
		if (NewsTrsDo.ips.containsKey(ip)) {
			ip = NewsTrsDo.ips.get(ip);
		} else {
			InetAddress a = null;
			try {
				a = InetAddress.getByName(ip);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			ip = a.getHostAddress();
			NewsTrsDo.ips.put(ip, a.getHostAddress());
		}
	    //ip地址所在地
		if(SysObject.ipTable.containsKey(ip)){
			ipName = SysObject.ipTable.get(ip);
		}else{
			ipName = NewsTrsDo.getAddressByIP(ip);
			if(StringUtils.isNotEmpty(ipName)){
				SysObject.ipTable.put(ip, ipName);
			}
		}
		ne.setWebsite_ip(ip);
		ne.setWebsite_ip_area(ipName);
		ne.setNews_class(task.getNewsType());
		
//		if (SiteCrawler.homePageUrls != null && SiteCrawler.homePageUrls.contains(ne.getUrl()))
//			ne.setIsHomePageNews(1);
		/** end default notChange * */
		String newsTitle = getNewsTitle(task.getSiteConfig(), page, doc);
		ne.setNews_title(newsTitle);
		if(StringUtils.isNotBlank(ne.getNews_title())) {
			String regex = "[\\u4E00-\\u9FA5]";
			String[] chinese = JavaUtil.match(ne.getNews_title(), regex);
			if (null == chinese || chinese.length < 0) {
				ne = null;
				return ne;
			}
		}
		long tStart = System.currentTimeMillis(), t2 = 0, t3 = 0, t4 = 0;
		String content = getNewsContent(task.getSiteConfig(), page, doc);
		
		if (logger.isDebugEnabled()) {
			t3 = System.currentTimeMillis() - tStart - t2;
			logger.debug("正文提取耗费时间：" + t3 + "ms");
		}
		content = content.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", "").trim();
		if(content.contains("您好，欢迎登录山西招投标网")||content.contains("发布日期：")){
			content = null ;
		}
		ne.setNews_content(content);
		Date dateDublished = getNewsPublishTime(task, page, doc);
		if(null != dateDublished)
			ne.setPage_publish_time(dateDublished);
		ne.setPage_save_time(new Date());
		
		InetAddress a = null;
		try {
			ne.setServer_ip(a.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (logger.isDebugEnabled()) {
			t4 = System.currentTimeMillis() - tStart - t2 - t3;
			logger.debug("时间提取耗费时间：" + t4 + "ms");
		}

		link.setLinkType(SysConstants.PAGETYPE_CONTENT);
		String newsSource = getNewsSource(task.getSiteConfig(), page, doc);
		if(newsSource.contains("资金")||newsSource.contains("筹")||newsSource.contains("投资")
		||newsSource.contains("贷款")||newsSource.contains("财政")||newsSource.contains("拨款")
		||newsSource.contains("维检")||newsSource.contains("费")||newsSource.contains("自有")){
			newsSource = null;
		}
		if(StringUtils.isNotBlank(newsSource))
			ne.setPage_source(newsSource);
		String newsAuthor = getNewsAuthor(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(newsAuthor))
			ne.setNews_author(StringUtils.replaceEach(newsAuthor, new String[] { "等", "撰写","作者：","作者:"," ","?" }, new String[] { "", "" ,"" , "","",""}));
		List<String> newsImage = getNewsImages(task.getSiteConfig(), page, doc,ne.getPage_url());
		if(!newsImage.isEmpty())
			ne.setPage_image_url(newsImage);
		//获取附件地址
		List<String> accessList = getNewsAccess(task.getSiteConfig(), page, doc, ne.getPage_url());
		if(!accessList.isEmpty()) 
			ne.setPage_acces(accessList);
		String columnName = getNewsColumnName(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(columnName))
			ne.setNews_column(columnName);
		String documentNo = getNewsDocumentNo(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(documentNo))
			ne.setNews_notice_code(documentNo);
		rtask.setContentPages(rtask.getContentPages() + 1);
		
		if(StringUtils.isEmpty(ne.getNews_title())) {
			ne = null;
			return ne;
		}
		if(StringUtils.isNotBlank(ne.getNews_title())) {
			String titles = ne.getNews_title();
			boolean titleFilter = titleKeyFilter.isContentKeyWords(ne.getNews_title());
			if(titleFilter || ne.getNews_title().length() < 5 
					|| ne.getNews_title().endsWith("下载") || ne.getNews_title().endsWith("解答") || ne.getNews_title().endsWith("须知")) {
				ne = null;
				return ne;
			}
		}
		if(StringUtils.isBlank(ne.getNews_content())) {
			if(newsImage.isEmpty() && accessList.isEmpty()) {
				ne = null;
			}
		} else {
			if(ne.getNews_content().length() < 100) {
				ne = null;
			}
		}
		return ne;
	}
	
	/***
	 * 获取新闻中附件
	 * @param conf
	 * @param page
	 * @param doc
	 * @param pageUrl 
	 * @return
	 */
	protected List<String> getNewsAccess(SiteConfig conf, WebPage page, Document doc, String pageUrl) {
		List<String> accesList = new ArrayList<String>();
		List<String> accesListUrl = new ArrayList<String>();
		try {
			String text = getLocText(conf.getImageUrlLocation(), page, doc);
			if(StringUtils.isNotBlank(text)){
				NodeList itemNodes = XPathAPI.selectNodeList(JavaUtil.getDocument(text), "//A");
				for (int i = 0; i < itemNodes.getLength(); i++){
					String accessUrl = "";
					Node node1 = itemNodes.item(i).getAttributes().getNamedItem("href");
					if(null != node1){
						accessUrl += completeImageUrl(node1.getNodeValue(),accessUrl,pageUrl);
					}
					if(StringUtils.isNotBlank(accessUrl))
						if (accessUrl.endsWith(".pdf") ||accessUrl.contains("Download.action")||accessUrl.contains("http://www.mwr.gov.cn/")
						||accessUrl.contains("http://www.ctba.org.cn/")
						|| accessUrl.endsWith(".docx") || accessUrl.endsWith(".doc") || accessUrl.endsWith(".xls") || accessUrl.endsWith(".xlsx"))
							accesList.add(accessUrl);
				}
			}
		} catch (Throwable e) {
			logger.error("抓取新闻附件链接出现异常: " + page.getLink().getUrl(), e);
		}
		for(String str:accesList)
	     {
	         if(!accesListUrl.contains(str))
	        	 accesListUrl.add(str);
	     }
		return accesListUrl;
	}
}
