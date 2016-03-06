package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import com.meiah.htmlParser.PublishTimeExtractor;
import com.meiah.po.SiteConfig;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.po.WebPage;
import com.meiah.webCrawlers.SitePlugin.PageResolver_General;

/**
 * 北京大学国家发展研究院
 * @author lyao
 * @date 2015-10-10
 */
public class PageResolver_Unirule extends PageResolver_General{

	public PageResolver_Unirule(Task task) {
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
			
			if (StringUtils.isNotBlank(content)) {
				int endPos = StringUtils.lastIndexOfIgnoreCase(content, "&nbsp;");
				if (endPos >= 0) {
					content = StringUtils.substring(content, 0, endPos);
				}
			}
			// logger.info(content);
			if (StringUtils.isNotBlank(content) && !content.replaceAll("\\s+", "").equals("")) {
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
		return publishTime;
	}

	@Override
	protected String getNewsAuthor(SiteConfig conf, WebPage page, Document doc) {
		String newsAuthor = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String author = page.getLink().getAuthor();
			if (author != null && !author.trim().equals(""))
				return author;
			newsAuthor = getLocText(conf.getAuthorLocation(), page, doc);

			int pos = StringUtils.indexOfIgnoreCase(newsAuthor, "：");
			if (pos >= 0) {
				webContent = webContent.replaceAll(
						"(?is)<marquee.*?</marquee>", "").replaceAll(
						"(?s)(?i)<.*?>", "").replaceAll("\\s+", " ");
				newsAuthor = StringUtils.substring(newsAuthor, 0, pos);
				newsAuthor = newsAuthor.replaceAll("&nbsp;", " ").replaceAll(
						"(?s)(?i)<.*?>", "");
			} else {
				newsAuthor = null;
			}
		} catch (Throwable e) {
			logger.error("抓取新闻作者出现异常: " + link.getUrl(), e);
		}
		return newsAuthor;
	}

}
