package com.meiah.webCrawlers.SitePlugin.Gxhs.finance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

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

public class PageResolver_Hxahv extends PageResolver_General {
	
	public PageResolver_Hxahv(Task task) {
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
		String replace = "";
		if(content.contains("/>")) {
			replace = content.substring(content.indexOf("/>"));
			replace = replace.substring(0, replace.indexOf("/>") + 2);
		}
		content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>","(?s)(?i)<.*?/>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;",replace }, new String[] { "" , "", "" ,"" , "","",""});
		content = content.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", "").trim();
//		if(content.contains("请自觉遵守互联网相关的政策法规")||content.contains("上一页")||content.contains("下一页")){
//			content = null;
//		}
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
		if(newsSource.contains("资金")||newsSource.contains("自筹")||newsSource.contains("投资")
		||newsSource.contains("贷款")||newsSource.contains("财政")||newsSource.contains("拨款")
		||newsSource.contains("维检")||newsSource.contains("费")||newsSource.contains("自有")){
			newsSource = null;
		}
		if(StringUtils.isNotBlank(newsSource)){
			if(newsSource.contains("来源:")&&newsSource.contains("编辑:")){
				newsSource = newsSource.substring(newsSource.indexOf("来源:")+3, newsSource.indexOf("编辑:"));
			}
			ne.setPage_source(newsSource.replace("未知 ", ""));
		}
		String newsAuthor = getNewsAuthor(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(newsAuthor)){
			if(newsAuthor.contains("编辑:")&&newsAuthor.contains("点击:")){
				newsAuthor = newsAuthor.substring(newsAuthor.indexOf("编辑:")+3, newsAuthor.indexOf("点击:")).trim();
			}
			ne.setNews_author(StringUtils.replaceEach(newsAuthor, new String[] { "等", "撰写","作者：","作者:"," ","?","编辑","客服"}, new String[] { "","","", "" ,"" , "","",""}).trim());
		}
		List<String> newsImage = getNewsImages(task.getSiteConfig(), page, doc,ne.getPage_url());
		if(!newsImage.isEmpty())
			ne.setPage_image_url(newsImage);
		//获取附件地址
		List<String> accessList = getNewsAccess(task.getSiteConfig(), page, doc, ne.getPage_url());
		if(!accessList.isEmpty()) 
			ne.setPage_acces(accessList);
		String columnName = getNewsColumnName(task.getSiteConfig(), page, doc);
		if(columnName.equals("人才频道")||columnName.equals("会议展会")||columnName.contains("视频资讯")||columnName.contains("价格行情")){
			ne = null;
			return ne ;
		}
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
			boolean titleFilter = titleKeyFilter.isContentKeyWords(ne.getNews_title());
			if(titleFilter || ne.getNews_title().length() < 5) {
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
	
	/**
	 * 提取新闻来源
	 * 
	 * @param beginSource
	 * @param endSource
	 * @param webContent
	 * @return
	 */
	protected String getNewsSource(SiteConfig conf, WebPage page, Document doc) {
		String newsSource = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String souce = page.getLink().getSource();
			if (souce != null && !souce.trim().equals(""))
				return souce;
			newsSource = getLocText(conf.getSourceSiteLocation(), page, doc);
			newsSource = newsSource.replaceAll("&nbsp;", " ").replaceAll(
					"(?s)(?i)<.*?>", "");
			if (newsSource == null || newsSource.equals("")) {
				String regex = "(?s)(?i)(来源\\s+|來源：|来源：|來源:|来源:|来自:)([\\u4E00-\\u9FA5]+[0-9]+|[\\u4E00-\\u9FA5]+-[\\u4E00-\\u9FA5]+|[\\u4E00-\\u9FA5]+)";
				webContent = webContent
						.replaceAll("(?i)(?s)<marquee.*?</marquee>", "")
						.replaceAll("(?s)(?i)<.*?>", " ")
						.replaceAll("\\s+", " ");
				String[] results = JavaUtil.match(webContent, regex);
				if ((results != null) && (results.length > 2)) {
					newsSource = results[2];
				}
			}
			if (newsSource != null)
				newsSource = newsSource.replaceAll("&nbsp;", " ").replaceAll(
						"(?s)(?i)<.*?>", "");
		} catch (Throwable e) {
			logger.error("抓取新闻来源出现异常: " + link.getUrl(), e);
		}
		if(newsSource.equals("作者") || newsSource.equals(" 作者") || newsSource.contains("人气") || newsSource.contains("日期")
				|| newsSource.length() <= 1 || newsSource.contains("其它")
				|| newsSource.contains("分享") || newsSource.contains("其他"))
			newsSource = "";
		return newsSource;
	}
}
