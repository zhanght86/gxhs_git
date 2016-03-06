package com.meiah.test;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.meiah.dao.RunningTaskDao;
import com.meiah.po.RunningTask;
import com.meiah.po.TaskLink;
import com.meiah.util.SysConstants;
import com.meiah.webCrawlers.PageCrawler;
import com.meiah.webCrawlers.PluginFactory;
import com.meiah.webCrawlers.SiteCrawler;

public class TestPage {
	private static Logger logger = Logger.getLogger(TestPage.class);

	/**
	 * @param
	 */
	public static void main(String[] args) {
		Properties props = new Properties();
		try {

			InputStream istream = SiteCrawler.class.getResourceAsStream("/log4j.properties");
			props.load(istream);
			istream.close();

			props.setProperty("log4j.rootLogger", "debug,NEWS,logfile,logfile1");

			// 重新配置后，日志会打到新的文件去。
			PropertyConfigurator.configure(props);// 装入log4j配置信息

		} catch (Exception e) {
			logger.error("装入属性文件异常 Exception ", e);
		}
		// TODO Auto-generated method stub
		// String taskUrl = "";
		String taskid = "2072358318";
		String url = "http://www.cryptome.org";
		try {
			SiteCrawler c = new SiteCrawler(taskid);
			RunningTaskDao.getInstance().deleteRunningTask((RunningTask) SiteCrawler.task);
			RunningTaskDao.getInstance().addRunningTask((RunningTask) SiteCrawler.task);
			TaskLink link = new TaskLink();
			// String url = "http://paper.people.com.cn/rmrb/";
			link.setUrl(url);
			link.setLevel(0);
			link.setLinkType(SysConstants.PAGETYPE_LIST);
			if (SiteCrawler.task.pluginMode == true) {
				PageCrawler crawler = PluginFactory.getInstance().getPageCrawler(link, SiteCrawler.task);
				if (crawler != null)
					crawler.start();
				else {
					new PageCrawler(link, SiteCrawler.task).start();
				}
			} else {
				new PageCrawler(link, SiteCrawler.task).start();
			}
			// pc.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
