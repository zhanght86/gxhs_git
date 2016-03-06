package com.meiah.htmlParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import com.meiah.util.JavaUtil;

public class ListPageDeciderWJ {
	private static Logger logger = Logger.getLogger(ListPageDeciderWJ.class);

	public static boolean isList(String url, String pageType, String webContent) {
		boolean islist = false;
		if (pageType != null && pageType.length() != 0) {
			if (judgeThroughUrl(url, pageType)) {
				islist = true;
				return islist;
			} else
				return islist;
		} else {
			islist = (judgeThroughSentence(url, webContent) || judgeThoughLinkText(
					url, webContent));
			return islist;
			// if () {
			// return true;
			// } else {
			// return false;
			// }
		}
	}

	/**
	 * @param webContent
	 *            Html源文件
	 * @return
	 */
	public static boolean isList(String webContent) {
		boolean islist = false;
		webContent = JavaUtil.clearHtml(webContent);
		islist = (judgeThroughSentence("", webContent) || judgeThoughLinkText(
				"", webContent));
		return islist;

	}

	public static boolean judgeThroughUrl(String url, String pageType) {
		boolean isList = false;
		if (pageType != null && pageType.length() != 0) {
			String indexRegex = "";
			String contentRegex = "";

			if (pageType.indexOf(",") != -1) {
				indexRegex = pageType.split(",")[0]
						.substring("index:".length()).trim();
				contentRegex = pageType.split(",")[1].substring(
						"content:".length()).trim();
			} else {
				indexRegex = pageType.indexOf("index:") != -1 ? pageType
						.substring("index:".length()).trim() : "";
				contentRegex = pageType.indexOf("content:") != -1 ? pageType
						.substring("content:".length()).trim() : "";
			}
			if ((indexRegex.length() != 0 && JavaUtil.isAllMatch(url,
					indexRegex))
					|| contentRegex.length() != 0
					&& !JavaUtil.isAllMatch(url, contentRegex))
				isList = true;
		}
		return isList;
	}

	public static boolean judgeThoughLinkText(String url, String webContent) {
		boolean flag = false;
		webContent = webContent.replaceAll("(?i)(?s)<marquee.*?</marquee>", "");
		if (isChinesePage(webContent)) {
			int linkCharCount = 0;
			int totalCharCount = 0;

			try {
				Parser myParser;
				Node[] nodes = null;
				NodeList nodeList = null;
				myParser = Parser.createParser(webContent, null);
				nodeList = myParser
						.extractAllNodesThatMatch(new NodeClassFilter(
								LinkTag.class));
				nodes = nodeList.toNodeArray();
				// logger.debug("url count: " + nodes.length);
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < nodes.length; i++) {

					String linkText = nodes[i].toPlainTextString().replaceAll(
							"[\\pP|\\s]", "");
					sb.append(linkText);
					linkCharCount += getMatchChineseCharCount(linkText);

				}
				// logger.debug("link:\r\n" + sb.toString());
				String totalText = getPageTextContent(webContent).trim();
				// logger.debug("page:\r\n" + totalText);
				totalCharCount = getMatchChineseCharCount(totalText);
				// logger.debug("链接文字长度：" + linkCharCount);
				// logger.debug("页面文字总长度：" + totalCharCount);
				// logger.debug("totalText：" + totalText);

				float ratio = (float) linkCharCount / (float) totalCharCount;
				if (logger.isDebugEnabled()) {
					logger.debug("链接(中)文字比例：" + ratio);
				}
				if (ratio > 0.90) {
					flag = true;
				} else {
					// logger.debug("url:" + url + ";链接文字比例：" + ratio);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}

		} else {
			int linkCharCount = 0;
			int totalCharCount = 0;

			try {
				Parser myParser;
				Node[] nodes = null;
				NodeList nodeList = null;
				myParser = Parser.createParser(webContent, null);
				nodeList = myParser
						.extractAllNodesThatMatch(new NodeClassFilter(
								LinkTag.class));
				nodes = nodeList.toNodeArray();
				// logger.debug("url count: " + nodes.length);
				for (int i = 0; i < nodes.length; i++) {
					String linkText = nodes[i].toPlainTextString().replaceAll(
							"[\\pP|\\s]", "");
					linkCharCount += linkText.length();
					// logger.debug(nodes[i].toPlainTextString());
				}
				String totalText = getPageTextContent(webContent).trim();
				totalCharCount = totalText.length();
				// logger.debug("链接文字长度：" + linkCharCount);
				// logger.debug("页面文字总长度：" + totalCharCount);
				// logger.debug("totalText：" + totalText);
				float ratio = (float) linkCharCount / (float) totalCharCount;
				if (ratio > 0.90)
					flag = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}
		}
		return flag;
	}

	/**
	 * 只适用于中文网页判断
	 * 
	 * @param webContent
	 * @return
	 */
	public static boolean judgeThroughSentence(String url, String webContent) {
		if (!isChinesePage(webContent))
			return false;
		boolean isList = true;
		webContent = webContent.replaceAll("(?i)(?s)<marquee.*?</marquee>", "");

		// try {
		// Parser myParser;
		// Node[] nodes = null;
		// NodeList nodeList = null;
		//
		// myParser = Parser.createParser(webContent, null);
		//
		// // 这里不能抛出异常
		// nodeList = myParser.extractAllNodesThatMatch(new NodeClassFilter(
		// TextNode.class));
		// nodes = nodeList.toNodeArray();
		// for (int i = 0; i < nodes.length; i++) {
		// TextNode textnode = (TextNode) nodes[i];
		// String line = textnode.toPlainTextString().trim();
		// if (line.equals(""))
		// continue;
		// else {
		// if (line.length() > 70) {
		// logger.debug("发现超过长度70的句子： " + line);
		// if (logger.isDebugEnabled()) {
		// logger.debug("句子长度判断耗费时间:"
		// + (System.currentTimeMillis() - t) + " ms");
		// }
		// isList = false;
		// return isList;
		// }
		// }
		// }
		// } catch (Exception e) {
		// logger.error("列表判断出现异常：", e);
		//
		// }
		webContent = webContent.replaceAll("(?i)</?span.*?>", "").replaceAll(
				"(?i)</?font.*?>", "").replaceAll("(?i)</?strong.*?>", "")
				.replaceAll("(?i)</?b.*?>", "")
				.replaceAll("(?i)(?s)<.*?>", " ").replaceAll("&nbsp;?", " ")
				.replaceAll("[\\s　]+", " ");// 去掉空格
		String[] texts = webContent.split(" ");
		for (int i = 1; i < texts.length; i++) {
			if (texts[i].length() > 70) {
				if (logger.isDebugEnabled())
					logger.debug("发现超过长度70的句子： " + texts[i]);
				isList = false;

				return isList;
			}
		}
		if (logger.isDebugEnabled())
			logger.debug("未发现超过长度70的句子块,页面为列表页");
		return isList;
	}

	public static String getPageTextContent(String source) {

		String target = source.replaceAll("(?s)(?i)<.*?>", "");
		target = target.replaceAll("[\\pP|\\s]", "");
		return target;
	}

	public static boolean isChinesePage(String webContent) {
		boolean flag = false;
		char[] ch = webContent.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (19968 <= (int) c && (int) c <= 40891) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static int getMatchChineseCharCount(String str) {
		int count = 0;
		// StringBuffer buf = new StringBuffer();
		Matcher m = Pattern.compile("[\u4e00-\u9fa5]").matcher(str);

		while (m.find()) {
			// System.out.println(i);
			// buf.append(m.group());
			count++;
		}
		// System.out.println(buf.toString());
		return count;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
