package com.meiah.webCrawlers.SitePlugin.Gxhs.bidding;
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

/**
 * 新浪财经,公司简介,财务报表 定向采集类
 * @author liubb
 *
 */
public class PageCrawler_Ahtba extends PageCrawler {
	
	protected static Map<String,String> columnUrl = new HashMap<String,String>();
	
	public PageCrawler_Ahtba(TaskLink link, Task task) {
		super(link, task);
	}
	protected void mainPro() {
		columnUrl.put("http://www.ahtba.org.cn/Category/More?id=582" , "工作动态");
		columnUrl.put("http://www.ahtba.org.cn/Category/More?id=585" , "通知公告");
		columnUrl.put("http://www.ahtba.org.cn/Category/More?id=583" , "市县");
		columnUrl.put("http://www.ahtba.org.cn/Category/More?id=613" , "专栏文章");
		columnUrl.put("http://www.ahtba.org.cn/Category/More?id=723" , "公告发布监督台");
		columnUrl.put("http://www.ahtba.org.cn/Category/More?id=618" , "行业服务培训通知");
		columnUrl.put("http://www.ahtba.org.cn/Category/More?id=725" , "违法违规公告台");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=714&scid=713" , "招标事项核准");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=714&scid=596" , "资格预审公告");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=714&scid=597" , "建设工程招标公告");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=714&scid=600" , "建设工程中标候选人及中标结果公示");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=739&scid=740" , "政府采购交易信息");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=739&scid=741" , "政府采购中标成交公示");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=742&scid=743" , "国有产权交易信息");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=742&scid=744" , "国有产权中标成交公示");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=745&scid=746" , "土地出让交易信息");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=745&scid=747" , "土地出让中标成交公示");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=569&scid=604" , "自主采招采招公告");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=569&scid=605" , "自主采招项目变更公告");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=569&scid=606" , "自主采招中标候选人及中标结果公示");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=569&scid=605" , "自主采招项目变更公告");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=569&scid=605" , "自主采招项目变更公告");
		columnUrl.put("http://www.ahtba.org.cn/Notice/AnhuiNoticeSearch?spid=569&scid=605" , "自主采招项目变更公告");
		columnUrl.put("http://www.ahtba.org.cn/Category/More?typeId=120&id=583" , "国家行业动态");
		columnUrl.put("http://www.ahtba.org.cn/Category/More?typeId=121&id=583" , "省级行业动态");
		columnUrl.put("http://www.ahtba.org.cn/Category/More?typeId=122&id=583" , "市县行业动态");
		columnUrl.put("http://www.ahtba.org.cn/Category/More?typeId=123&id=583" , "企业行业动态");
		for(Entry<String,String> entry : columnUrl.entrySet()) {
			if (!SysObject.existsUrl(entry.getKey())) {
				TaskLink tl = new TaskLink();
				tl.setUrl(entry.getKey());
				tl.setTitle(entry.getValue());
				tl.setLevel(link.getLevel());
				SysObject.addLink(link);
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
	
	/**
	 * 抓取html页面源代码，如果需要，设置cookie、验证转向
	 * 
	 * @return
	 */
	protected String getPageContent() {
		String webContent = "";
		String downloadUrl = link.getUrl();
		String[] element = downloadUrl.split("&");
		
		if(downloadUrl.contains("Notice/AnhuiNoticeSearch?")) {
			webContent = doHttpGet(downloadUrl + "&srcode=&sttype=&stime=36500&stitle=&pageNum=");
			return webContent;
		}
		if(downloadUrl.contains("More?id")) {
			String categoryID = downloadUrl.substring(downloadUrl.indexOf("?id=") + 4);
			webContent = doHttpPost("http://www.ahtba.org.cn/Article/SearchArticleAnHuiList" , categoryID , "");
			return webContent;
		}
		if(downloadUrl.contains("More?typeId") && downloadUrl.contains("&id")) {
			String categoryID = downloadUrl.substring(downloadUrl.indexOf("&id=") + 4);
			String typeId = downloadUrl.substring(downloadUrl.indexOf("?typeId") + 7 , downloadUrl.indexOf("&id"));
			webContent = doHttpPost("http://www.ahtba.org.cn/Article/SearchArticleAnHuiList" , categoryID ,typeId);
			return webContent;
		}
		WebPageDownloaderAhtba downloader = new WebPageDownloaderAhtba(downloadUrl);
		downloader.setCheckCookie(true);
		downloader.setCookieStr(task.getUcookies());
		downloader.setCheckRedirect(true);
		if (task.getProxyurl() != null && task.getProxyurl().length() > 0) {
			downloader.setUseProxyIf(true);
			downloader.setProxyUrl(task.getProxyurl());
			downloader.setProxyPort(task.getProxyport());
			downloader.setProxyUser(task.getProxyuser());
			downloader.setProxyPwd(task.getProxypwd());
		}

		try {
			webContent = downloader.getPageContent();
			if (logger.isDebugEnabled())
				logger.debug(webContent);
			if (link.getLevel() == SysConstants.INIT_LEVEL
					&& webContent.indexOf("t3_ar_guard()") != -1) {
				try {
					String[] cookieInfo = JavaUtil.match(webContent, "(?is)(ant_stream_.*?)\\|path\\|(\\d+)\\|(\\d+)");
					String cookie = cookieInfo[1] + "=" + cookieInfo[3] + "/" + cookieInfo[2];
					task.setUcookies(cookie);
					downloader.setCookieStr(cookie);
					webContent = "";
					webContent = downloader.getPageContent();
				} catch (Exception e) {
				}
			}
			String cookie = downloader.getCookieGot();
			if (cookie != null && cookie.length() > 0) {
				task.setUcookies(cookie);
				logger.info(link.getUrl() + ":设置cookie: " + task.getUcookies());
			}
			String redirectUrl = downloader.getRedirectUrl();
			// if (redirectUrl != null && !redirectUrl.equals("")
			// && SysObject.isExistsUrl(redirectUrl)
			// && !redirectUrl.equals(link.getUrl())) {
			// // 如果转向后的链接已经抓取过则忽略该链接
			// return "";
			// }
			if (redirectUrl != null && !redirectUrl.equals("")
					&& downloadUrl.equals(task.getUrl())) {// 如果在任务url（一般为网站的主页），存在跳转，则添加跳转后的超链接为任务的前缀
				link.setUrl(redirectUrl);
				if (redirectUrl.substring(9).indexOf("/") == -1)
					redirectUrl = redirectUrl + "/";
				String prefix = redirectUrl.substring(0, redirectUrl
						.lastIndexOf("/") + 1);
				if (task.getPrefix() != null && !task.getPrefix().equals("")) {
					task.setPrefix(task.getPrefix() + "," + prefix);
				} else {
					task.setPrefix(prefix);
				}
				logger.warn("任务添加前缀: " + prefix + "  在链接：" + downloadUrl);

			}
		} catch (Exception e) {
			logger.warn("下载网页 " + downloadUrl + " 出现异常：" + e.getMessage());
		}
		return webContent;
	}
	/**
	 * 模拟post请求
	 * @param url 
	 * @param categoryID 
	 * @param typeId 
	 * @return
	 */
	protected String doHttpPost(String url, String categoryID, String typeId) {
		String htmlInfo = "";
		int retryCount = 5;
		int pageNum = 1;
		url = "http://www.ahtba.org.cn/Article/SearchArticleAnHuiList";
		while(pageNum != -1) {
			for (int i = 0; i < retryCount; i++) {
				HttpClient client = new HttpClient();    
		        //使用GET方法，如果服务器需要通过HTTPS连接，那只需要将下面URL中的http换成https      
			 	PostMethod method = new PostMethod(url);
		 		NameValuePair[] data ={ new NameValuePair("str",""),new NameValuePair("categoryId",categoryID),new NameValuePair("pageNum",String.valueOf(pageNum)),new NameValuePair("pageSize","45"),new NameValuePair("typeId",typeId)};
		        method.setRequestBody(data);
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
		        	if(!htmlInfo.contains("<li>") && !htmlInfo.contains("<LI>")) {
		        		pageNum = -1;
		        	}
		        	addLink(htmlInfo);
		  	       //释放连接      
		  	        method.releaseConnection();
		        	break;
		        }
		        else {
		        	continue;
		        }
			}
			if(pageNum != -1) {
				pageNum++;
			}
		}
        return htmlInfo;
	}
	
	private String doHttpGet(String url) {
		String htmlInfo = "";
		int retryCount = 3;
		int pageNum = 0;
		while(pageNum != -1) {
			url = url + String.valueOf(++pageNum) + "&pageSize=45";
			for (int i = 0; i < retryCount; i++) {
				HttpClient client = new HttpClient();         
		        //设置代理服务器地址和端口           
				//client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);      
		        //使用GET方法，如果服务器需要通过HTTPS连接，那只需要将下面URL中的http换成https      
			 	GetMethod method = new GetMethod(url);
		        try {
					client.executeMethod(method);
				} catch (HttpException e) {
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				} 
		       //打印返回的信息  
		        if (method.getStatusCode() == HttpStatus.SC_OK) { 
		        	htmlInfo = method.getResponseBodyAsString();
		        }
		        if(StringUtils.isNotBlank(htmlInfo)) {
		        	
		        	if(!htmlInfo.contains("<a Title=") && !htmlInfo.contains("<a title=")) {
		        		pageNum = -1;
		        	}
		        	addLink(htmlInfo);
		  	       //释放连接      
		  	        method.releaseConnection();
		        	break;
		        }
		        else {
		        	continue;
		        }
			}
		}
		return htmlInfo;
	}
	public static void main(String[] args) {
		String url = "http://www.ahtba.org.cn/Author/AuthorInfoDetail?id=170018";
		String regex = "(?s)(?i)(\\?id=)(\\d+)";
		String[] results = JavaUtil.match(url, regex);
	}
}
