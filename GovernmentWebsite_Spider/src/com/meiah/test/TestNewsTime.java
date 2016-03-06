package com.meiah.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.meiah.htmlParser.PublishTimeExtractor;
import com.meiah.util.WebPageDownloader;

public class TestNewsTime {
	// private static Logger logger = Logger.getLogger(TestNewsTime.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String url = "http://auto.sina.com.cn/car/2013-12-25/09441259561.shtml";
		//TestNewsTime.testPage(url, true, "");
		// TestNewsTime.testAll();
		// TestNewsTime.testAll();
		TestNewsTime.testString(" 9:19 p.m. EST January 11, 2014 ");

	}

	public static void testPage(String url, boolean proxyIf) {
		testPage(url, proxyIf, "");
	}

	public static void testPage(String url, boolean proxyIf, String dataformat) {
		PublishTimeExtractor te = new PublishTimeExtractor();
		String webContent = getWeb(url, proxyIf);
		// System.out.println(webContent);
		webContent = clearHtml(webContent);
		// System.out.println(webContent);
		Date dateline = te.getNewsPublishedDate("", webContent, dataformat, -1);
//		System.out.println("result: " + dateline);
	}

	public static void testPage(String url) {
		testPage(url, false);
	}

	public static void testPage(String url, String dateformat) {
		testPage(url, false);
	}

	public static void testAll() {
		PublishTimeExtractor te = new PublishTimeExtractor();
		String[] timeFormats = getAllTimeFormats("E:\\huhb\\time.txt");
		for (int i = 0; i < timeFormats.length; i++) {
			String timeFormat = timeFormats[i];
			Date dateline = te.getNewsPublishedDate("", timeFormat, "", -1);
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//			System.out.println(timeFormat + ": " + sf.format(dateline));
		}
	}

	public static void testSetting(String url, String dateformat) {
		PublishTimeExtractor te = new PublishTimeExtractor();
		String[] timeFormats = getAllTimeFormats("E:\\huhb\\time.txt");
		for (int i = 0; i < timeFormats.length; i++) {
			String timeFormat = timeFormats[i];
			Date dateline = te.getNewsPublishedDate("", timeFormat, "", -1);
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//			System.out.println(timeFormat + ": " + sf.format(dateline));
		}
	}

	public static void testString(String text) {
		PublishTimeExtractor te = new PublishTimeExtractor();

		Date dateline = te.getNewsPublishedDate("", text, "", -1);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		System.out.println(text + ": " + sf.format(dateline));

	}

	public static String[] getAllTimeFormats(String fileName) {

		StringBuffer sb = new StringBuffer();
		try {

			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String temp = "";
			while ((temp = br.readLine()) != null) {
				if (!temp.trim().equals(""))
					sb.append(temp).append(";");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString().split(";");
	}

	public static String getWeb(String url, boolean proxyIf) {
		String ret = "";
		WebPageDownloader wd = new WebPageDownloader(url);
		if (proxyIf == true) {
			wd.setUseProxyIf(true);
			wd.setProxyUrl("115.160.154.50");
			wd.setProxyPort("443");
			wd.setProxyUser("pico");
			wd.setProxyPwd("pico2009server");
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
	public static String clearHtml(String webSourcePage) {
		String target = webSourcePage.replaceAll("(?i)(?s)<style.*?</style>",
				"").replaceAll("(?i)(?s)<(no)?script.*?</(no)?script>", "")
				.replaceAll("(?i)(?s)<select.*?</select>", "").replaceAll(
						"(?i)(?s)<!--.*?-->", "");
		target = target.replaceAll("&nbsp;?", " ").replaceAll("\\s+", " ");
		return target;
	}
	// static class NewsDate implements Comparable<NewsDate> {
	//
	// public int compareTo(NewsDate o) {
	//
	// if (this.getLength() > o.getLength())
	// return 1;
	// else if (this.getLength() < o.getLength())
	// return 0;
	// else
	// return -1;
	// }
	//
	// private String date;
	// private int length;
	//
	// public String getDate() {
	// return date;
	// }
	//
	// public void setDate(String date) {
	// this.date = date;
	// }
	//
	// public int getLength() {
	// return length;
	// }
	//
	// public void setLength(int length) {
	// this.length = length;
	// }
	//
	// }

}
