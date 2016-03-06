package com.meiah.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.meiah.po.Link;
import com.meiah.util.JavaUtil;
import com.meiah.util.WebPageDownloader;

public class TestJudge {
	private Logger logger = Logger.getLogger(TestJudge.class);
	public static void main(String[] args) {
		TestJudge judge = new TestJudge();
//		int flag = judge.getUrlPosInNewsWeb("http://finance.sina.com.cn/stock/jsy/20150618/150322467111.shtml", "http://www.sina.com.cn/");
		boolean flag = judge.isExitHomePage("http://finance.sina.com.cn/stock/jsy/20150618/150322467111.shtml", "http://www.sina.com.cn/");
//		System.out.println(flag);
	}
	
	/**
	 * 判断新闻URL是否在网站首页
	 * @param detailUrl
	 * @param homePageUrl
	 * @return
	 */
	public boolean isExitHomePage(String detailUrl, String homePageUrl){
		boolean flag = false;
		Set<String> homePageUrls = getHomePageUrls(homePageUrl);
		if(homePageUrls != null && homePageUrls.contains(detailUrl)){
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 判断新闻URL是否在App首页
	 * @param detailUrl
	 * @param homePageUrl
	 * @return
	 */
	public boolean isExitAppHomePage(String detailUrl, String homePageUrl){
		boolean flag = false;
		Set<String> homePageUrls = getAppPageUrls(homePageUrl);
		if(homePageUrls != null && homePageUrls.contains(detailUrl)){
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 判断新闻URL是否在要闻模块
	 * @param detailUrl
	 * @param homePageUrl
	 * @return
	 */
	public boolean isExitHomePageImportantNews(String detailUrl, String homePageUrl){
		boolean flag = false;
		Set<String> homePageUrls = getSinaImportantNewsUrls(homePageUrl);
		if(homePageUrls != null && homePageUrls.contains(detailUrl)){
			flag = true;
		}
		return flag;
	}

	/**
	 * 提取网站首页所有的URL
	 * @param homePageUrl
	 * @return
	 */
	private Set<String> getHomePageUrls(String homePageUrl) {
		Set<String> urls = null;
		Link link = new Link();
		link.setUrl(homePageUrl);
		if (homePageUrl != null && !homePageUrl.equals("")) {
			WebPageDownloader downloader = new WebPageDownloader(homePageUrl);
			downloader.setCheckCookie(true);
			downloader.setCheckRedirect(true);
			String webContent = "";
			try {
				webContent = downloader.getPageContent();
			} catch (Exception e) {
				logger.error("下载主页出现异常：" + homePageUrl, e);
			}
			ArrayList<Link> urlList = LinkExtractor.getUrlsByParser(link, webContent);
			if (urlList.size() <= 0)
				return null;
			urls = new HashSet<String>();
			for (int i = 0; i < urlList.size(); i++) {
				String url = urlList.get(i).getUrl();
				if (url != null && !url.equals(""))
					urls.add(url);
			}
		}
		return urls;
	}
	
	/**
	 * 提取APP首页所有的URL
	 * @param homePageUrl
	 * @return
	 */
	private Set<String> getAppPageUrls(String homePageUrl){
		Set<String> urls = null;
		WebPageDownloader wd = new WebPageDownloader(homePageUrl);
		String json = "";
		try {
			json = wd.getPageContent();
		} catch (Exception e) {

			e.printStackTrace();
		}
		JSONObject obj = JSONObject.fromObject(json);
		JSONArray newsArray = obj.getJSONObject("data").getJSONArray("list");
		urls = new HashSet<String>();
		for (int i = 0; i < newsArray.size(); i++) {
			try {
				JSONObject newsObj = newsArray.getJSONObject(i);
				String url = newsObj.getString("link");
				if(url != null && !url.equals("")){
					urls.add(url);
				}
			} catch (Exception e) {
				logger.error("获取Sina客户端新闻链接出现异常！" + homePageUrl, e);
			}
		}
		
		return urls;
	}
	
	/**
	 * 提取网站要闻模块的所有URL
	 * @param homePageUrl
	 * @return
	 */
	private Set<String> getSinaImportantNewsUrls(String homePageUrl){
		Set<String> urls = null;
		if (homePageUrl != null && !homePageUrl.equals("")) {
			WebPageDownloader downloader = new WebPageDownloader(homePageUrl);
			downloader.setCheckCookie(true);
			downloader.setCheckRedirect(true);
			String webContent = "";
			try {
				webContent = downloader.getPageContent();
			} catch (Exception e) {
				logger.error("下载主页出现异常：" + homePageUrl, e);
			}
			
			Document doc = null;
			try {
				doc = JavaUtil.getDocument(webContent);
			} catch (Exception e) {
				logger.error("解析页面出现异常！" + homePageUrl);
			}
			
			try {
				Node node = XPathAPI.selectSingleNode(doc, ".//UL[@id='syncad_0']");
				NodeList nodeList = XPathAPI.selectNodeList(node, ".//A");
				if(nodeList.getLength() == 0){
					return null;
				}
				
				urls = new HashSet<String>();
				for(int i = 0; i < nodeList.getLength(); i++){
					Node item = nodeList.item(i);
					String url = JavaUtil.getNodeValue(item, "href");
					if(url != null && !url.equals("")){
						urls.add(url);
					}
				}
			} catch (TransformerException e) {
				logger.error("解析要闻模块异常:" + e);
			}
		}
		return urls;
	}
	
	/**
	 * 计算detailUrl在网站模块中的位置
	 * @param detailUrl
	 * @param homePageUrl
	 * @return
	 */
	private Integer getUrlPosInNewsWeb(String detailUrl, String homePageUrl){
		int urlPos = 0;
		if (homePageUrl != null && !homePageUrl.equals("")) {
			WebPageDownloader downloader = new WebPageDownloader(homePageUrl);
			downloader.setCheckCookie(true);
			downloader.setCheckRedirect(true);
			String webContent = "";
			try {
				webContent = downloader.getPageContent();
			} catch (Exception e) {
				logger.error("下载主页出现异常：" + homePageUrl, e);
			}
			
			Document doc = null;
			try {
				doc = JavaUtil.getDocument(webContent);
			} catch (Exception e) {
				logger.error("解析页面出现异常！" + homePageUrl);
			}
			
			try {
				Node node = XPathAPI.selectSingleNode(doc, ".//UL[@id='syncad_0']");
				NodeList nodeList = XPathAPI.selectNodeList(node, ".//A");
				if(nodeList.getLength() == 0){
					return null;
				}
				
				for(int i = 0; i < nodeList.getLength(); i++){
					Node item = nodeList.item(i);
					String url = JavaUtil.getNodeValue(item, "href");
					
					if(url.equals(detailUrl)){
						urlPos = i + 1;
					}
				}
			} catch (TransformerException e) {
				logger.error("解析要闻模块异常:" + e);
			}
		}
		
		return urlPos;
	}
	
	/**
	 * 
	 * @param detailUrl
	 * @param homePageUrl
	 * @return
	 */
	private Integer getUrlPosInAppNews(String detailUrl, String homePageUrl){
		int urlPos = 0;
		WebPageDownloader wd = new WebPageDownloader(homePageUrl);
		String json = "";
		try {
			json = wd.getPageContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject obj = JSONObject.fromObject(json);
		JSONArray newsArray = obj.getJSONObject("data").getJSONArray("list");
		for (int i = 0; i < newsArray.size(); i++) {
			try {
				JSONObject newsObj = newsArray.getJSONObject(i);
				String url = newsObj.getString("link");
				
				if(detailUrl.equals(url)){
					urlPos = i + 1;
				}
			} catch (Exception e) {
				logger.error("获取Sina客户端新闻链接出现异常！" + homePageUrl, e);
			}
		}
		
		return urlPos;
	}
}
