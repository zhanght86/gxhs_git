package com.meiah.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.meiah.po.TaskLink;
import com.meiah.po.WebPage;
import com.meiah.util.JavaUtil;
import com.meiah.util.WebPageDownloader;

public class TestNewsSource {
	private static Logger logger = Logger.getLogger(TestNewsSource.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String url = "http://cn.wsj.com/gb/20101213/bog073800.asp?source=UpFeature";
		TestNewsSource t = new TestNewsSource();
		t.testPage(url, false);

		// TestNewsTime.testAll();

	}

	public void testPage(String url, boolean proxyIf) {

		String webContent = getWeb(url, proxyIf);
		TaskLink link = new TaskLink();
		link.setUrl(url);
		WebPage page = new WebPage();
		page.setLink(link);
		webContent = clearHtml(webContent);
		page.setWebContent(webContent);
		String source = getNewsSource("", "", page);
		logger.info(source);

	}

	public void testPage(String url) {
		testPage(url, false);
	}

	public static String getWeb(String url, boolean proxyIf) {
		String ret = "";
		WebPageDownloader wd = new WebPageDownloader(url);
		if (proxyIf == true) {
			wd.setUseProxyIf(true);
			wd.setProxyUrl("210.177.242.75");
			wd.setProxyPort("443");
			wd.setProxyUser("lst");
			wd.setProxyPwd("lst2010");
		}
		try {
			ret = wd.getPageContent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public static String getWeb(String url) {
		String ret = getWeb(url, false);
		return ret;
	}

	/**
	 * 去除html页面源代码 无用信息
	 * 
	 * @param webSourcePage
	 * @return
	 */
	private String clearHtml(String webSourcePage) {
		String target = webSourcePage.replaceAll("(?i)(?s)<style.*?</style>",
				"").replaceAll("(?i)(?s)<(no)?script.*?</(no)?script>", "")
				.replaceAll("(?i)(?s)<select.*?</select>", "").replaceAll(
						"(?i)(?s)<!--.*?-->", "");
		target = target.replaceAll("&nbsp;?", " ").replaceAll("\\s+", " ");
		return target;
	}

	/**
	 * 提取新闻来源
	 * 
	 * @param beginSource
	 * @param endSource
	 * @param webContent
	 * @return
	 */
	private String getNewsSource(String beginSource, String endSource,
			WebPage page) {
		String newsSource = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String textBeginCode = beginSource;
			String textEndCode = endSource;
			if (!textBeginCode.equals("") && !textEndCode.equals("")) {
				String text = webContent.toLowerCase();
				int iPos0 = text.indexOf(textBeginCode.toLowerCase());
				if (iPos0 != -1) {
					int len0 = textBeginCode.length();
					int iPos1 = text.indexOf(textEndCode.toLowerCase(), iPos0
							+ len0);
					if (iPos1 != -1) {
						newsSource = text.substring(iPos0 + len0, iPos1)
								.replaceAll("&nbsp;", " ").replaceAll(
										"(?s)(?i)<.*?>", "");
					}
				}

			}

			if (newsSource.equals("")) {
				String regex = "(?s)(?i)(来源|來源)[\\s|\\pP]{1,2}([[\u4e00-\u9fa5][\\w]]{1,6}[网报])[\\s|\\pP]";
				webContent = webContent.replaceAll(
						"(?i)(?s)<marquee.*?</marquee>", "").replaceAll(
						"(?s)(?i)<.*?>", " ").replaceAll("\\s+", " ");
				String[] results = JavaUtil.match(webContent, regex);
				if (results != null && results.length > 2)
					newsSource = results[2];

			}
			if (newsSource.equals("")) {
				String regex = "\\d{2}:\\d{2}";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(webContent);
				while (m.find()) {
					String temp = webContent.substring(m.end(), m.end() + 10);
					String regexSource = "(?s)(?i)[\\s|\\pP]{1,2}([[\u4e00-\u9fa5][\\w]]{1,6}[网报])[\\s|\\pP]";
					String[] results = JavaUtil.match(temp, regexSource);
					if (results != null && results.length > 1) {
						newsSource = results[1];
						break;
					}
				}

			}
		} catch (Throwable e) {
			logger.error("抓取新闻来源出现异常: " + link.getUrl(), e);
		}
		return newsSource;
	}

}
