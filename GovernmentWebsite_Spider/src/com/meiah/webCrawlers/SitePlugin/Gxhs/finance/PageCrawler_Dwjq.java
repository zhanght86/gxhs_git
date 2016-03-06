package com.meiah.webCrawlers.SitePlugin.Gxhs.finance;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.meiah.webCrawlers.PageCrawler;

/**
 * 搜房网 定向采集类
 * 
 * @author dingrr
 * 
 */
public class PageCrawler_Dwjq extends PageCrawler {
	
	private static Logger logger = Logger.getLogger(PageCrawler_Dwjq.class);
	public PageCrawler_Dwjq(TaskLink link, Task task) {
		super(link, task);

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
					linkUrl = a.getAttribute("href");
					if(linkUrl.equals("javascript:void(0)")) {
						linkUrl = a.toHtml();
						if(StringUtils.isNotBlank(linkUrl)) {
							if(linkUrl.contains("OpenDetail")) {
								linkUrl = linkUrl.substring(linkUrl.indexOf("/") , linkUrl.lastIndexOf("'"));
							}
						}
					}
					if (linkUrl == null || linkUrl.equals(""))
						continue;
					if (linkUrl.toLowerCase().startsWith("javascript")) {
						// href 中的JavaScript链接
						linkUrl = getJavaScriptLink(linkUrl);
					}
					if (linkUrl == null || linkUrl.equals(""))
						continue;
					
					title = a.getLinkText();
					if (title.trim().startsWith("http:")
							|| title.matches(".*?htm[l]?")) {
						title = "";
					} else if (title.equals("")) {
						String html = a.toHtml();
						try {
							title = JavaUtil.matchWeak(html,
									"<img [^>]*?alt=\"(.*?)\"")[1];
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
					// if (logger.isDebugEnabled()) {
					// logger.debug("url：" + title + ":" + linkUrl);
					// }
					if (filter != null && filter.accept(linkUrl)) {
						Link l = new Link();
						l.setUrl(linkUrl);
						l.setTitle(title);
						l.setRefererUrl(url);
						l.setRefererText(link.getRefererText());
						if (logger.isDebugEnabled()) {
							logger.debug("发现url：" + l.getTitle() + ":" + l.getUrl());
						}
						urlList.add(l);
					}
				}
			}
		} catch (Exception e) {
			logger.error(link.getUrl() + "提取超链接出现错误", e);
		}
		if (logger.isDebugEnabled())
			logger.debug("提取链接个数：" + urlList.size());
		return urlList;
	}

	/**
	 * 获取几种简单的JavaScript超链接url
	 * 
	 * @param url
	 * @return
	 */
	public static String getJavaScriptLink(String url) {
		String link = "";
		String[] patterns = new String[] {
				"javascript:window.open\\('(.*?)'\\)",
				"javascript:document.URL ='(.*?)'",
				"javascript:window.location.href='(.*?)'",
				"javascript:window.location.assign\\('(.*?)'\\)",
				"javascript:window.location.replace\\('(.*?)'\\)",
				"javascript:openwin\\('(http://.*?)'" };
		for (int i = 0; i < patterns.length; i++) {
			String[] matchs = JavaUtil.matchWeak(url, patterns[i]);
			if (matchs != null && matchs.length != 0) {
				link = matchs[1];
			}
		}

		return link;
	}
}
