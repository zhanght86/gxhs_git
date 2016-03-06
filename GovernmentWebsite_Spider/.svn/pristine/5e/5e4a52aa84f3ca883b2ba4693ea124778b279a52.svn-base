package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import com.gxhs.mongodb.entry.news.NewsGeneric;
import com.meiah.dao.TaskDao;
import com.meiah.htmlParser.ContentExtractor;
import com.meiah.htmlParser.PublishTimeExtractor;
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

/**
 * 北京大学国家发展研究院
 * @author lyao
 * @date 2015-10-10
 */
public class PageResolver_Ciis extends PageResolver_General{

	public PageResolver_Ciis(Task task) {
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
		String siteName = ((Task) ts.get(ne.getTask_id())).getTname();
		ne.setWebsite_name(siteName);
		ne.setTask_name(siteName);
		String ipUrl = ((Task) ts.get(ne.getTask_id())).getUrl();
		String ipName = "";
		String ip = JavaUtil.matchWeak(ipUrl, "http://([^/]*)")[1];
		InetAddress a = null;
		try {
			a = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		ip = a.getHostAddress();
		NewsTrsDo.ips.put(ip, a.getHostAddress());
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
		if(content.contains("<?xml:namespace")) {
			replace = content.substring(content.indexOf("<?xml:namespace"));
			replace = replace.substring(0, replace.indexOf("/>") + 2);
		}
		content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;",replace }, new String[] { "", "" ,"" , "","",""});
		ne.setNews_content(content.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", "").trim());
		
		Date dateDublished = getNewsPublishTime(task, page, doc);
		if(null != dateDublished)
			ne.setPage_publish_time(dateDublished);
		ne.setPage_save_time(new Date());
		
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
		String newsSource = "";
		if(StringUtils.isNotBlank(newsSource))
			ne.setPage_source(newsSource);
		String newsAuthor = getNewsAuthor(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(newsAuthor)){
			if(newsAuthor.contains("作者")) {
				newsAuthor = newsAuthor.substring(newsAuthor.indexOf("作者"));
				if(newsAuthor.contains("责编")) {
					newsAuthor = newsAuthor.substring(0,newsAuthor.indexOf("责编"));
				}
				ne.setNews_author(StringUtils.replaceEach(newsAuthor, new String[] { "等", "撰写","作者：","作者:"," ","?" }, new String[] { "", "" ,"" , "","",""}));
			}else {
				newsAuthor = "";
			}
		}
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
	@Override
	protected Date getNewsPublishTime(Task task, WebPage page, Document doc1) {
		Date publishTime = null;
		String content = StringUtils.EMPTY;
		try {
			Date linktime = page.getLink().getPublishTime();
			if (linktime != null)
				return linktime;
			content = getLocText(task.getSiteConfig().getPublishTimeLocation(), page, doc1);
			// logger.info(content);
			if (StringUtils.isNotBlank(content) && !content.replaceAll("\\s+", "").equals("")) {
				
				int endPos = StringUtils.lastIndexOfIgnoreCase(content, "]");
				if (endPos >= 0) {
					content = StringUtils.substring(content, 0, endPos);
					content = StringUtils.replace(content, "[", "");
				}
				content = " " + content + " 00:00:00";
				PublishTimeExtractor te = new PublishTimeExtractor();
				publishTime = te.getNewsPublishedDate(page.getLink().getUrl(), content, task.getDateFormat(), task.getDatePos());
			}
			if (content == null || content.equals("")) {
				PublishTimeExtractor te = new PublishTimeExtractor();
				String webContent = clearHtml(page.getWebContent());
				publishTime = te.getNewsPublishedDate(page.getLink().getUrl(), webContent, task.getDateFormat(), task.getDatePos());
			}
		} catch (Exception e) {
			logger.error("提取新闻发布日期出现异常！", e);
		}
		return publishTime;
	}

	@Override
	protected String getNewsAuthor(SiteConfig conf, WebPage page, Document doc) {
		String newsAuthor = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String author = page.getLink().getAuthor();
			if (author != null && !author.trim().equals(""))
				return author;

			if (StringUtils.indexOfIgnoreCase(link.getUrl(), "http://news.takungpao.com/") >= 0) {
				
				String regex = "文(\\||/).{2,5}</";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(webContent);
				
				if (m.find()) {
					newsAuthor = m.group();
					newsAuthor = newsAuthor.replaceAll("文\\|", "").replaceAll("文/", "").replaceAll("</", "");
				}
			} else {
				String regex = "作者: .+责编:";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(webContent);
				
				if (m.find()) {
					newsAuthor = m.group();
					newsAuthor = newsAuthor.replaceAll("作者: ", "").replaceAll("责编:", "");
				}
			}
			if (StringUtils.isBlank(newsAuthor)) {
				newsAuthor = getLocText(conf.getAuthorLocation(), page, doc);
			}
			if (newsAuthor != null)
				newsAuthor = newsAuthor.replaceAll("&nbsp;", " ").replaceAll(
						"(?s)(?i)<.*?>", "");
		} catch (Throwable e) {
			logger.error("抓取新闻作者出现异常: " + link.getUrl(), e);
		}
		return newsAuthor;
	}

	@Override
	protected String getNewsContent(SiteConfig conf, WebPage page, Document doc) {
		String content = "";
		try {
			String webContent = page.getWebContent();
			TaskLink link = page.getLink();
			
			if (StringUtils.indexOfIgnoreCase(link.getUrl(), "http://news.takungpao.com/") >= 0) {
				content = getLocText("XPATH://DIV[@class=\"tpk_text clearfix\"]", page, doc);
			} else {
				content = getLocText(conf.getContentLocation(), page, doc);
			}
			
			// logger.info(content);
			if (content != null) {
				content = content.replaceAll("(?is)<marquee.*?</marquee>", "");
				content = content.replaceAll("&gt;?", ">").replaceAll("&lt;?", "<");
//				content = content.replaceAll("(?is)<style.*?</style>", "").replaceAll("(?is)<(no)?script.*?((/>)|(</(no)?script>))","")
				content = content.replaceAll("(?is)<style.*?</style>", "").replaceAll("(?is)<(no)?script.*?((</(no)?script>))","")
						.replaceAll("(?is)<select.*?</select>", "").replaceAll("(?is)<!--.*?-->", "");

				content = content.replaceAll("(?is)</?p( .*?)?>", "　　");
				content = content.replaceAll("(?is)</?br/?>", "　　");
				content = content.replaceAll("(?is)<.*?>", "");
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
