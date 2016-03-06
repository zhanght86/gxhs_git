package com.meiah.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.CRC32;

import com.meiah.util.JavaUtil;

public class GenerateForeignTask {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		String insersql = " INSERT [n_tasks] (  [Taskid] , [Turl] , [Tname] , [Ttype] , "
				+ "[Lang] , [SleepTime] , [MaxThread] , [Prefix] , [Substr] , "
				+ "[BeginTitle] , [EndTitle] , [BeginCode] , [EndCode] , "
				+ "[ProxyUrl] , [ProxyPort],[ProxyUser],  [ProxyPassword] , [Pages] , [Layers] , "
				+ "[Timeout] , [Status] , [Userid] , [DateFormat] , [DatePos] , "
				+ "[DatePrefix] , [PageType] , [InTime] , [NextRunTime]  ) "
				+ "VALUES (  432143124 , 'http://www.citygf.com/news/News_001011/' , "
				+ "'安徽新闻网-企业在线' , '测试1' , -1 , 3600 , 1 , '' , '' , '' , '' , '' , '' ,"
				+ " '115.160.154.50' , 443,'pico' , 'pico2009server' , 200 , 3 , 60 , 1 , 1 , '' , 1 , 'null' , '' , "
				+ "'2011-11-14 09:40:41.000' , '2012-11-24 11:14:03.717'  )";
		InputStream stream = ClassLoader
				.getSystemResourceAsStream("insertTask.txt");
		InputStreamReader isr = new InputStreamReader(stream, "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String temp = null;
		// System.out.println(ClassLoader.getSystemResource("").getPath());
		File reslutFile = new File(ClassLoader.getSystemResource("").getPath()
				+ "insert.sql");
		if (!reslutFile.exists())
			reslutFile.createNewFile();
		// System.setOut(new PrintStream(reslutFile, "gbk"));
//		System.out.println("use taskcenter ");
//		System.out.println("go ");
		while ((temp = br.readLine()) != null) {
			try {
				// System.out.println(temp);
				String[] info = temp.split("[,，		　	]+");
				// System.out.println(temp);
				// String tname = info[1];
				String turl = info[0];

				CRC32 c = new CRC32();
				c.update(turl.getBytes());
				long siteUrlCRC = c.getValue();
				String tname = JavaUtil.getHost1(turl);
				String sql = insersql
						.replaceFirst("432143124", siteUrlCRC + "")
						.replaceFirst("安徽新闻网-企业在线", tname)
						.replaceFirst(
								"http://www.citygf.com/news/News_001011/", turl);
				// System.out.println(sql);

//				System.out.println(sql);
			} catch (Exception e) {
				System.err.println(temp);
				e.printStackTrace();
			}
		}
		// WebPageDownloader wd = new
		// WebPageDownloader("http://www.xm.gov.cn/");
		// String web = wd.getPageContent();
		// Document doc = JavaUtil.getDocument(web);
		// NodeList siteNodes = XPathAPI.selectNodeList(doc,
		// ".//TABLE[@id='table_151']/TBODY//A");
		// for (int i = 0; i < siteNodes.getLength(); i++) {
		// Node siteNode = siteNodes.item(i);
		// String siteName = JavaUtil.getTextContent(siteNode);
		// String siteUrl = JavaUtil.getNodeValue(siteNode, "href");
		// CRC32 c = new CRC32();
		// c.update(siteUrl.getBytes());
		// long siteUrlCRC = c.getValue();
		// String sql = insersql.replaceFirst("1431", siteUrlCRC + "")
		// .replaceFirst("厦门政府", siteName).replaceFirst(
		// "http://www.xm.gov.cn/", siteUrl);
		// // System.out.println(sql);
		// System.out.println(sql);
		// }

	}
}
