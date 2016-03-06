package com.meiah.htmlParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import com.meiah.linkfilters.LinkFilter;
import com.meiah.po.Link;
import com.meiah.util.JavaUtil;

public class LinkExtractorCqybzx extends LinkExtractor {
	private static Logger logger = Logger.getLogger(LinkExtractorCqybzx.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * 获取一个html页面的超链接url，<A><FRAME><iFRAME>和简单的JavaScript中的链接，并保持链接文字（作为标题）
	 * 
	 * @param webContent
	 * @return
	 */
	public static ArrayList<Link> getPageUrlListByParser(Link link,
			String webContent, LinkFilter filter) {

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
			NodeFilter[] orFilter = new NodeFilter[] { Afilter, Framefilter,
					Iframefilter };
			NodeFilter linkFilter = new OrFilter(orFilter);
			NodeList links = parser.extractAllNodesThatMatch(linkFilter);
			if (logger.isDebugEnabled())
				logger.debug("发现链接个数：" + links.size());
			for (int i = 0; i < links.size(); i++) {
				Node linkNode = links.elementAt(i);
				String linkUrl = "", title = "", author = "";
				Date pubTime = null;

				if (linkNode instanceof LinkTag) {
					LinkTag a = (LinkTag) links.elementAt(i);
					linkUrl = a.getAttribute("HREF");
					if (linkUrl == null || linkUrl.equals(""))
						continue;
					if (linkUrl.toLowerCase().startsWith("javascript")) {
						// href 中的JavaScript链接
						linkUrl = getJavaScriptLink(linkUrl);
					}
					linkUrl = getAbsoluteURL(baseUrl, linkUrl);
					if (linkUrl == null || linkUrl.equals(""))
						continue;
					linkUrl = linkUrl.replaceAll("&amp;", "&");
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
					if (JavaUtil
							.isAllMatch(linkUrl,
									"www.cqybzx.gov.cn/ShowArticle.asp\\?ArticleID=\\d+")) {
						title = a.getAttribute("title");
						if (title == null)
							continue;

					}
				} else if (linkNode instanceof FrameTag) {
					FrameTag a = (FrameTag) links.elementAt(i);
					title = "";
					linkUrl = a.getFrameLocation();
					linkUrl = getAbsoluteURL(baseUrl, linkUrl);
				} else {
					IFrameTag a = (IFrameTag) links.elementAt(i);
					title = "";
					linkUrl = a.getFrameLocation();
					linkUrl = getAbsoluteURL(baseUrl, linkUrl);
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
							logger.debug("发现url：" + l.getTitle() + ":"
									+ l.getUrl());
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

}
