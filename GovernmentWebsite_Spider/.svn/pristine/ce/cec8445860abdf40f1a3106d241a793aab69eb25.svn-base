package com.meiah.webCrawlers.SitePlugin.Gxhs.finance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.gxhs.mongodb.entry.news.NewsGeneric;
import com.meiah.dao.TaskDao;
import com.meiah.po.SiteConfig;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.po.WebPage;
import com.meiah.trs.NewsTrsDo;
import com.meiah.util.Config;
import com.meiah.util.JavaUtil;
import com.meiah.util.MD5Utils;
import com.meiah.util.SysConstants;
import com.meiah.util.SysObject;
import com.meiah.webCrawlers.SiteCrawler;
import com.meiah.webCrawlers.SitePlugin.PageResolver_General;

public class PageResolver_Dynews extends PageResolver_General {
	protected Logger logger = Logger.getLogger(PageResolver_Dynews.class);
	
	public PageResolver_Dynews(Task task) {
		super(task);
	}

	/**
	 * 提取新闻标题
	 * 
	 * @param beginTitle
	 * @param endTitle
	 * @param webContent
	 * @return
	 */
	protected String getNewsTitle(SiteConfig conf, WebPage page, Document doc) {
		String title = "";
		try {
			String webContent = page.getWebContent();
			TaskLink link = page.getLink();

			title = getLocText(conf.getTitleLocation(), page, doc);

			if (title != null) {
				title = title.replaceAll("(?is)<style.*?</style>", "").replaceAll("(?is)<(no)?script.*?((/>)|(</(no)?script>))", "")
						.replaceAll("(?is)<select.*?</select>", "").replaceAll("(?is)<!--.*?-->", "");
				title = title.replaceAll("&gt;?", ">").replaceAll("&lt;?", "<");
				title = title.replaceAll("&nbsp;", " ").replaceAll("(?s)(?i)<.*?>", "");
				title = title.trim();
				title = title.replaceAll("\r\n", "")
						.replaceAll("\n", "").replaceAll("\t", "")
						.replaceAll(">>", "").replaceAll("　　", "")
						.replaceAll(" ", "").replaceAll("■  ", "")
						.replaceAll("&gt;", "").replaceAll("&lt;", "")
						.replaceAll(" ", "").replaceAll("\t","")
						.replaceAll("&ldquo;", "").replaceAll("&rdquo;", "");
			}

		} catch (Exception e) {
			logger.error("提取标题出现异常！", e);
		}

		return title;
	}
}
