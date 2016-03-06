package com.meiah.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;

public class GenerateTask {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// TODO Auto-generated method stub
		String insersql = " INSERT [n_tasks] (  [Taskid] , [Turl] , [Tname] , "
				+ "[Ttype] , "
				+ "[Lang] , [SleepTime] , [MaxThread] , [Prefix] , [Substr] , "
				+ "[BeginTitle] , [EndTitle] , [BeginCode] , [EndCode] , "
				+ "[ProxyUrl] , [ProxyUser] , [ProxyPassword] , [Pages] , [Layers] , "
				+ "[Timeout] , [Status] , [Userid] , [DateFormat] , [DatePos] , "
				+ "[DatePrefix] , [PageType] , [InTime] , [NextRunTime]  ) "
				+ "VALUES (  432143124 , 'http://www.citygf.com/news/News_001011/' , "
				+ "'安徽新闻网-企业在线' , '测试' , -1 , 3600 , 2 , '' , '' , '' , '' , '' , '' ,"
				+ " '' , '' , '' , 500 , 4 , 60 , 1 , 1 , '' , 1 , 'null' , '' , "
				+ "'" + sdf.format(new Date()) + ".000' , '"
				+ sdf.format(new Date()) + ".000'  )";
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
				 String tname = info[0];
				String turl = info[1];

				CRC32 c = new CRC32();
				c.update(turl.getBytes());
				long siteUrlCRC = c.getValue();
				//String tname = JavaUtil.getHost1(turl);
				String sql = insersql
						.replaceFirst("432143124", siteUrlCRC + "")
						.replaceFirst("安徽新闻网-企业在线", tname)
						.replaceFirst(
								"http://www.citygf.com/news/News_001011/", turl);
				// System.out.println(sql);
//
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
