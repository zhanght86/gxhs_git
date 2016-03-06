package com.meiah.webCrawlers.SitePlugin.Gxhs.nanyang;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.BaseHrefTag;
import org.htmlparser.tags.FrameTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import com.meiah.exhtmlparser.IFrameTag;
import com.meiah.htmlParser.LinkExtractor;
import com.meiah.linkfilters.ExcludeStrFilter;
import com.meiah.linkfilters.FileExtensionFilter;
import com.meiah.linkfilters.LinkFilter;
import com.meiah.linkfilters.LinkFilterUtil;
import com.meiah.linkfilters.LocalLinkFilter;
import com.meiah.po.Link;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.JavaUtil;
import com.meiah.util.WebPageDownloader;
import com.meiah.webCrawlers.PageCrawler;

public class PageCrawler_Nanyang extends PageCrawler{
	private static Logger logger = Logger.getLogger(PageCrawler_Nanyang.class);
	public PageCrawler_Nanyang(TaskLink link, Task task) {
		super(link, task);
	}
	
	/**
	 * 过滤提取在当前html页面下需要进行抓取的链接：规则
	 * <ol>
	 * <li>站内url，或者在任务前缀下的url（任务url为默认前缀）
	 * <li>不包含排除字符串的url，如：print.html
	 * <li>以其他文档类型为结束的ur 如：.txt,.doc,.mp3等等
	 * </ol>
	 * 
	 * @param webContent
	 *            超文本内容
	 * @param task
	 *            任务设置
	 * @param link
	 *            当前页面链接信息
	 */
	protected ArrayList<TaskLink> extractTaskLinks(String webContent, Task task, TaskLink link) {
		if (logger.isDebugEnabled()) {
			logger.debug("extracting TaskLinks");
		}
		ArrayList<TaskLink> urlList = new ArrayList<TaskLink>();

		String prefix = task.getPrefix();
		String[] localSitePrefixs = prefix.split(",");
		LinkFilter LocalLinkFilter = new LocalLinkFilter(localSitePrefixs);// 过滤器，保留站点内和任务前缀下的超链接

		String[] fileExtetions = ".xls,.xml,.txt,.jpg,.mp3,.mp4,.doc,.mpg,.mpeg,.jpeg,.gif,.pdf,.png,.js,.zip,.rar,.exe,.swf,.rm,.ra,.asf,.css,.bmp,.z,.gz,.tar,.cpio,.class,.jsp".split(",");
		LinkFilter fileExtetionFilter = new FileExtensionFilter(fileExtetions);
		LinkFilter noFileExtetionFilter = LinkFilterUtil.not(fileExtetionFilter);// 过滤器，过滤掉非文本的一些的超链接

		String excludeStr = task.getSubstr();
		LinkFilter excludeStrFilter = new ExcludeStrFilter(excludeStr);
		LinkFilter noExcludeStrFilter = LinkFilterUtil.not(excludeStrFilter);// 过滤器，保留不包含排除字符串的超链接
		LinkFilter[] filters = new LinkFilter[] { LocalLinkFilter, noFileExtetionFilter, noExcludeStrFilter };

		LinkFilter taskLinkFilter = LinkFilterUtil.and(filters);
		Link l = (Link) link;

		ArrayList<Link> links = getPageUrlListByParser(l, webContent, taskLinkFilter);
		for (int i = 0; i < links.size(); i++) {
			TaskLink tl = new TaskLink(links.get(i));
			String title = fixTitle(link, tl.getTitle(), webContent);// 对于超链接标题的一个处理
			tl.setTitle(title);
			tl.setTaskid(task.getTaskid());
			tl.setLevel(link.getLevel() + 1);
			String url = tl.getUrl();
			urlList.add(tl);
		}
		return urlList;
	}
	/**
	 * 获取一个html页面的超链接url，<A><FRAME><iFRAME>和简单的JavaScript中的链接，并保持链接文字（作为标题）
	 * 
	 * @param webContent
	 * @return
	 */
	public static ArrayList<Link> getPageUrlListByParser(Link link, String webContent, LinkFilter filter) {
		if (logger.isDebugEnabled()) {
			logger.debug("getPageUrlListByParser");
		}
		String url = link.getUrl();
		String baseUrl = null;
		ArrayList<Link> urlList = new ArrayList<Link>();
		PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
		try {
			webContent = webContent.substring(webContent.indexOf("<"));
			webContent = webContent.replaceAll(
					"(?i)(?s)<(no)?script.*?</(no)?script>", "").replaceAll(
					"(?i)(?s)<!--.*?-->", "");
			Parser parser = new Parser(webContent);
			factory.registerTag(new IFrameTag());
			parser.setNodeFactory(factory);
			NodeFilter baseUrlFilter = new NodeClassFilter(BaseHrefTag.class);
			NodeList baseUrls = parser.extractAllNodesThatMatch(baseUrlFilter);
			if (baseUrls.size() > 0) {
				baseUrl = ((BaseHrefTag) baseUrls.elementAt(0)).getBaseUrl();
			}
			if (baseUrl == null || baseUrl.equals("")) {
				baseUrl = url;
			}
			parser.reset();
			NodeFilter Afilter = new NodeClassFilter(LinkTag.class);
			NodeFilter Framefilter = new NodeClassFilter(FrameTag.class);
			NodeFilter Iframefilter = new NodeClassFilter(IFrameTag.class);
			NodeFilter[] orFilter = new NodeFilter[] { Afilter, Framefilter, Iframefilter };
			NodeFilter linkFilter = new OrFilter(orFilter);
			NodeList links = parser.extractAllNodesThatMatch(linkFilter);
			if (logger.isDebugEnabled())
				logger.debug("发现链接个数：" + links.size());
			for (int i = 0; i < links.size(); i++) {
				Node linkNode = links.elementAt(i);
				String linkUrl = "", title = "";

				if (linkNode instanceof LinkTag) {
					LinkTag a = (LinkTag) links.elementAt(i);

					linkUrl = a.getAttribute("HREF");
					if (linkUrl == null || linkUrl.equals(""))
						continue;
					if (linkUrl.toLowerCase().startsWith("javascript")) {
						// href 中的JavaScript链接
						linkUrl = LinkExtractor.getJavaScriptLink(linkUrl);
					}
					linkUrl = LinkExtractor.getAbsoluteURL(baseUrl, linkUrl);
					if (linkUrl == null || linkUrl.equals(""))
						continue;
					linkUrl = linkUrl.replaceAll("&amp;", "&");
					title = a.getLinkText();
					if (title.trim().startsWith("http:") || title.matches(".*?htm[l]?")) {
						title = "";
					} else if (title.equals("")) {
 						String html = a.toHtml();
						try {
							title = JavaUtil.matchWeak(html, "<img [^>]*?alt=\"(.*?)\"")[1];
							
						} catch (Exception e) {
						}
					}
				} else if (linkNode instanceof FrameTag) {
					FrameTag a = (FrameTag) links.elementAt(i);
					title = "";
					linkUrl = a.getFrameLocation();
					linkUrl = LinkExtractor.getAbsoluteURL(baseUrl, linkUrl);
				} else {
					IFrameTag a = (IFrameTag) links.elementAt(i);
					title = "";
					linkUrl = a.getFrameLocation();
					linkUrl = LinkExtractor.getAbsoluteURL(baseUrl, linkUrl);
				}
				if (linkUrl != null) {
					linkUrl = linkUrl.replaceAll("\\s", "");// 消除url中可能的不可见字符（换行符！）
					int indexofAnc = linkUrl.indexOf("#");
					if (indexofAnc != -1)
						linkUrl = linkUrl.substring(0, indexofAnc);
					if (linkUrl.indexOf("/", 9) == -1)
						linkUrl = linkUrl + "/";
					if (filter != null && filter.accept(linkUrl)) {
//						http://epaper.01ny.cn/http_rb/html/2016-01/08/content_267496.htm
						String strUrl = linkUrl;
						if (strUrl.matches("http://epaper.01ny.cn/http_cb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]+.htm")
								|| strUrl.matches("http://epaper.01ny.cn/http_rb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]+.htm")
								|| strUrl.matches("http://epaper.01ny.cn/http_wb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]+.htm")
								|| strUrl.matches("http://epaper.01ny.cn/http_rb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]+.htm")
								|| strUrl.matches("http://epaper.01ny.cn/http_wb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]+.htm")
								|| strUrl.matches("http://epaper.01ny.cn/http_cb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]+.htm")) {
							List<Link> positionList = new ArrayList<Link>();
							String currentDate = "";
							String prefixs = "";
							String suffix = "";
							WebPageDownloader wd = null;
							if(strUrl.matches("http://epaper.01ny.cn/http_cb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_181+.htm") 
									|| strUrl.matches("http://epaper.01ny.cn/http_cb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]+.htm")) {
								if(strUrl.contains("html") && strUrl.contains("node")) {
									prefixs = strUrl.substring(0, strUrl.indexOf("html/") + 5);
									suffix = strUrl.substring(strUrl.indexOf("/node"));
									currentDate = strUrl.substring(strUrl.indexOf("html/") + 5);
									currentDate = currentDate.substring(0, currentDate.indexOf("/"));
									strUrl = prefixs + currentDate + "/period.xml";
									wd = new WebPageDownloader(strUrl);
									File file =createFileCB("D:/newspaperCB.xml" , wd);
									positionList = parseXmlStr2NewsList(file,link,prefixs,suffix,url);
									urlList.addAll(positionList);
								}
							}
							if(strUrl.matches("http://epaper.01ny.cn/http_rb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_5+.htm") 
									|| strUrl.matches("http://epaper.01ny.cn/http_rb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]+.htm")) {
								if(strUrl.contains("html") && strUrl.contains("node")) {
									prefixs = strUrl.substring(0, strUrl.indexOf("html/") + 5);
									suffix = strUrl.substring(strUrl.indexOf("/node"));
									currentDate = strUrl.substring(strUrl.indexOf("html/") + 5);
									currentDate = currentDate.substring(0, currentDate.indexOf("/"));
									strUrl = prefixs + currentDate + "/period.xml";
									wd = new WebPageDownloader(strUrl);
									File file =createFileRB("D:/newspaperRB.xml" , wd);
									positionList = parseXmlStr2NewsList(file,link,prefixs,suffix,url);
									urlList.addAll(positionList);
								}
							}
							if(strUrl.matches("http://epaper.01ny.cn/http_wb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_21+.htm") 
									|| strUrl.matches("http://epaper.01ny.cn/http_wb/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]+.htm")) {
								if(strUrl.contains("html") && strUrl.contains("node")) {
									prefixs = strUrl.substring(0, strUrl.indexOf("html/") + 5);
									suffix = strUrl.substring(strUrl.indexOf("/node"));
									currentDate = strUrl.substring(strUrl.indexOf("html/") + 5);
									currentDate = currentDate.substring(0, currentDate.indexOf("/"));
									strUrl = prefixs + currentDate + "/period.xml";
									wd = new WebPageDownloader(strUrl);
									File file =createFileWB("D:/newspaperWB.xml" , wd);
									positionList = parseXmlStr2NewsList(file,link,prefixs,suffix,url);
									urlList.addAll(positionList);
								}
							}
							// http://epaper.01ny.cn/http_rb/html/2015-01/period.xml
							
							Link l = new Link();
							l.setUrl(linkUrl);
							l.setTitle(title);
							l.setRefererUrl(url);
							l.setRefererText(link.getRefererText());
							urlList.add(l); 
							if (logger.isDebugEnabled()) {
								logger.debug("发现url：" + l.getTitle() + ":" + l.getUrl());
							}
						}
					}
			   }
		   }
		} catch (Exception e) {
			logger.error(link.getUrl() + "提取超链接出现错误", e);
		}
		if (logger.isDebugEnabled())
			logger.debug("提取链接个数：" + urlList.size());
//		for (int i = 0; i < urlList.size(); i++) {
//			System.out.println(urlList.get(i).getUrl());
//		}
		return urlList;
	}
	
