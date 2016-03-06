package com.meiah.test;

import com.meiah.util.Config;
import com.meiah.util.MD5Utils;
import com.meiah.webCrawlers.ClientCenter;

public class CheckNewsExist {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		// TODO Auto-generated method stub
		String url = "http://news.google.com/news/more?ncl=dBGeXiDqQ3Gd1mM0es0G7mjqqL2cM&authuser=0&ned=cn&topic=n";
		Config.setServerIp("110.80.33.98");
		Config.setServerPort(39578);
		
		String urlmd5 = MD5Utils.getMD5(url.getBytes("gbk"));
		String solrSearchURL = "http://110.80.33.98:4055/SearchCloud/s?wt=xml&q=GUID:" + urlmd5;
//		System.out.println("query url:"+url);
//		System.out.println("mongo exist:" + ClientCenter.isNewsExist(url));
		ClientCenter.endSocket();
//		System.out.println(solrSearchURL);
		String html = HttpClientFetcher.getPageContent(solrSearchURL);
		if(html.equals(""))
//			System.out.println("索引查询失败");
		//System.out.println(html);
//		System.out.println("index exist:" + (html.indexOf("numFound=\"0\"") == -1 ? "true" : "false"));
		HttpClientGenerator.shutdown();
	}
}
