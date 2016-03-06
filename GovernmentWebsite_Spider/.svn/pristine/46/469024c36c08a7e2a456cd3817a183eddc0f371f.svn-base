package com.meiah.test;

import org.apache.log4j.Logger;

import com.meiah.po.TaskLink;
import com.meiah.po.WebPage;
import com.meiah.util.JavaUtil;
import com.meiah.util.WebPageDownloader;

public class TestNewsAuthor {
	private static Logger logger = Logger.getLogger(TestNewsAuthor.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String url = "http://cn.wsj.com/gb/20101210/hkt083935.asp?source=UpFeature";
		TestNewsAuthor t = new TestNewsAuthor();
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
		String source = getNewsAuthor("", "", page);
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
	 * 提取新闻作者
	 * 
	 * @param beginAuthor
	 * @param endAuthor
	 * @param webContent
	 * @return
	 */
	private String getNewsAuthor(String beginAuthor, String endAuthor,
			WebPage page) {
		String newsAuthor = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String textBeginCode = beginAuthor;
			String textEndCode = endAuthor;
			if (!textBeginCode.equals("") && !textEndCode.equals("")) {
				String text = webContent.toLowerCase();
				int iPos0 = text.indexOf(textBeginCode.toLowerCase());
				if (iPos0 != -1) {
					int len0 = textBeginCode.length();
					int iPos1 = text.indexOf(textEndCode.toLowerCase(), iPos0
							+ len0);
					if (iPos1 != -1) {
						newsAuthor = text.substring(iPos0 + len0, iPos1)
								.replaceAll("&nbsp;", " ").replaceAll(
										"(?s)(?i)<.*?>", "");
					}
				}

			}

			if (newsAuthor.equals("")) {
				String regex = "(?s)(?i)(记者|编辑|作者)[ 　：:]{1,2}([[\u4e00-\u9fa5][ 　][\\w]]{2,4})[\\s|\\pP]";
				webContent = webContent.replaceAll(
						"(?i)(?s)<marquee.*?</marquee>", "").replaceAll(
						"(?s)(?i)<.*?>", " ").replaceAll("\\s+", " ");
				String[] results = JavaUtil.match(webContent, regex);
				if (results != null && results.length > 2)
					newsAuthor = results[2];
			}
		} catch (Throwable e) {
			logger.error("抓取新闻作者出现异常: " + link.getUrl(), e);
		}
		return newsAuthor;
	}
}