	private static List<Link> parseXmlStr2NewsList(File file, Link link, String prefixs, String suffix, String url) {
		List<Link> positionList = new ArrayList<Link>();
		SAXReader saxReader = new SAXReader();
		try {
//			解析xml文件
			Document document = saxReader.read(file);
			Element root = document.getRootElement();
			List taskItemsList = root.selectNodes("period");
			//循环遍历period节点
			for (int i = 0; i < taskItemsList.size(); i++) {
				Element element = (Element) taskItemsList.get(i);
				String tel = element.selectSingleNode("period_name").getText();
				String prefixDate = tel.substring(0,tel.indexOf("-") + 1);
				String sufDate = tel.substring(tel.indexOf("-") + 1).replace("-", "/");
				String date = prefixDate + sufDate;
				String currentUrl = prefixs + date + suffix;
				Link t2 = new Link();
				t2.setUrl(currentUrl);
				t2.setTitle("");
				t2.setRefererUrl(url);
				positionList.add(t2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return positionList;
	}
	private synchronized static File createFileRB(String fileName, WebPageDownloader wd) {
		File file = new File(fileName);
		FileWriter fw;
		if (file.exists()) {
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			String rnt = wd.getPageContent();
			if (!rnt.equals("") && rnt != null) {
				fw = new FileWriter(file);
				fw.write(rnt);
				fw.flush();
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	private synchronized static File createFileCB(String fileName, WebPageDownloader wd) {
		File file = new File(fileName);
		FileWriter fw;
		if (file.exists()) {
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			String rnt = wd.getPageContent();
			if (!rnt.equals("") && rnt != null) {
				fw = new FileWriter(file);
				fw.write(rnt);
				fw.flush();
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	private synchronized static File createFileWB(String fileName, WebPageDownloader wd) {
		File file = new File(fileName);
		FileWriter fw;
		if (file.exists()) {
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			String rnt = wd.getPageContent();
			if (!rnt.equals("") && rnt != null) {
				fw = new FileWriter(file);
				fw.write(rnt);
				fw.flush();
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
}

