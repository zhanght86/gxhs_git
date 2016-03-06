package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import com.meiah.htmlParser.PublishTimeExtractor;
import com.meiah.po.Task;
import com.meiah.po.WebPage;
import com.meiah.webCrawlers.SitePlugin.PageResolver_General;

/**
 * 清华大学当代国际关系研究院卡内基全球政策中心
 * @author lyao
 * @date 2015-10-10
 */
public class PageResolver_QhCarnegie extends PageResolver_General{

	public PageResolver_QhCarnegie(Task task) {
		super(task);
	}

	@Override
	protected Date getNewsPublishTime(Task task, WebPage page, Document doc1) {
		Date publishTime = null;
		String content = StringUtils.EMPTY;
		try {
			Date linktime = page.getLink().getPublishTime();
			if (linktime != null)
				return linktime;
			content = getLocText(task.getSiteConfig().getPublishTimeLocation(), page, doc1);
			
			// logger.info(content);
			if (StringUtils.isNotBlank(content) && !content.replaceAll("\\s+", "").equals("")) {
				content = StringUtils.replaceEach(content, new String[] { "年", "月", "日" }, new String[] { "-", "-", "" });
				content = " " + content + " 00:00:00";
				PublishTimeExtractor te = new PublishTimeExtractor();
				publishTime = te.getNewsPublishedDate(page.getLink().getUrl(), content, task.getDateFormat(), task.getDatePos());
			}

			if (content == null || content.equals("")) {
				PublishTimeExtractor te = new PublishTimeExtractor();
				String webContent = clearHtml(page.getWebContent());
				publishTime = te.getNewsPublishedDate(page.getLink().getUrl(), webContent, task.getDateFormat(), task.getDatePos());
			}
		} catch (Exception e) {
			logger.error("提取新闻发布日期出现异常！", e);
		}
		// Date publishTime = null;
		return publishTime;
	}

}
