package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

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
import com.meiah.htmlParser.ContentExtractor;
import com.meiah.po.SiteConfig;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.po.WebPage;
import com.meiah.trs.NewsTrsDo;
import com.meiah.util.Config;
import com.meiah.util.JavaUtil;
import com.meiah.util.MD5Utils;
import com.meiah.util.SysObject;
import com.meiah.util.WebPageDownloader;
import com.meiah.webCrawlers.SiteCrawler;
import com.meiah.webCrawlers.SitePlugin.PageResolver_General;

/**
 * 获取正确的作者信息以及正文图片链接地址
 * @author dingrr
 * @date 2015-10-9
 */
public class PageResolver_ThinkTank_cssn extends
		PageResolver_General {

	public PageResolver_ThinkTank_cssn(Task task) {
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
		/** begin default notChange * */
		NewsGeneric ne = new NewsGeneric();
		ne.setTask_id(task.getTaskid());
		
		ne.setPage_url(link.getUrl());
		ne.set_id(MD5Utils.getMD5(ne.getPage_url().getBytes()));
		String fileName = "";
		if (Config.getIsSaveSnapShot() == 1)
			fileName = savePageSnapShot(page);
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
		String siteName = ((Task) ts.get(ne.getTask_id())).getTname();
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
		long tStart = System.currentTimeMillis(), t2 = 0, t3 = 0, t4 = 0;
		String content = getNewsContent(task.getSiteConfig(), page, doc);
		if (logger.isDebugEnabled()) {
			t3 = System.currentTimeMillis() - tStart - t2;
			logger.debug("正文提取耗费时间：" + t3 + "ms");
		}
		String replace = "";
		if(content.contains("<?xml:namespace")) {
			replace = content.substring(content.indexOf("<?xml:namespace"));
			replace = replace.substring(0, replace.indexOf("/>") + 2);
		}
		content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;",replace }, new String[] { "", "" ,"" , "","",""});
		ne.setNews_content(content.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", ""));
		Date dateDublished = getNewsPublishTime(task, page, doc);
		if(null != dateDublished)
			ne.setPage_publish_time(dateDublished);
		ne.setPage_save_time(new Date());
		if (logger.isDebugEnabled()) {
			t4 = System.currentTimeMillis() - tStart - t2 - t3;
			logger.debug("时间提取耗费时间：" + t4 + "ms");
		}
		InetAddress a = null;
		try {
			ne.setServer_ip(a.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		/** 如果有正文中有翻页需要取出链接继续进行采集其它页的内容 */
		StringBuffer pageTurnContent = new StringBuffer();
		int alreadyPageTurn = 0;// 0表示正文没有翻页
		if (content.contains("createPageHTML")) {
			String pageNo = content.substring(content.indexOf("createPageHTML"));
			pageNo = pageNo.substring(15,pageNo.indexOf(","));
			if (null != pageNo && !pageNo.equals("") && !pageNo.equals("1")) {
				
				/*把正文中的createPageHTML(3, 0, \"t20151009_67430\",\"html\")删除*/
				if (content.contains("createPageHTML")) {
					String createPageHtml = content.substring(content.indexOf("createPageHTML"));
					content = content.replace(createPageHtml , "");
				}
				alreadyPageTurn = 1;//1表示正文有翻页
				int pageNumber = Integer.parseInt(pageNo);
				String aHrefLink = ne.getPage_url().substring(0,ne.getPage_url().lastIndexOf(".shtml"));
				try {
					for (int i = 1; i < pageNumber; i++) {
						aHrefLink = aHrefLink + "_" + i + ".shtml";
						if(null == pageTurnContent || !pageTurnContent.equals("")) {
							pageTurnContent = pageTurnContent.append(content);// 追加前一页的正文
						} 
						WebPageDownloader wd = new WebPageDownloader(aHrefLink);
						String pageContent = wd.getPageContent();
						WebPage webPage = new WebPage();
						webPage.setWebContent(pageContent);
						String turnContent = getNewsContent(task.getSiteConfig(), webPage, JavaUtil.getDocument(webPage.getWebContent()));
						if (turnContent.contains("createPageHTML")) {
							String createPageHtml = turnContent.substring(turnContent.indexOf("createPageHTML"));
							turnContent = turnContent.replace(createPageHtml , "");
						}
						pageTurnContent.append(turnContent);
						aHrefLink = ne.getPage_url().substring(0,ne.getPage_url().lastIndexOf(".shtml"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 给新闻内容set值时需要判断是否有分页内容，从而判断赋哪个值
		if (alreadyPageTurn == 0) {// 0代表没有分页
			//把正文中的createPageHTML分页信息删除
			if(content.contains("createPageHTML")) {
				String createPageHtml = content.substring(content.indexOf("createPageHTML"));
				content = content.replace(createPageHtml , "");
			}

			content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;",replace }, new String[] { "", "" ,"" , "","",""});
			ne.setNews_content(content.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", ""));
		} else if (alreadyPageTurn == 1) {// 1代表有分页
			//把正文中的createPageHTML分页信息删除
			String turnPageConent = pageTurnContent.toString();
			if (turnPageConent.contains("createPageHTML")) {
				String createPageHtml = turnPageConent.substring(turnPageConent.indexOf("createPageHTML"));
				turnPageConent = turnPageConent.replace(createPageHtml , "");
			}
			turnPageConent = turnPageConent.replaceAll("(?is)<(no)?script.*?((/>)|(</(no)?script>))","").replaceAll("(?is)<.*?>", "").replaceAll("//", "").replace(replace, "");
			ne.setNews_content(turnPageConent.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", ""));
		}
		
		String newsSource = getNewsSource(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(newsSource))
			ne.setPage_source(newsSource);
		String newsAuthor = getNewsAuthor(task.getSiteConfig(), page, doc);
		//获取正确的作者信息
		if(newsAuthor.contains("作者：")) {
			newsAuthor = newsAuthor.substring(newsAuthor.indexOf("作者："),newsAuthor.indexOf("字号") - 1);
			newsAuthor = newsAuthor.replace("作者：", "");
		} else {
			newsAuthor = "";
		}
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
			boolean titleFilter = titleKeyFilter.isContentKeyWords(ne.getNews_title());
			if(titleFilter || StringUtils.isEmpty(ne.getNews_content()) || ne.getNews_title().length() < 5) {
				ne = null;
				return ne;
			}
		}
		if(StringUtils.isNotBlank(ne.getNews_content())) {
			if(ne.getNews_content().length() < 100) {
				ne = null;
			}
		}
		return ne;

	}

	/**
	 * 提取新闻正文
	 * 
	 * @param beginTitle
	 * @param endTitle
	 * @param webContent
	 * @return
	 */
	protected String getNewsContent(SiteConfig conf, WebPage page, Document doc) {
		String content = "";
		try {
			String webContent = page.getWebContent();
			TaskLink link = page.getLink();
			content = getLocText(conf.getContentLocation(), page, doc);
			// logger.info(content);
			if (content != null) {
				content = content.replaceAll("(?is)<marquee.*?</marquee>", "");
				content = content.replaceAll("&gt;?", ">").replaceAll("&lt;?", "<");
//				content = content.replaceAll("(?is)<style.*?</style>", "").replaceAll("(?is)<(no)?script.*?((/>)|(</(no)?script>))","")
				content = content.replaceAll("(?is)<style.*?</style>", "").replaceAll("(?is)<select.*?</select>", "").replaceAll("(?is)<!--.*?-->", "");
				content = content.replaceAll("(?is)</?p( .*?)?>", "　　");
				content = content.replaceAll("(?is)</?br/?>", "　　");
//				content = content.replaceAll("(?is)<.*?>", "");
				content = content.replaceAll("&nbsp;?", " ").replaceAll("&#149;?", "").replaceAll("&quot;?", "\"").replaceAll("[\\s]+", " ");
			}

			if (content == null || content.equals("") || content.matches("[\\s　]+")) {
				webContent = clearHtml(webContent);
				try {
					content = ContentExtractor.extractMainContent("", "", webContent);
				} catch (Exception e) {
					logger.error("抓取正文出现异常，链接地址: " + link.getUrl(), e);
				}
			}
		} catch (Exception e) {
			logger.error("提取正文出现异常！", e);
		}

		return content;
	}
}
