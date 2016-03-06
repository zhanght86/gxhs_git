package com.meiah.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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

public class LinkExtractor {
	private static Logger logger = Logger.getLogger(LinkExtractor.class);

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
	public static ArrayList<Link> getUrlsByParser(Link link, String webContent) {
		ArrayList<Link> urlList = getPageUrlListByParser(link, webContent, null);
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
						linkUrl = getJavaScriptLink(linkUrl);
					}
					linkUrl = getAbsoluteURL(baseUrl, linkUrl);
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
//					if (filter != null && filter.accept(linkUrl)) {
						Link l = new Link();
						l.setUrl(linkUrl);
						l.setTitle(title);
						l.setRefererUrl(url);
						l.setRefererText(link.getRefererText());
						urlList.add(l);
//					}
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
	 * Build a URL from the link and base provided.
	 * 
	 * @param link
	 *            The (relative) URI.
	 * @param base
	 *            The base URL of the page, either from the &lt;BASE&gt; tag or,
	 *            if none, the URL the page is being fetched from.
	 * @param strict
	 *            If <code>true</code> a link starting with '?' is handled
	 *            according to <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC
	 *            2396</a>, otherwise the common interpretation of a query
	 *            appended to the base is used instead.
	 * @return An absolute URL.
	 * @exception MalformedURLException
	 *                If creating the URL fails.
	 */
	public static URL constructUrl(String link, String base, boolean strict)
			throws MalformedURLException {
		String path;
		boolean modified;
		boolean absolute;
		int index;
		URL url; // constructed URL combining relative link and base

		// Bug #1461473 Relative links starting with ?
		if (!strict && ('?' == link.charAt(0))) { // remove query part of base
			// if any
			if (-1 != (index = base.lastIndexOf('?')))
				base = base.substring(0, index);
			url = new URL(base + link);
		} else
			url = new URL(new URL(base), link);
		path = url.getFile();
		modified = false;
		absolute = link.startsWith("/");
		if (!absolute) { // we prefer to fix incorrect relative links
			// this doesn't fix them all, just the ones at the start
			while (path.startsWith("/.")) {
				if (path.startsWith("/../")) {
					path = path.substring(3);
					modified = true;
				} else if (path.startsWith("/./") || path.startsWith("/.")) {
					path = path.substring(2);
					modified = true;
				} else
					break;
			}
		}
		// fix backslashes
		while (-1 != (index = path.indexOf("/\\"))) {
			path = path.substring(0, index + 1) + path.substring(index + 2);
			modified = true;
		}
		if (modified)
			url = new URL(url, path);

		return (url);
	}

	/**
	 * Create an absolute URL from a relative link.
	 * 
	 * @param link
	 *            The reslative portion of a URL.
	 * @return The fully qualified URL or the original link if it was absolute
	 *         already or a failure occured.
	 */
	public static String getAbsoluteURL(String baseUrl, String link) {
		return (getAbsoluteURL(baseUrl, link, false));
	}

	/**
	 * Create an absolute URL from a relative link.
	 * 
	 * @param link
	 *            The reslative portion of a URL.
	 * @param strict
	 *            If <code>true</code> a link starting with '?' is handled
	 *            according to <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC
	 *            2396</a>, otherwise the common interpretation of a query
	 *            appended to the base is used instead.
	 * @return The fully qualified URL or the original link if it was absolute
	 *         already or a failure occured.
	 */
	public static String getAbsoluteURL(String baseUrl, String link,
			boolean strict) {
		String base;
		URL url;
		String ret;

		if ((null == link) || ("".equals(link)))
			ret = "";
		else
			try {
				base = baseUrl;

				url = constructUrl(link, base, strict);
				ret = url.toExternalForm();

			} catch (MalformedURLException murle) {
				// if (link.indexOf("javascript:") == -1)
				// logger.error(baseUrl + "解析超链接出现异常：" + link, murle);
				ret = null;
			}

		return (ret);
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
