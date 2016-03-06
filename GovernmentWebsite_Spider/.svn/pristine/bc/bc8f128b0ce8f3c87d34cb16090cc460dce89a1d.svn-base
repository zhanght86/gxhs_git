//package com.meiah.test;
//
//import java.util.zip.CRC32;
//
//import org.apache.log4j.Logger;
//
//import com.meiah.dao.NewsDao;
//import com.meiah.htmlParser.ContentExtractor;
//import com.meiah.htmlParser.ListPageDecider;
//import com.meiah.po.News;
//import com.meiah.util.Simi;
//import com.meiah.util.WebPageDownloader;
//import com.meiah.webCrawlers.SiteCrawler;
//
//public class TestConform {
//	private static Logger logger = Logger.getLogger(TestConform.class);
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//
//		String url = "http://www.ynfzb.cn/Article/snxw/201012/59334.html";
//		TestConform t = new TestConform();
//		t.testPage(url, false);
//
//		// TestNewsTime.testAll();
//
//	}
//
//	public void testPage(String url, boolean proxyIf) {
//
//		String webContent = getWeb(url, proxyIf);
//		String content = "";
//		try {
//			content = ContentExtractor.extractMainContent("", "", webContent);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		logger.info(content);
//		News ne = new News();
//		ne.setUrl(url);
//		ne.setContent(content);
//		boolean isSimilarNews = isConformExist(ne);
//
//	}
//
//	public void testPage(String url) {
//		testPage(url, false);
//	}
//
//	public static String getWeb(String url, boolean proxyIf) {
//		String ret = "";
//		WebPageDownloader wd = new WebPageDownloader(url);
//		if (proxyIf == true) {
//			wd.setUseProxyIf(true);
//			wd.setProxyUrl("210.177.242.75");
//			wd.setProxyPort("443");
//			wd.setProxyUser("lst");
//			wd.setProxyPwd("lst2010");
//		}
//		try {
//			ret = wd.getPageContent();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return ret;
//	}
//
//	public static String getWeb(String url) {
//		String ret = getWeb(url, false);
//		return ret;
//	}
//
//	/**
//	 * 通过最长句判断当前新闻是否存在已经下载的站内的相似新闻
//	 * 
//	 * @param ne
//	 * @return flag true=存在，false=不存在
//	 */
//	public boolean isConformExist(News ne) {
//		boolean flag = false;
//		String content = ne.getContent();
//		CRC32 c = new CRC32();
//		String conformText = Simi.getSimitxt(content);// 提取正文的最长句
//		c.update(conformText.getBytes());
//		long conformCrc = c.getValue();
//		logger.info(conformText);
//		if (SiteCrawler.conformMap != null) {
//			String url = SiteCrawler.conformMap.get(conformCrc);
//			if (url != null) {
//				if (!ne.getUrl().equals(""))//
//				{
//					logger.info(ne.getUrl() + "，存在相似新闻:" + url);
//					flag = true;
//				}
//			} else {
//				flag = false;
//				SiteCrawler.conformMap.put(conformCrc, ne.getUrl());
//			}
//		} else {
//			flag = NewsDao.getInstance().isNewsExist(ne.getTaskid(),
//					ne.getUrl(), conformCrc);
//		}
//
//		if (c.getValue() != 0) {
//			ne.setConform(c.getValue() + "");
//		} else {
//			ne.setConform("");
//		}
//		return flag;
//	}
//
//}
