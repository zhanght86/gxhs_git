package com.meiah.webCrawlers.SitePlugin.Gxhs.thinktank;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import com.meiah.htmlParser.PublishTimeExtractor;
import com.meiah.po.Task;
import com.meiah.po.WebPage;
import com.meiah.webCrawlers.SitePlugin.PageResolver_General;

/**
 * 北京大学国家发展研究院
 * @author lyao
 * @date 2015-10-10
 */
public class PageResolver_Siis extends PageResolver_General{

	public PageResolver_Siis(Task task) {
		super(task);
	}

	@Override
	protected Date getNewsPublishTime(Task task, WebPage page, Document doc1) {
		Date publishTime = null;
		String content = StringUtils.EMPTY;
		try {
			
			if (StringUtils.equalsIgnoreCase(page.getLink().getRefererUrl(), "http://www.siis.org.cn/index.php?m=content&c=index&a=lists&catid=15")
					|| StringUtils.startsWithIgnoreCase(page.getLink().getUrl(), "http://www.siis.org.cn/index.php?m=content&c=index&a=show&catid=15&id=")) {
				return new Date();
			}
			
			Date linktime = page.getLink().getPublishTime();
			if (linktime != null)
				return linktime;
			content = getLocText(task.getSiteConfig().getPublishTimeLocation(), page, doc1);
			
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

}
