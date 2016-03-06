package com.meiah.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;

import com.meiah.httpclient.HttpFetcher;
import com.meiah.util.Config;
import com.meiah.util.JavaUtil;
import com.meiah.util.MD5Utils;
import com.meiah.util.WebPageDownloader;
import com.meiah.webCrawlers.ClientCenter;

public class CheckNewsExistBatch {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		try {
			InputStream stream = ClassLoader
					.getSystemResourceAsStream("CheckNews.txt");
			InputStreamReader isr = new InputStreamReader(stream, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String temp = null;

			while ((temp = br.readLine()) != null) {
				try {
					String url = temp;
					Config.setServerIp("110.80.33.98");
					Config.setServerPort(39578);

					String urlmd5 = MD5Utils.getMD5(url.getBytes("gbk"));
					String solrSearchURL = "http://110.80.33.98:4055/SearchCloud/s?wt=xml&q=GUID:"
							+ urlmd5;
//					System.out.println("query url:" + url);
//					System.out.println("mongo exist:"
//							+ ClientCenter.isNewsExist(url));
//					System.out.println(solrSearchURL);
					String html = HttpClientFetcher
							.getPageContent(solrSearchURL);
					// System.out.println(html);
//					System.out.println("index exist:"
//							+ (html.indexOf("numFound=\"0\"") == -1 ? "true"
//									: "false"));
				} catch (Exception e) {
					System.err.println(temp);
					e.printStackTrace();
				}
			}
		} finally {
			ClientCenter.endSocket();
			HttpClientGenerator.shutdown();
		}

	}
}
