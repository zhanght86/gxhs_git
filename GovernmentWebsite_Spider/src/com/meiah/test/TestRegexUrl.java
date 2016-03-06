//package com.meiah.test;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import com.meiah.dao.CenterBaseDao;
//import com.meiah.htmlParser.LinkExtractor;
//import com.meiah.po.Link;
//import com.meiah.po.SiteConfig;
//import com.meiah.po.TaskLink;
//import com.meiah.util.JavaUtil;
//import com.meiah.util.SysConstants;
//import com.meiah.util.WebPageDownloader;
//
//public class TestRegexUrl {
//	/**
//	 * @param 传入链接
//	 * @return 是否正文页面
//	 */
//	protected static int detectLinkType(TaskLink link, SiteConfig siteConfig) {
//		String url = link.getUrl();
//		if (siteConfig != null) {
//			String newsUrlPatterns = siteConfig.getNewsUrlPattern();
//			String[] patterns = newsUrlPatterns.split("[,，]");
//			for (String pattern : patterns) {
//				if (JavaUtil.isAllMatch(url, pattern))
//					return SysConstants.PAGETYPE_CONTENT;
//			}
//			return SysConstants.PAGETYPE_LIST;
//		} else {
//			return SysConstants.PAGETYPE_UNKNOWN;
//		}
//	}
//
//	/**
//	 * @param
//	 * @throws Exception
//	 */
//	public static void main(String[] args) throws Exception {
//		System.out.println(JavaUtil.getHost1("http://cn.wsj.com/gb/"));
//		String taskUrl = "http://cn.wsj.com/gb/";
//		String regex = "";
//		// System.out.println(testSiteConfig(taskUrl));
//		if (testSiteConfig(taskUrl, regex)) {
//			System.out.println("通过");
//		}
//	}
//
//	public static void insertOrUpdateSiteConfig(SiteConfig config)
//			throws SQLException {
//		CenterBaseDao dao = new CenterBaseDao();
//		String sqlSelect = "select top 1 * from n_SiteConfigs where topDomain='"
//				+ config.getTopDomain() + ";";
//		String sqlInsert = "insert n_SiteConfigs (topDomain,contentUrlRegex) values ("
//				+ config.getTopDomain()
//				+ ","
//				+ config.getNewsUrlPattern()
//				+ ")";
//		String sqlUpdate = "update n_SiteConfigs set contentUrlRegex=contentUrlRegex+'"
//				+ "," + config.getNewsUrlPattern() + "'";
//		if (dao.query(sqlSelect).size() == 0) {
//			dao.save(sqlInsert);
//		} else {
//			dao.save(sqlUpdate);
//		}
//	}
//
//	public static boolean testSiteConfig(String taskUrl, String urlRegex)
//			throws Exception {
//
//		// TODO Auto-generated method stub
//
//		WebPageDownloader wd = new WebPageDownloader(taskUrl);
//		String webContent = wd.getPageContent();
//
//		Link link = new Link();
//		link.setUrl(taskUrl);
//		List<Link> urls = LinkExtractor.getUrlsByParser(link, webContent);
//		String topDomain = JavaUtil.getHost1(taskUrl);
//
//		SiteConfig config = new SiteConfig();
//		config.setNewsUrlPattern(urlRegex);
//		config.setTopDomain(topDomain);
//		int i = 0;
//		for (Link l : urls) {
//			if (detectLinkType(new TaskLink(l), config) == SysConstants.PAGETYPE_CONTENT) {
//				System.out.println(l.getTitle() + ":" + l.getUrl());
//				i++;
//			}
//			if (i > 10) {
//				return true;
//			}
//		}
//		return false;
//	}
//}
