package com.meiah.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.meiah.util.WebPageDownloader;

public class TestRegex {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "http://www.ynfzb.cn/Article/snxw/201012/59334.html";
		TestRegex t = new TestRegex();
		t.testPage(url, false);
		t.testText();
	}

	public void testPage(String url, boolean proxyIf) {
		String regex = "来源[：: 　]([^\\s]*?)\\s";
		String webContent = getWeb(url, proxyIf);
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(webContent);
		while (m.find()) {
//			System.out.println(m.group() + "\r\nstart:" + m.start() + ",end:"
//					+ m.end());

		}

	}

	public void testText() {
		//String nonWord = "[^\u4e00-\u9fa5&&[^\\w]]";
		String regex = "来源[\\s|\\pP]{1,2}([[\u4e00-\u9fa5][\\w]]{1,6})[\\s|\\pP]";
		String webContent = "来源： 钱江晚报 ";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(webContent);
		while (m.find()) {
//			System.out.println("find： " + m.group(1));

		}

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
}
