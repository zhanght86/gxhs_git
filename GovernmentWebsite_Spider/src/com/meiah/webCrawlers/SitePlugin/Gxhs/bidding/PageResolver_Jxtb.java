package com.meiah.webCrawlers.SitePlugin.Gxhs.bidding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import com.gxhs.mongodb.entry.news.NewsGeneric;
import com.ibm.icu.text.SimpleDateFormat;
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


public class PageResolver_Jxtb extends PageResolver_General {
	
	
	public PageResolver_Jxtb(Task task) {
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
		if(content.contains("上一篇")) {
			content = content.substring(0, content.indexOf("上一篇"));
		}
		if (logger.isDebugEnabled()) {
			t3 = System.currentTimeMillis() - tStart - t2;
			logger.debug("正文提取耗费时间：" + t3 + "ms");
		}
		content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>","(?s)(?i)<.*?/>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;","你好，欢迎光临！"}, new String[] { "" , "", "" ,"" , "","",""});
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
		if(StringUtils.isNotBlank(newsSource) && newsSource.length() < 15) {
			ne.setPage_source(newsSource.replace(" ", "").replace("来源", "").replace("来源：", "").replace("来源:", ""));
		}
		String newsAuthor = getNewsAuthor(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(newsAuthor))
			ne.setNews_author(StringUtils.replaceEach(newsAuthor, new String[] { "等", "撰写","作者：","作者:"," ","?" ,"\r\n","作者"}, new String[] { "", "" ,"" , "","","","",""}));
		List<String> newsImage = getNewsImages(task.getSiteConfig(), page, doc,ne.getPage_url());
		if(!newsImage.isEmpty()){
			if(newsImage.contains("http://ztb.hainan.gov.cn/manager/images/file/unknow.gif")) 
				newsImage.remove("http://ztb.hainan.gov.cn/manager/images/file/unknow.gif");
			if(newsImage.contains("http://ztb.hainan.gov.cn/manager/images/file/doc.gif")) 
				newsImage.remove("http://ztb.hainan.gov.cn/manager/images/file/doc.gif");
			if(!newsImage.isEmpty())
				ne.setPage_image_url(newsImage);
		}
			
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
			if(ne.getNews_content().length() < 30) {
				ne = null;
			}
		}
		return ne;
	}
	/**
	 * 获取新闻文号
	 * @param count
	 * @return
	 */
	protected String getNewsDocumentNo(SiteConfig conf, WebPage page, Document doc) {
		String documentNo = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String documentNumber = page.getLink().getDocumentName();
			if (documentNumber != null && !documentNumber.trim().equals(""))
				return documentNumber;
			documentNo = getLocText(conf.getDocumentLocation(), page, doc);
			
			if (documentNo == null || documentNo.equals("")) {
				webContent = webContent
						.replaceAll("(?is)<marquee.*?</marquee>", "")
						.replaceAll("(?s)(?i)<.*?>", "")
						.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
				String regex = "(?s)(?i)(招标编号：)(\\s*[\\u4E00-\\u9FA5]+\\[\\d+\\]\\d+号|[A-Z]-\\d{4}-\\d+)";
				String[] temp = JavaUtil.match(webContent, regex);
				if ((temp != null) && (temp.length > 2)) {
					documentNo = temp[2];
				}
			}
			if(StringUtils.isBlank(documentNo)) {
				String regex = "(?s)(?i)发文字号：\\s*[\\u4E00-\\u9FA5]+\\[\\d+\\].*?号|\\s+[\\u4E00-\\u9FA5]+\\[\\d{4}\\]\\d+号|\\d{4}年\\d{1,2}月\\d{1,2}日[\\u4E00-\\u9FA5]+第\\d+号|\\d{4}年第\\d+号|\\d{4}年[\\u4E00-\\u9FA5]+第\\d+号|\\s+[\\u4E00-\\u9FA5]+〔[0-9]+〕[0-9]+号|\\s+[\\u4E00-\\u9FA5]+第[0-9]+号|[\\u4E00-\\u9FA5]+\\[\\d+\\]号|\\s+[\\u4E00-\\u9FA5]+\\［\\d+\\］第\\d+号|\\s+[\\u4E00-\\u9FA5]+〔2015〕\\d+号";
				String[] temp = JavaUtil.match(webContent, regex);
				if ((temp != null) && (temp.length > 0)) {
					documentNo = temp[0];
				}
			}
			if (documentNo != null) {
				if(documentNo.length() > 22) {
					documentNo = "";
				}
				if(documentNo.contains("根据")) {
					documentNo.substring(documentNo.indexOf("根据"));
				} else if(documentNo.contains("按照")) {
					documentNo.substring(documentNo.indexOf("按照"));
				} else if(documentNo.contains("以")) {
					documentNo.substring(documentNo.indexOf("以"));
				} else if(documentNo.contains("依据")) {
					documentNo.substring(documentNo.indexOf("依据"));
				} else if(documentNo.contains("已由")) {
					documentNo.substring(documentNo.indexOf("已由"));
				}
				documentNo = documentNo.replaceAll("&nbsp;", " ").replaceAll(
						"(?s)(?i)<.*?>", "").replaceAll("发文字号：", "").replaceAll("	", "")
						.replaceAll("：", "").replace("按照", "").replace("以", "")
						.replace("根据", "").replace("依据", "").replace("已由", "");
			}
		} catch (Throwable e) {
			logger.error("抓取文号出现异常: " + link.getUrl(), e);
		}
		return documentNo;
	}
}
