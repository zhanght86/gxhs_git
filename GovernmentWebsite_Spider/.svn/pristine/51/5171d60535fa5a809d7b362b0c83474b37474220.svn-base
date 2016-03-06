package com.meiah.webCrawlers.SitePlugin.Gxhs.finance;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.FrameTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import com.meiah.exhtmlparser.IFrameTag;
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
public class PageCrawler_Cngold extends PageCrawler {
	
	private static Logger logger = Logger.getLogger(PageCrawler_Cngold.class);
	public PageCrawler_Cngold(TaskLink link, Task task) {
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
	protected ArrayList<TaskLink> extractTaskLinks(String webContent,
			Task task, TaskLink link) {
		if (logger.isDebugEnabled()) {
			logger.debug("extracting TaskLinks");
		}
		ArrayList<TaskLink> urlList = new ArrayList<TaskLink>();

		String prefix = task.getPrefix();
		String[] localSitePrefixs = prefix.split(",");
		LinkFilter LocalLinkFilter = new LocalLinkFilter(localSitePrefixs);// 过滤器，保留站点内和任务前缀下的超链接

		String[] fileExtetions = ".xls,.xml,.txt,.jpg,.mp3,.mp4,.doc,.mpg,.mpeg,.jpeg,.gif,.png,.js,.zip,.rar,.exe,.swf,.rm,.ra,.asf,.css,.bmp,.pdf,.z,.gz,.tar,.cpio,.class"
				.split(",");
		LinkFilter fileExtetionFilter = new FileExtensionFilter(fileExtetions);
		LinkFilter noFileExtetionFilter = LinkFilterUtil
				.not(fileExtetionFilter);// 过滤器，过滤掉非文本的一些的超链接

		String excludeStr = task.getSubstr();
		LinkFilter excludeStrFilter = new ExcludeStrFilter(excludeStr);
		LinkFilter noExcludeStrFilter = LinkFilterUtil.not(excludeStrFilter);// 过滤器，保留不包含排除字符串的超链接
		LinkFilter[] filters = new LinkFilter[] { LocalLinkFilter,
				noFileExtetionFilter, noExcludeStrFilter };
		Link l = (Link) link;
		ArrayList<Link> links = getPageUrlListByParser(l, webContent);
		for (int i = 0; i < links.size(); i++) {
			TaskLink tl = new TaskLink(links.get(i));
			tl.setTaskid(task.getTaskid());
			tl.setLevel(link.getLevel() + 1);
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
	public static ArrayList<Link> getPageUrlListByParser(Link link,
			String webContent) {
		if (logger.isDebugEnabled()) {
			logger.debug("getPageUrlListByParser");
		}
		ArrayList<Link> urlList = new ArrayList<Link>();
		PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
		String prefixUrl = "";
		try {
			webContent = webContent.replaceAll(
					"(?i)(?s)<(no)?script.*?</(no)?script>", "").replaceAll(
					"(?i)(?s)<!--.*?-->", "");
			Parser parser = new Parser(webContent);
			factory.registerTag(new IFrameTag());
			parser.setNodeFactory(factory);
			parser.reset();
			NodeFilter Afilter = new NodeClassFilter(LinkTag.class);
			NodeFilter Framefilter = new NodeClassFilter(FrameTag.class);
			NodeFilter Iframefilter = new NodeClassFilter(IFrameTag.class);
			NodeFilter[] orFilter = new NodeFilter[] { Afilter, Framefilter,
					Iframefilter };
			NodeFilter linkFilter = new OrFilter(orFilter);
			NodeList links = parser.extractAllNodesThatMatch(linkFilter);
			String linkUrl = "";
			String title = "";
			if (logger.isDebugEnabled())
				logger.debug("发现链接个数：" + links.size());
			for (int i = 0; i < links.size(); i++) {
				Node linkNode = links.elementAt(i);
				if (linkNode instanceof LinkTag) {
					LinkTag a = (LinkTag) links.elementAt(i);
					linkUrl = a.getAttribute("href");
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
				} else {
					IFrameTag a = (IFrameTag) links.elementAt(i);
					title = "";
					linkUrl = a.getFrameLocation();
				}
				if (linkUrl != null) {
					linkUrl = linkUrl.replaceAll("\\s", "");// 消除url中可能的不可见字符（换行符！）
					int indexofAnc = linkUrl.indexOf("#");
					if (indexofAnc != -1)
						linkUrl = linkUrl.substring(0, indexofAnc);
					if (linkUrl.indexOf("/", 9) == -1)
						linkUrl = linkUrl + "/";
					// if (filter != null && filter.accept(linkUrl)) {
					Link l = new Link();
					l.setUrl(linkUrl);
					l.setTitle(title);
					l.setRefererUrl(prefixUrl);
					l.setRefererText(link.getRefererText());
					if (logger.isDebugEnabled()) {
						logger.debug("发现url：" + l.getTitle() + ":" + l.getUrl());
					}
					urlList.add(l);
				}
			}
		} catch (Exception e) {
			logger.error(link.getUrl() + "提取超链接出现错误", e);
		}
		if (logger.isDebugEnabled())
			logger.debug("提取链接个数：" + urlList.size());
		/*for (int j = 0; j <urlList.size(); j++) {
//			System.out.println(urlList.get(j).getUrl());
		}*/
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
