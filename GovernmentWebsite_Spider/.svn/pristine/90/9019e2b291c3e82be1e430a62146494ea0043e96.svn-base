package com.meiah.test;

import org.apache.log4j.Logger;

import com.meiah.htmlParser.ContentExtractor;
import com.meiah.util.WebPageDownloader;

public class TestNewsContent {
	private static Logger logger = Logger.getLogger(TestNewsContent.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// String url =
		// "http://futures.eastmoney.com/news/1517,20110505133984845.html";
		String url = "http://www.huaihua.gov.cn/main/hhmh/zwgk/rdzt/xzcf/4_18791/Default.shtml";
		TestNewsContent t = new TestNewsContent();
		t.testPage(url, false);

		// TestNewsTime.testAll();

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

		target = target.replaceAll("&nbsp;?", " ").replaceAll("\\s+", " ")
				.replaceAll("[ ]{2,}", " ");// [ ]非空格！
		return target;
	}

	public void testPage(String url, boolean proxyIf) {

		String webContent = getWeb(url, proxyIf);
		webContent = clearHtml(webContent);
		logger.debug(webContent);
		String content = "";
		try {
			content = ContentExtractor.extractMainContent("", "", webContent);
		} catch (Exception e) {

			e.printStackTrace();
		}
		// content = content.replaceAll(" {2,}", "\r\n");
		// logger.info("\r\n" + content);
		// try {
		// content = ContentExtractorV2.extractMainContent("", "", webContent);
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// }

		content = content.replaceAll("　{2,}", "\r\n");
//		System.out.println("\r\n\r\n" + content);

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

			e.printStackTrace();
		}
		return ret;
	}

	public static String getWeb(String url) {
		String ret = getWeb(url, false);
		return ret;
	}

}
