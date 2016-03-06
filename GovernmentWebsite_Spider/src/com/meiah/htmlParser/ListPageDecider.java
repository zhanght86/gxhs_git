package com.meiah.htmlParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import com.meiah.util.JavaUtil;

/**
 * @author huhb
 * 
 */
public class ListPageDecider {
	private static Logger logger = Logger.getLogger(ListPageDecider.class);
	public static final String SPLITER = "###+";

	/**
	 * @param url
	 *            传入网页url 可以为空
	 * @param pageType
	 *            url的正文正则表达式，可以为空
	 * @param webContent
	 *            html网页源码，不为空
	 * 
	 * @常见调用：boolean islistPage=LisPageDecide.isList("","",htmlSource);
	 * @return
	 */
	public static boolean isList(String url, String pageType, String webContent) {
//		System.out.println(webContent);
		boolean islist = false;
		String regex = "(?s)(?i)(.*?index[a-zA-Z]*\\.[a-zA-Z]{3,5}|.*?index[0-9]*\\.[a-zA-Z]{3,5}"
				+ "|.*?list[a-zA-Z]*\\.[a-zA-Z]{3,5}|.*?list[0-9]*\\.[a-zA-Z]{3,5}"
				+ "|.*?index_[a-zA-Z]*\\.[a-zA-Z]{3,5}|.*?index_[0-9]*\\.[a-zA-Z]{3,5}"
				+ "|.*?list_[0-9]*\\.[a-zA-Z]{3,5}|.*?list_[0-9]*\\.[a-zA-Z]{3,5})";
		String[] temp = JavaUtil.match(url, regex);
		if ((temp != null && temp.length >= 0) || url.endsWith("/")) {
			islist = true;
			return islist;
		}
		if(filter(url)) {
			islist = true;
			return islist;
		}
		if (pageType != null && pageType.length() != 0) {
			if (judgeThroughUrl(url, pageType)) {
				islist = true;
				return islist;
			} else
				return islist;
		} else {
//			islist = (judgeThroughSentence(url, webContent) || judgeThoughLinkText(url, webContent));
			islist = (judgeThoughLinkText(url, webContent));
			return islist;
		}
	}

