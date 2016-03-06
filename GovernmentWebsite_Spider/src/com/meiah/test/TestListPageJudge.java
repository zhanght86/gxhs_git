package com.meiah.test;

import com.meiah.htmlParser.ContentExtractor;
import com.meiah.htmlParser.ListPageDecider;
import com.meiah.po.Task;
import com.meiah.util.JavaUtil;
import com.meiah.util.Simi;
import com.meiah.util.WebPageDownloader;
import com.meiah.webCrawlers.PageResolver;

public class TestListPageJudge {
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String url = "http://people.rednet.cn/PeopleShow.asp?ID=1871400";
		TestListPageJudge.testPage(url, false);

		// TestNewsTime.testAll();

	}

	public static void testPage(String url, boolean proxyIf) {

		String webContent = getWeb(url, proxyIf);
		webContent = clearHtml(webContent);
		boolean flag = ListPageDecider.isList(url, "", webContent);
		if (flag){}
//			System.out.println("列表：" + url);
		else {
			String content = "";
			try {
				content = ContentExtractor.extractMainContent("", "",
						webContent);
			} catch (Exception e) {

				e.printStackTrace();
			}
			Task task = new Task();
			PageResolver pr = new PageResolver(task);
			flag = pr.isContentPage(url, "", content);
			if (flag){}
//				System.out.println("正文：" + url);
			String conformText = Simi.getSimitxt(content);
//			System.out.println("长句："+conformText);
		}
	}

	/**
	 * 判断当前页面是否为正文页面
	 * 
	 * @param url
	 * @param pageType
	 * @param extractContent
	 * @return
	 */
	public boolean isContentPage(String url, String pageType,
			String extractContent) {
		boolean flag = false;
		if (pageType != null && pageType.length() != 0) {
			String contentRegex = "";
			contentRegex = pageType.indexOf("content:") != -1 ? pageType
					.substring("content:".length()).trim() : "";

			if (contentRegex.length() != 0
					&& JavaUtil.isAllMatch(url, contentRegex))
				flag = true;
		} else if (extractContent.length() > 200) {
			flag = true;
		} else {
			if (extractContent != null && !extractContent.equals("")) {
				String[] keyWords = new String[] { "电", "讯", "消息", "报道" };
				int indexInitial = extractContent.length() >= 20 ? 20
						: extractContent.length() - 1;
				String initText = extractContent.substring(0, indexInitial);
				for (int i = 0; i < keyWords.length; i++) {
					if (initText.indexOf(keyWords[i]) != -1) {
						flag = true;
						break;
					}

				}
			}
		}
		return flag;
	}

	public static void testPage(String url) {
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
	private static String clearHtml(String webSourcePage) {
		String target = webSourcePage.replaceAll("(?i)(?s)<style.*?</style>",
				"").replaceAll("(?i)(?s)<(no)?script.*?</(no)?script>", "")
				.replaceAll("(?i)(?s)<select.*?</select>", "").replaceAll(
						"(?i)(?s)<!--.*?-->", "");
		target = target.replaceAll("&nbsp;?", " ").replaceAll("\\s+", " ");
		return target;
	}

}
