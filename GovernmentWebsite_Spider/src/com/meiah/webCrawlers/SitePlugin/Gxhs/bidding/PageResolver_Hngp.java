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

import sun.java2d.pipe.SpanShapeRenderer.Simple;

public class PageResolver_Hngp extends PageResolver_General {
	
	
	public PageResolver_Hngp(Task task) {
		super(task);
	}

	protected NewsGeneric resloveNewsPage(WebPage page) {
		TaskLink link = page.getLink();
		String webContent = "";
		String pageUrl = link.getUrl();
		if(pageUrl.equals("http://www.hngp.gov.cn/henan/content?infoId=1455598189445600&channelCode=H600101&bz=1")) {
			webContent = "";
		}
		webContent = page.getWebContent();
		Document doc = null;
		try {
			doc = JavaUtil.getDocument(webContent);
		} catch (Exception e) {
			logger.error("解析新闻页面出现异常！" + link.getUrl());
			return null;
		}
		Date dateDublished = getNewsPublishTime(task, page, doc);
		if(webContent.contains("<div id=\"content\"></div>")) {
			String passUrl = "";
			if(webContent.contains("jQuery(document).ready(function ()")) {
				String html = webContent.substring(webContent.indexOf("jQuery(document).ready(function ()"),webContent.indexOf("function (data)"));
				String regex = "(?s)(?i)/webfile/henan/" + ".*" + ".htm";
				String[] results = JavaUtil.match(html, regex);
				if ((results != null) && (results.length > 0)) {
					passUrl = results[0];
				}
			}
			String webContent1 = webContent.substring(0 , webContent.indexOf("<div id=\"content\"></div>"));
			String webContent2 = webContent.substring(webContent.indexOf("<div id=\"content\"></div>") + 24);
			webContent = webContent1 + doHttpGet(pageUrl , dateDublished , passUrl) + webContent2;
			try {
				doc = JavaUtil.getDocument(webContent);
			} catch (Exception e) {
				logger.error("解析新闻页面出现异常！" + link.getUrl());
				return null;
			}
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
		
		/** end default notChange * */
		String newsTitle = getNewsTitle(task.getSiteConfig(), page, doc);
		ne.setNews_title(newsTitle);
		long tStart = System.currentTimeMillis(), t2 = 0, t3 = 0, t4 = 0;
		String content = getNewsContent(task.getSiteConfig(), page, doc);
		
		if (logger.isDebugEnabled()) {
			t3 = System.currentTimeMillis() - tStart - t2;
			logger.debug("正文提取耗费时间：" + t3 + "ms");
		}
		content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;","(?s)(?i)<.*?/>" }, new String[] { "", "" ,"" , "","",""});
		ne.setNews_content(content.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", "").trim());
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
		if(StringUtils.isNotBlank(newsSource)){
			ne.setPage_source(newsSource.replace("null", ""));
		}
		String newsAuthor = getNewsAuthor(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(newsAuthor)){
			ne.setNews_author(StringUtils.replaceEach(newsAuthor, new String[] { "等", "撰写","作者：","作者:"," ","?","null" }, new String[] { "", "" ,"" , "","","",""}));
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
				String regex = "(?s)(?i)(招标编号：)(\\s*[\\u4E00-\\u9FA5]+\\[\\d+\\]\\d+号)";
				String[] temp = JavaUtil.match(webContent, regex);
				if ((temp != null) && (temp.length > 2)) {
					documentNo = temp[2];
				}
			}
			if(StringUtils.isBlank(documentNo)) {
				String regex = "(?s)(?i)发文字号：\\s*[\\u4E00-\\u9FA5]+\\[\\d+\\].*?号|\\d{4}年\\d{1,2}月\\d{1,2}日[\\u4E00-\\u9FA5]+第\\d+号|\\d{4}年第\\d+号|\\d{4}年[\\u4E00-\\u9FA5]+第\\d+号|\\s+[\\u4E00-\\u9FA5]+〔[0-9]+〕[0-9]+号|\\s+[\\u4E00-\\u9FA5]+第[0-9]+号|[\\u4E00-\\u9FA5]+\\[\\d+\\]号";
				String[] temp = JavaUtil.match(webContent, regex);
				if ((temp != null) && (temp.length > 0)) {
					documentNo = temp[0];
				}
			}
			if (documentNo != null)
				documentNo = documentNo.replaceAll("&nbsp;", " ").replaceAll(
						"(?s)(?i)<.*?>", "").replaceAll("发文字号：", "").replaceAll("	", "");
		} catch (Throwable e) {
			logger.error("抓取文号出现异常: " + link.getUrl(), e);
		}
		return documentNo;
	}
	/**
	 * 模拟post请求
	 * @param url 
	 * @param dateDublished 
	 * @param passUrl 
	 * @param categoryID 
	 * @param typeId 
	 * @return
	 */
	protected static String doHttpGet(String url, Date dateDublished, String passUrl) {
		String htmlInfo = "";
		int retryCount = 5;
		/*String htmlid = "";
		if(url.contains("?infoId=")) {
			String regex = "(?s)(?i)(\\?infoId=)(\\d+)";
			String[] results = JavaUtil.match(url , regex);
	 		htmlid = results[2];
		}*/
		for (int i = 0; i < retryCount; i++) {
			HttpClient client = new HttpClient();
			// 设置代理服务器地址和端口
			// client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);
			// 使用GET方法，如果服务器需要通过HTTPS连接，那只需要将下面URL中的http换成https
			url = "http://www.hngp.gov.cn" + passUrl;
			GetMethod method = new GetMethod(url);
			try {
				client.executeMethod(method);

				// 打印返回的信息
				if (method.getStatusCode() == HttpStatus.SC_OK) {
					// htmlInfo = method.getResponseBodyAsString();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(method.getResponseBodyAsStream(), "ISO-8859-1"));
					String tmp = null;
					String htmlRet = "";
					while ((tmp = reader.readLine()) != null) {
						htmlRet += tmp + "\r\n";
					}
					htmlInfo = new String(htmlRet.getBytes("ISO-8859-1"), "utf-8");
				}
			} catch (HttpException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			if (StringUtils.isNotBlank(htmlInfo)) {
				htmlInfo = htmlInfo.replaceAll("(?i)(?s)<style.*?</style>", "");
				htmlInfo = "<div id=\"content\">" + "\n" + htmlInfo + "</div>";
				// 释放连接
				method.releaseConnection();
				break;
			} else {
				continue;
			}
		}
        return htmlInfo;
	}
	public static void main(String[] args) {
	}
}
