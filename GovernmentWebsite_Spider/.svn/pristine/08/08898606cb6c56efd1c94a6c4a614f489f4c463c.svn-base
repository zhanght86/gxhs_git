package com.meiah.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.CRC32;

import com.meiah.util.JavaUtil;

public class CopyOfGenerateTask {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		String insersql = "INSERT INTO `n_tasks` (Taskid,Turl,Tname,Ttype,Lang,"
				+ "SleepTime,MaxThread,Prefix,Substr,"
				+ "Begintitle,EndTitle,BeginCode,EndCode,"
				+ "ProxyUrl,ProxyPort,ProxyUser,ProxyPassword,"
				+ "Pages,Layers,Timeout,Status,Userid,"
				+ "DateFormat,DatePos,DatePrefix,PageType,intime,NextRunTime,"
				+ "getCommentIf,homePageUrl,"
				+ "beginSource,endSource,beginAuthor,endAuthor,"
				+ "isSavePic,"
				+ "channel,site_id,site_name,"
				+ "board_class_tag,board_id) VALUES "
				+ "(583279920,'http://news.qq.com/','腾讯新闻','评论测试',"
				+ "-1,1800,2,'','','','','','','',NULL,'','',"
				+ "50,2,60,1,1,'',1,'','','2010-11-30 09:27:54',"
				+ "'2012-12-24 20:26:51',1,NULL,NULL,NULL,NULL,NULL,NULL,"
				+ "1,12345687,'腾讯新闻','m103_114,m103',87354542);";
		// m101
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
		// System.out.println("use taskcenter ");
		// System.out.println("go ");
		StringBuffer sbf = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			try {
				// System.out.println(temp);
				// String[] info = temp.split("[,，		　	]+");
				// // System.out.println(temp);
				// String tname = info[0];
				String turl = temp;
				String tname = JavaUtil.getHost1(turl);
				if (tname == null || tname.equals("")) {
					tname = JavaUtil.getHost2(turl);
				}
				CRC32 c = new CRC32();
				c.update(turl.getBytes());
				long siteUrlCRC = c.getValue();
				sbf.append(siteUrlCRC).append(",");
				String sql = insersql
						.replaceFirst("583279920", siteUrlCRC + "").replaceAll(
								"腾讯新闻", tname).replaceAll("评论测试", "foreign")
						.replaceAll("http://news.qq.com/", turl);
				// System.out.println(sql);

//				System.out.println(sql);
			} catch (Exception e) {
				System.err.println(temp);
				e.printStackTrace();
			}
		}
		String taskids = sbf.toString();
		String updatesql = "update n_tasks set site_id=id,board_id=id+1 where Taskid in ("
				+ taskids.substring(0, taskids.length() - 1) + "); ";
//		System.out.println(updatesql);
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