	public static boolean judgeThroughUrl(String url, String pageType) {
		boolean isList = false;
		if (pageType != null && pageType.length() != 0) {
			String[] contentRegexs = pageType.split(SPLITER);

			for (int i = 0; i < contentRegexs.length; i++) {
				String contentRegex = contentRegexs[i];
				contentRegex = contentRegex.substring("content:".length())
						.trim();
				contentRegexs[i] = contentRegex;
				if (contentRegex.length() != 0
						&& !JavaUtil.isAllMatch(url, contentRegex)) {
					isList = true;
					break;
				}
			}

		}
		return isList;
	}
	public static boolean filter (String webContent) {
		boolean islist = false;
		int appearTimes = hit(webContent, "详细");
	    int appearTimes1 = hit(webContent, "详情");
	    int apperaTimesFB = hit(webContent, "发布于：");
	    int apperaTimesYD = hit(webContent, "阅读全文");
	    int apperaTimesMore = hit(webContent, "更多");
	    if ((appearTimes >= 7) || (apperaTimesFB >= 7) || (apperaTimesYD >= 7) || (appearTimes1 >= 7) || apperaTimesMore > 7) {
	        islist = true;
	        return islist;
	    }
	    if((appearTimes >= 5 || apperaTimesFB >= 5 || apperaTimesYD >= 5 || appearTimes1 >= 5) && webContent.contains("下一页")) {
	    	islist = true;
	    }
	    return islist;
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
				nodeList = myParser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
				nodes = nodeList.toNodeArray();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < nodes.length; i++) {
					String linkText = nodes[i].toPlainTextString().replaceAll("[\\pP|\\s]", "");
					sb.append(linkText);
					linkCharCount += getMatchChineseCharCount(linkText);
				}
//				System.out.println(webContent + "111111111111111111");
				String totalText = getPageTextContent(webContent).trim();
//				System.out.println(totalText);
				totalCharCount = getMatchChineseCharCount(totalText);
//				System.out.println("链接文字长度：" + linkCharCount);
//				System.out.println("页面文字总长度：" + totalCharCount);
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
				nodeList = myParser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
				nodes = nodeList.toNodeArray();
				// logger.debug("url count: " + nodes.length);
				for (int i = 0; i < nodes.length; i++) {
					String linkText = nodes[i].toPlainTextString().replaceAll("[\\pP|\\s]", "");
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
		Matcher m = Pattern.compile("[\u4e00-\u9fa5]").matcher(str);
		while (m.find()) {
			count++;
		}
		return count;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String aaa = "<ul style='margin-top:8px; '>            <li class='news_warp01'>      	<a href='/junshi/2015-12-30/1451439795871.shtml' title='中国长征三号甲运载火箭每17天发射1次完爆美俄' target='_blank'>        <img src=' http://img01.imgcdc.com/mili/zh_cn/important/11132797/20151230/21043050_201512300926232473400.jpg' alt='中国长征三号甲运载火箭每17天发射1次完爆美俄' title='中国长征三号甲运载火箭每17天发射1次完爆美俄' />        </a>    	<em class='em_tit01'>        <a href='/junshi/2015-12-30/1451439795871.shtml' title='中国长征三号甲运载火箭每17天发射1次完爆美俄' target='_blank'>中国长征三号甲运载火箭每17天发射1次完爆美俄</a></em>                <p> 高分四号卫星29日成功发射标志着2015年中国航天发射任务圆满收官。然而在这次发射的背后，另一组数据同样振奋人心：在2015年9月至12月29日的......【<a href='/junshi/2015-12-30/1451439795871.shtml' target='_blank'>详细</a>】</p>	  </li>            <li class='news_warp01'>      	<a href='/junshi/2015-12-30/1451439693124.shtml' title='中国依靠高分卫星家族夺回80%卫星数据市场' target='_blank'>        <img src=' /images/icon/nopic.jpg ' alt='中国依靠高分卫星家族夺回80%卫星数据市场' title='中国依靠高分卫星家族夺回80%卫星数据市场' />        </a>    	<em class='em_tit01'>        <a href='/junshi/2015-12-30/1451439693124.shtml' title='中国依靠高分卫星家族夺回80%卫星数据市场' target='_blank'>中国依靠高分卫星家族夺回80%卫星数据市场</a></em>                <p>2月29日凌晨，中国在2015年的最后一次航天发射中，成功用长征三号乙运载火箭从西昌卫星发射中心将高分四号遥感卫星送入预定轨道。...【<a href='/junshi/2015-12-30/1451439693124.shtml' target='_blank'>详细</a>】</p>	  </li>            <li class='news_warp01'>      	<a href='/junshi/2015-12-30/1451439539217.shtml' title='武警西藏总队开展“雪域勇士”大练兵' target='_blank'>        <img src=' http://news.xinhuanet.com/mil/2015-11/30/128481417_14488418752831n.jpg' alt='武警西藏总队开展“雪域勇士”大练兵' title='武警西藏总队开展“雪域勇士”大练兵' />        </a>    	<em class='em_tit01'>        <a href='/junshi/2015-12-30/1451439539217.shtml' title='武警西藏总队开展“雪域勇士”大练兵' target='_blank'>武警西藏总队开展“雪域勇士”大练兵</a></em>                <p>　11月28日，武警西藏总队在拉萨开启“雪域勇士·2015”极限训练考核竞赛活动。活动旨在检验和提升特战队员在高寒缺氧等复杂环境条件下的综合......【<a href='/junshi/2015-12-30/1451439539217.shtml' target='_blank'>详细</a>】</p>	  </li>            <li class='news_warp01'>      	<a href='/junshi/2015-12-29/145135442533.shtml' title='中日钓鱼岛行动火药味十足 中国要提防日搞偷袭' target='_blank'>        <img src=' /images/icon/nopic.jpg ' alt='中日钓鱼岛行动火药味十足 中国要提防日搞偷袭' title='中日钓鱼岛行动火药味十足 中国要提防日搞偷袭' />        </a>    	<em class='em_tit01'>        <a href='/junshi/2015-12-29/145135442533.shtml' title='中日钓鱼岛行动火药味十足 中国要提防日搞偷袭' target='_blank'>中日钓鱼岛行动火药味十足 中国要提防日搞偷袭</a></em>                <p>      小编从日本政府的抗议中得知，“一艘配有机关炮的中国海警船26日上午进入钓鱼岛海域”。在小编看来，这不是很正常吗？钓鱼岛是属于中国......【<a href='/junshi/2015-12-29/145135442533.shtml' target='_blank'>详细</a>】</p>	  </li>            <li class='news_warp01'>";
//		System.out.println(aaa.replaceAll("(?s)(?i)<a.*\">", "").replaceAll("(?s)(?i)</a>", ""));
		String bb = "详细";
//		System.out.println(hit(aaa,bb));
	}
	//统计一个词组在文本出现的次数
	public static int hit(String a, String b) {
        if (a.length() < b.length()) {
            return 0;
        }
        char[] strA = a.toCharArray();
        char[] strB = b.toCharArray();
        int count = 0, temp = 0, j = 0;
        for (int i = 0; i < strA.length; i++) {
            // 保证一个连续的字符串 b 跟 a中某段相匹配
            if (strA[i] == strB[j] && j < strB.length) {
                temp++;
                j++;
                // 此时连续的字符串 b 跟 已跟 a 中某段相匹配
                if (temp == strB.length) {
                    count++;
                    temp = 0;
                    j = 0;
                }
            }
            // 只要有一个字符不匹配，temp计数从来
            else {
                temp = 0;
                j = 0;
            }
        }
        return count;
    }
}
