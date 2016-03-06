package com.meiah.webCrawlers.SitePlugin.Gxhs.bidding;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
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
import com.meiah.webCrawlers.SiteCrawler;
import com.meiah.webCrawlers.SitePlugin.PageResolver_General;

public class PageResolver_Ahtba extends PageResolver_General {
	public PageResolver_Ahtba(Task task) {
		super(task);
	}

	protected NewsGeneric resloveNewsPage(WebPage page) {
		TaskLink link = page.getLink();
		String pageUrl = link.getUrl();
//		String pageUrl = "http://www.ahtba.org.cn/Notice/NoticeDetail?id=319217";
		String webContent = "";
		if(pageUrl.contains("http://www.ahtba.org.cn/Notice/NoticeDetail?id=")) {
			webContent = doHttpPost(pageUrl);
		} else {
			webContent = page.getWebContent();
		}
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
			ne.setPage_source(newsSource);
		}
		String newsAuthor = getNewsAuthor(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(newsAuthor)){
			ne.setNews_author(StringUtils.replaceEach(newsAuthor, new String[] { "等", "撰写","作者：","作者:"," ","?" }, new String[] { "", "" ,"" , "","",""}));
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
	 * 模拟post请求
	 * @param url 
	 * @param categoryID 
	 * @param typeId 
	 * @return
	 */
	protected String doHttpPost(String url) {
		String htmlInfo = "";
		int retryCount = 5;
		for (int i = 0; i < retryCount; i++) {
			HttpClient client = new HttpClient();    
	        //使用GET方法，如果服务器需要通过HTTPS连接，那只需要将下面URL中的http换成https      
		 	PostMethod method = new PostMethod("http://www.ahtba.org.cn/Notice/NoticeContent");
		    if(url.contains("http://www.ahtba.org.cn/Notice/NoticeDetail?id=")) {
		 		String regex = "(?s)(?i)(\\?id=)(\\d+)";
				String[] results = JavaUtil.match(url, regex);
		 		String id = results[2];
		 		NameValuePair[] data ={ new NameValuePair("id",id)};
		        method.setRequestBody(data);
		 	}
	        try {
				client.executeMethod(method);
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
	       //打印返回的信息  
	        htmlInfo = method.getResponseBodyAsString();
	        if(StringUtils.isNotBlank(htmlInfo)) {
	        	if(!htmlInfo.contains("<!doctype html")) {
	        		StringBuffer sbf = new StringBuffer();
					sbf.append("<!doctype html>" + "\n" + "<html>" + "\n" + "<head>" + "\n");
					sbf.append("<meta http-equiv=\"x-ua-compatible\" content=\"ie=8\" />" + "\n" + "</head>" + "\n" + "<body>" + "\n");
					sbf.append(htmlInfo + "\n");
					sbf.append("</body>" + "\n" + "</html>");
					htmlInfo = sbf.toString();
	        	}
	  	       //释放连接      
	  	        method.releaseConnection();
	        	break;
	        }
	        else {
	        	continue;
	        }
		}
        return htmlInfo;
	}
}
