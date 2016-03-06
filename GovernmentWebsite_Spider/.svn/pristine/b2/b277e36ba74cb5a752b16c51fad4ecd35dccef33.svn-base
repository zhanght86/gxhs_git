package com.meiah.webCrawlers.SitePlugin.Gxhs.finance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.gxhs.mongodb.entry.news.NewsGeneric;
import com.meiah.dao.TaskDao;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.po.WebPage;
import com.meiah.trs.NewsTrsDo;
import com.meiah.util.Config;
import com.meiah.util.JavaUtil;
import com.meiah.util.MD5Utils;
import com.meiah.util.SysConstants;
import com.meiah.util.SysObject;
import com.meiah.util.WebPageDownloader;
import com.meiah.webCrawlers.SiteCrawler;
import com.meiah.webCrawlers.SitePlugin.PageResolver_General;

public class PageResolver_Cxjrw extends PageResolver_General {
	protected Logger logger = Logger.getLogger(PageResolver_General.class);

	public PageResolver_Cxjrw(Task task) {
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
		System.out.println(webContent);
		NewsGeneric ne = new NewsGeneric();
		ne.setTask_id(task.getTaskid());
		ne.setPage_url(link.getUrl());
		ne.set_id(MD5Utils.getMD5(ne.getPage_url().getBytes()));
		String fileName = "";
		if (Config.getIsSaveSnapShot() == 1) {
			fileName = savePageSnapShot(page);
		}
		if (StringUtils.isNotBlank(fileName))
			ne.setPage_snapshot(fileName);
		// ne.setIslist(SysConstants.PAGETYPE_CONTENT);
		// ne.setSpot_code(Config.getSpotID());
		String webdomain = ne.getPage_url();
		String domain = webdomain.substring(webdomain.indexOf("://") + 3);
		domain = domain.substring(0, domain.indexOf("/"));
		ne.setWebsite_domain(SiteCrawler.topDomain);
		String prefixUrl = link.getRefererUrl().substring(0, link.getRefererUrl().indexOf("://") + 3);
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
		// ip地址所在地
		if (SysObject.ipTable.containsKey(ip)) {
			ipName = SysObject.ipTable.get(ip);
		} else {
			ipName = NewsTrsDo.getAddressByIP(ip);
			if (StringUtils.isNotEmpty(ipName)) {
				SysObject.ipTable.put(ip, ipName);
			}
		}
		ne.setWebsite_ip(ip);
		ne.setWebsite_ip_area(ipName);
		ne.setNews_class(task.getNewsType());

		// if (SiteCrawler.homePageUrls != null &&
		// SiteCrawler.homePageUrls.contains(ne.getUrl()))
		// ne.setIsHomePageNews(1);
		/** end default notChange * */
		String newsTitle = getNewsTitle(task.getSiteConfig(), page, doc);
		ne.setNews_title(newsTitle);
		if (StringUtils.isNotBlank(ne.getNews_title())) {
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

		/** 如果有正文中有翻页需要取出链接继续进行采集其它页的内容 */
		StringBuffer pageTurnContent = new StringBuffer();
		int alreadyPageTurn = 0;// 0表示正文没有翻页
		if (content.contains("createPageHTML")) {
			String pageNo = content.substring(content.indexOf("createPageHTML"));
			pageNo = pageNo.substring(15, pageNo.indexOf(","));
			if (null != pageNo && !pageNo.equals("") && !pageNo.equals("1")) {

				/* 把正文中的createPageHTML(3, 0, \"t20151009_67430\",\"html\")删除 */
				if (content.contains("createPageHTML")) {
					String createPageHtml = content.substring(content.indexOf("createPageHTML"));
					content = content.replace(createPageHtml, "");
				}
				alreadyPageTurn = 1;// 1表示正文有翻页
				int pageNumber = Integer.parseInt(pageNo);
				String aHrefLink = ne.getPage_url().substring(0, ne.getPage_url().lastIndexOf(".html"));
				try {
					for (int i = 1; i < pageNumber; i++) {
						aHrefLink = aHrefLink + "_" + i + ".html";
						// System.out.println("分页链接：" + aHrefLink);
						if (null == pageTurnContent || !pageTurnContent.equals("")) {
							pageTurnContent = pageTurnContent.append(content);// 追加前一页的正文
						}
						WebPageDownloader wd = new WebPageDownloader(aHrefLink);
						String pageContent = wd.getPageContent();
						WebPage webPage = new WebPage();
						webPage.setWebContent(pageContent);
						String turnContent = getNewsContent(task.getSiteConfig(), webPage,
								JavaUtil.getDocument(webPage.getWebContent()));
						if (turnContent.contains("createPageHTML")) {
							String createPageHtml = turnContent.substring(turnContent.indexOf("createPageHTML"));
							turnContent = turnContent.replace(createPageHtml, "");
						}
						pageTurnContent.append(turnContent);
						aHrefLink = ne.getPage_url().substring(0, ne.getPage_url().lastIndexOf(".html"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 给新闻内容set值时需要判断是否有分页内容，从而判断赋哪个值
		if (alreadyPageTurn == 0) {// 0代表没有分页
			// 把正文中的createPageHTML分页信息删除
			if (content.contains("createPageHTML")) {
				String createPageHtml = content.substring(content.indexOf("createPageHTML"));
				content = content.replace(createPageHtml, "");
			}
			content = content.replaceAll("(?is)<(no)?script.*?((/>)|(</(no)?script>))", "");
		} else if (alreadyPageTurn == 1) {// 1代表有分页
			// 把正文中的createPageHTML分页信息删除
			String turnPageConent = pageTurnContent.toString();
			if (turnPageConent.contains("createPageHTML")) {
				String createPageHtml = turnPageConent.substring(turnPageConent.indexOf("createPageHTML"));
				turnPageConent = turnPageConent.replace(createPageHtml, "");
			}
			turnPageConent = turnPageConent.replaceAll("(?is)<(no)?script.*?((/>)|(</(no)?script>))", "");
			ne.setNews_content(turnPageConent);
		}
		ne.setNews_content(
				content.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("&ldquo", "")
						.replaceAll("&rdquo", "").replaceAll("&hellip", "").replaceAll("&middot", "").trim());
		if(content.contains("您还没有登录，请登录后查看联系方式免费注册为会员后，")||content.contains("目前理财计划预期投资收益率为")){
			ne = null;
		}
		Date dateDublished = getNewsPublishTime(task, page, doc);
		if (null != dateDublished)
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
		if (StringUtils.isNotBlank(newsSource)){
			if(newsSource.contains("来源") && newsSource.contains("浏览")) {
				newsSource = newsSource.substring(newsSource.indexOf("：") + 1 , newsSource.indexOf("浏览"));
			} 
			ne.setPage_source(newsSource);
		}
		String newsAuthor = getNewsAuthor(task.getSiteConfig(), page, doc);
		if (StringUtils.isNotBlank(newsAuthor)){
			ne.setNews_author( StringUtils.replaceEach(newsAuthor, new String[] { "等", "撰写", "作者：", "作者:", " ", "?" },
					new String[] { "", "", "", "", "", "" }));
		}
		List<String> newsImage = getNewsImages(task.getSiteConfig(), page, doc, ne.getPage_url());
		if (!newsImage.isEmpty())
			ne.setPage_image_url(newsImage);
		// 获取附件地址
		List<String> accessList = getNewsAccess(task.getSiteConfig(), page, doc, ne.getPage_url());
		if (!accessList.isEmpty())
			ne.setPage_acces(accessList);
		String columnName = getNewsColumnName(task.getSiteConfig(), page, doc);
		if (StringUtils.isNotBlank(columnName))
			ne.setNews_column(columnName);
		String documentNo = getNewsDocumentNo(task.getSiteConfig(), page, doc);
		if (StringUtils.isNotBlank(documentNo))
			ne.setNews_notice_code(documentNo);
		rtask.setContentPages(rtask.getContentPages() + 1);

		if (StringUtils.isEmpty(ne.getNews_title())) {
			ne = null;
			return ne;
		}
		if (StringUtils.isNotBlank(ne.getNews_title())) {
			boolean titleFilter = titleKeyFilter.isContentKeyWords(ne.getNews_title());
			if (titleFilter || StringUtils.isEmpty(ne.getNews_content()) || ne.getNews_title().length() < 5) {
				ne = null;
			}
		} else if (StringUtils.isNotBlank(ne.getNews_content())) {
			if (ne.getNews_content().length() < 100) {
				ne = null;
			}
		}
		return ne;
	}
}
