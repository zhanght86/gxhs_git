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

public class PageResolver_Heiguang extends PageResolver_General {
	
	public PageResolver_Heiguang(Task task) {
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
		newsTitle = StringUtils.replaceEach(newsTitle, new String[] { "(?s)(?i)<.*?>","(?s)(?i)<.*?/>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;","\r\n","\t","\n"," " }, new String[] { "","","","","","","","","",""});
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
		content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>","(?s)(?i)<.*?/>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;",replace }, new String[] { "" , "", "" ,"" , "","",""});
		ne.setNews_content(content.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", "").trim());
		
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
		if(StringUtils.isNotBlank(newsSource))
			ne.setPage_source(newsSource.replace(" ", "").replace("来源", "").replace("来源：", "").replace("来源:", ""));
		String newsAuthor = getNewsAuthor(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(newsAuthor))
			ne.setNews_author(StringUtils.replaceEach(newsAuthor, new String[] { "等", "撰写","作者：","作者:"," ","?" ,"\r\n","作者"}, new String[] { "", "" ,"" , "","","","",""}));
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
	 * 获取新闻栏目名称
	 * @param count
	 * @return
	 */
	protected String getNewsColumnName(SiteConfig conf, WebPage page, Document doc) {
		String columnName = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String columnMC = page.getLink().getColumnName();
			if (columnMC != null && !columnMC.trim().equals(""))
				return columnMC;
			columnName = getLocText(conf.getColumnNameLocation(), page, doc);

			if (StringUtils.isEmpty(columnName)) {
				
				String regex = "(?s)(?i)(首页|位置：|位置:)(\\s*»\\s*|\\s*>>\\s*|\\s*>\\s*|\\s*->\\s*|\\s*\\|\\s*|\\s*/\\s*|\\s*—\\s*|\\s*→\\s*"
						+ "|\\s*[\\u4E00-\\u9FA5]+\\s*>>\\s*|\\s*[\\u4E00-\\u9FA5]+\\s*>\\s*|\\s*[\\u4E00-\\u9FA5]+\\s*-\\s*|\\s*[\\u4E00-\\u9FA5]+\\s*\\"
						+ "|\\s*\\s*)([\\u4E00-\\u9FA5]+[a-zA-Z]+|[\\u4E00-\\u9FA5]+)";
				webContent = webContent
						.replaceAll("(?is)<marquee.*?</marquee>", "")
						.replaceAll("(?s)(?i)<.*?>", "")
						.replaceAll("\\s+", " ").replaceAll("&nbsp;", "")
						.replaceAll("&gt;", ">").replaceAll("&#62;", ">")
						.replaceAll("&mdash;", "—")
						.replaceAll("&raquo;", ">").replaceAll("&gt", ">")
						.replaceAll("-&gt;&gt;", ">")
						.replaceAll("设为首页", "");
				String[] temp = JavaUtil.match(webContent, regex);
				if ((temp != null) && (temp.length >= 3))
					columnName = temp[3].replace("首页 >", "");
				
				if ((!StringUtils.isEmpty(columnName))
						&& ((columnName.contains("首页")) || (columnName
								.contains("更多")))) {
					regex = "(?s)(?i)(位置:\\s*|位置：\\s*)([\\u4E00-\\u9FA5]+)(\\s*>\\s*|\\s*>>)";
					webContent = webContent
							.replaceAll("(?is)<marquee.*?</marquee>", "")
							.replaceAll("(?s)(?i)<.*?>", "")
							.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
					temp = JavaUtil.match(webContent, regex);
					if ((temp != null) && (temp.length >= 2))
						columnName = temp[2];
				}
				if (StringUtils.isEmpty(columnName) || columnName.contains("我们是")) {
					regex = "(?s)(?i)(当前位置：\\s*|当前位置:\\s*|您的位置:\\s*|您的位置：\\s*|位置：首页/)([\\u4E00-\\u9FA5]+[a-zA-Z]+|[\\u4E00-\\u9FA5]+)";
					webContent = webContent
							.replaceAll("(?is)<marquee.*?</marquee>", "")
							.replaceAll("(?s)(?i)<.*?>", "")
							.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
					temp = JavaUtil.match(webContent, regex);
					if ((temp != null) && (temp.length >= 2))
						columnName = temp[2];
				}
				if(columnName.contains("网站地图") && webContent.contains(">")) {
					regex = "(?s)(?i)([\\u4E00-\\u9FA5]+\\s*>\\s+)([\\u4E00-\\u9FA5]+)";
					webContent = webContent
							.replaceAll("(?is)<marquee.*?</marquee>", "")
							.replaceAll("(?s)(?i)<.*?>", "")
							.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
					temp = JavaUtil.match(webContent, regex);
					if ((temp != null) && (temp.length >= 2))
						columnName = temp[2];
				}
				if(!StringUtils.isEmpty(columnName)) {
					boolean boo = keyFilter.isContentKeyWords(columnName);
					if(boo) {
						columnName = "";
					}
				}
			} 
			if(StringUtils.isNotBlank(columnName))
				columnName = columnName.replaceAll("&nbsp;", " ").replaceAll("(?s)(?i)<.*?>", "");
		} catch (Throwable e) {
			logger.error("抓取栏目名称出现异常: " + link.getUrl(), e);
			e.printStackTrace();
		}
		return columnName;
	}
	/**
	 * 提取新闻作者
	 * 
	 * @param beginAuthor
	 * @param endAuthor
	 * @param webContent
	 * @return
	 */
	protected String getNewsAuthor(SiteConfig conf, WebPage page, Document doc) {
		String newsAuthor = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String author = page.getLink().getAuthor();
			if (author != null && !author.trim().equals(""))
				return author;
			newsAuthor = getLocText(conf.getAuthorLocation(), page, doc);

			if (newsAuthor == null || newsAuthor.equals("")) {
				webContent = webContent
						.replaceAll("(?i)(?s)<marquee.*?</marquee>", "")
						.replaceAll("(?s)(?i)<.*?>", "")
						.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
				if (newsAuthor.equals("")) {
					String regex = "(?s)(?i)(作者：|作者:|作者\\s+|作者︰|作者 :|发布者\\s+|发布者:|\\d+\\s+\\|\\s+)([\\u4E00-\\u9FA5]{1}\\s+[\\u4E00-\\u9FA5]{1}|\\s*[\\u4E00-\\u9FA5]+\\s*|\\s*\\[[\\u4E00-\\u9FA5]+\\])";
					String[] results = JavaUtil.match(webContent, regex);
					if ((results != null) && (results.length > 2))
						newsAuthor = results[2];
					if(newsAuthor.equals("")) {
						regex = "(?s)(?i)([\\u4E00-\\u9FA5]{1}\\s+[\\u4E00-\\u9FA5]{1}|[\\u4E00-\\u9FA5]{2,4})(\\d{4}年\\s+)";
						results = JavaUtil.match(webContent, regex);
						if ((results != null) && (results.length > 1))
							newsAuthor = results[1];
					}
					if(newsAuthor.equals("")) {
						regex = "(?s)(?i)(作者：|作者:|作者\\s+|作者︰|作者 :|編輯\\s+|編輯：|編輯:|编辑：|编辑:|编辑\\s+|\\（记者：|\\（记者:|\\(记者：|\\(记者:|发布者：|发布者\\s+|发布者:|\\d+\\s+\\|\\s+)([\\u4E00-\\u9FA5]+\\s+[\\u4E00-\\u9FA5]+{2,4})";
						results = JavaUtil.match(webContent, regex);
						if ((results != null) && (results.length > 2))
							newsAuthor = results[2];
					}
				}
				String single = "";
				String multi = "";
				if(!StringUtils.isEmpty(newsAuthor)) {
					single = newsAuthor.substring(0,1);
					if(newsAuthor.length() >= 2) {
						multi = newsAuthor.substring(0,2);
					}
					boolean isSingle = this.baijiaS.isContentKeyWords(single);
					boolean isMulti = this.baijiaM.isContentKeyWords(multi);
					boolean boo = this.keyFilter.isContentKeyWords(newsAuthor);
					if(newsAuthor.length() > 4) {
						newsAuthor = "";
					} else {
						if(!isSingle && !isMulti) {
							newsAuthor = "";
						} else {
							if(boo) {
								newsAuthor = "";
							}
						}
					}
					if (newsAuthor.contains("报道")) {
						newsAuthor = newsAuthor.replaceAll("报道", "");
					}
					if(newsAuthor.length() <= 1) {
						newsAuthor = "";
					}
				}
			}
			if (newsAuthor != null)
				newsAuthor = newsAuthor.replaceAll("&nbsp;", " ").replaceAll(
						"(?s)(?i)<.*?>", "");
		} catch (Throwable e) {
			logger.error("抓取新闻作者出现异常: " + link.getUrl(), e);
		}
		return newsAuthor;
	}
}
