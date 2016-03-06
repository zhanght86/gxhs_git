package com.meiah.webCrawlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gxhs.mongodb.dao.base.MongoBaseDao;
import com.gxhs.mongodb.entry.news.NewsGeneric;
import com.ibm.icu.text.SimpleDateFormat;
import com.meiah.dao.TaskDao;
import com.meiah.htmlParser.ContentExtractor;
import com.meiah.htmlParser.ListPageDecider;
import com.meiah.htmlParser.PublishTimeExtractor;
import com.meiah.po.News;
import com.meiah.po.RunningTask;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.po.WebPage;
import com.meiah.trs.NewsTrsDo;
import com.meiah.util.BaijiaxingMFilter;
import com.meiah.util.BaijiaxingSFilter;
import com.meiah.util.Config;
import com.meiah.util.ContentExtractorUtil;
import com.meiah.util.JavaUtil;
import com.meiah.util.KeywordFilter;
import com.meiah.util.MD5Utils;
import com.meiah.util.MyDate;
import com.meiah.util.SysObject;
import com.meiah.util.TitleKeywordFilter;

public class PageResolver extends Thread {
	private Logger logger = Logger.getLogger(PageResolver.class);
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected Task task;
	protected RunningTask rtask;
	protected KeywordFilter keyFilter;
	protected TitleKeywordFilter titleKeyFilter;
	protected BaijiaxingSFilter baijiaS;
	protected BaijiaxingMFilter baijiaM;
	public PageResolver(Task task) {
		this.task = task;
		this.keyFilter = KeywordFilter.getInstance();
		this.baijiaS = BaijiaxingSFilter.getInstance();
		this.baijiaM = BaijiaxingMFilter.getInstance();
		this.titleKeyFilter = TitleKeywordFilter.getInstance();
	}

	public void run() {
		SysObject.resloveThreads.incrementAndGet();
		mainPro();
		SysObject.resloveThreads.decrementAndGet();
	}

	private void mainPro() {
		try {
			int save_BatchCount = Config.getSave_BatchCount();
			rtask = (RunningTask) task;
			if (rtask.getRunstate() == Task.STOP) {
				logger.info("任务结束，最后一次保存！");
				ArrayList<WebPage> webPages = getSavePages(-1);
				resolvePage(webPages);
				logger.info("保存完毕结束任务！当前线程数：" + SysObject.resloveThreads.get());
			} else {
				ArrayList<WebPage> webPages = getSavePages(save_BatchCount);
				resolvePage(webPages);
			}
		} catch (Exception e) {
			logger.error("解析页面出现异常！！！！！", e);
		}
	}

	protected NewsGeneric resloveNewsPage(WebPage page) {
		return null;
	}

	private NewsGeneric resloveKnownPage(WebPage page) {
		try {
			String webContent = page.getWebContent();
			webContent = clearHtml(webContent);
			TaskLink link = page.getLink();
			int pageType = 0;
			long tStart = System.currentTimeMillis();
			long t2 = 0L;
			long t3 = 0L;
			long t4 = 0L;
//			System.out.println(webContent);
			boolean isList = ListPageDecider.isList(link.getUrl(),this.task.getPageType(), webContent);
			if (isList)
				pageType = 1;
			if (this.logger.isDebugEnabled()) {
				t2 = System.currentTimeMillis() - tStart;
				this.logger.debug("列表判断耗费时间：" + t2 + "ms");
			}
			if (pageType != 1) {
				String contentBeginCode = this.task.getBeginCode();
				String contentEndCode = this.task.getEndCode();
				String content = "";
				try {
					content = ContentExtractor.extractMainContent(
							contentBeginCode, contentEndCode, webContent);
					String replace = ""; 
					if(content.contains("<?xml:namespace")) {
						replace = content.substring(content.indexOf("<?xml:namespace"));
						replace = replace.substring(0, replace.indexOf("/>") + 2);
					}
					content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;",replace }, new String[] { "", "" ,"" , "","",""});
				} catch (Exception e) {
					this.logger.error("抓取正文出现异常，链接地址: " + link.getUrl(), e);
				}
				if (this.logger.isDebugEnabled()) {
					t3 = System.currentTimeMillis() - tStart - t2;
					this.logger.debug("正文提取耗费时间：" + t3 + "ms");
				}
				boolean isContentPage = isContentPage(link.getUrl(),this.task.getPageType(), content);

				if (isContentPage) {
					NewsGeneric ne = new NewsGeneric();
					News news = new News();
					
					news.setUrl(link.getUrl());
					ne.setPage_url(link.getUrl());
					ne.set_id(MD5Utils.getMD5(ne.getPage_url().getBytes()));
					ne.setTask_id(this.task.getTaskid());
					Map<String, Task> ts = TaskDao.getInstance().getAllTaskMap();
					String siteName = ((Task) ts.get(ne.getTask_id())).getTname();
					ne.setWebsite_name(siteName);
					ne.setTask_name(siteName);
					String ipName = "";
					String ip = JavaUtil.matchWeak(ts.get(ne.getTask_id()).getUrl(), "http://([^/]*)")[1];
					if (NewsTrsDo.ips == null)
						NewsTrsDo.ips = new HashMap<String, String>();
					if (NewsTrsDo.ips.containsKey(ip)) {
						ip = NewsTrsDo.ips.get(ip);
					} else {
						InetAddress a = null;
						try {
							a = InetAddress.getByName(ip);
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						ip = a.getHostAddress();
						NewsTrsDo.ips.put(ip, a.getHostAddress());
					}
				    //ip地址所在地
					if(SysObject.ipTable.containsKey(ip)){
						ipName = SysObject.ipTable.get(ip);
					}else{
						ipName = NewsTrsDo.getAddressByIP(ip);
						if(StringUtils.isNotEmpty(ipName)){
							SysObject.ipTable.put(ip, ipName);
						}
					}

					ne.setWebsite_ip(ip);
					ne.setWebsite_ip_area(ipName);
//					ne.setIslist(pageType);
//					ne.setSpot_code(Config.getSpotID());
					String webdomain = ne.getPage_url();
					String domain = webdomain.substring(webdomain.indexOf("://") + 3);
					domain = domain.substring(0, domain.indexOf("/"));
					ne.setWebsite_domain(SiteCrawler.topDomain);
					String prefixUrl = link.getRefererUrl().substring(0,link.getRefererUrl().indexOf("://") + 3);
					ne.setWebsite_url(prefixUrl + domain + "/");
					ne.setPage_size(String.valueOf(webContent.getBytes().length));
//					if ((SiteCrawler.homePageUrls != null)
//							&& (SiteCrawler.homePageUrls.contains(ne.getUrl())))
//						ne.setIsHomePageNews(1);
					pageType = 0;
					news = ContentExtractorUtil.getNewsByHtml(webContent, news);
					String replace = "";
					if(content.contains("<?xml:namespace")) {
						replace = content.substring(content.indexOf("<?xml:namespace"));
						replace = replace.substring(0, replace.indexOf("/>") + 2);
					}
					content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;",replace }, new String[] { "", "" ,"" , "","",""});
					ne.setNews_content(content.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", "").replace("你好!退出相关阅读换一换", ""));
					PublishTimeExtractor te = new PublishTimeExtractor();
					Date dateDublished = te.getNewsPublishedDate(link.getUrl(),
							webContent, this.task.getDateFormat(),
							this.task.getDatePos());
					if(null != dateDublished)
						ne.setPage_publish_time(dateDublished);
					ne.setPage_save_time(new Date());
					InetAddress a = null;
					try {
						ne.setServer_ip(a.getLocalHost().getHostAddress());
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					ne.setNews_title(link.getTitle());
					if(StringUtils.isNotBlank(ne.getNews_title())) {
						if ((ne.getNews_title().contains(".jpg"))
								|| (link.getTitle().contains(".png"))
								|| (link.getTitle().contains(".gif"))
								|| (link.getTitle().contains(".JPG"))
								|| (link.getTitle().contains(".PNG"))
								|| (link.getTitle().contains(".GIF")) 
								|| link.getTitle().contains("详细") || link.getTitle().contains("全文")) {
//							System.out.println(link.getTitle());
							ne.setNews_title("");
						}
						else {
							ne.getNews_title().replaceAll("\r\n", "")
									.replaceAll("\n", "").replaceAll("\t", "")
									.replaceAll(">>", "").replaceAll("　　", "")
									.replaceAll(" ", "").replaceAll("■  ", "")
									.replaceAll("&gt;", "").replaceAll("&lt;", "")
									.replaceAll(" ", "").replaceAll("\t","")
									.replaceAll("&ldquo;", "").replaceAll("&rdquo;", "").replaceAll("\\s+", "");
						}
					}
					if(StringUtils.isEmpty(ne.getNews_title())) {
						news = ContentExtractorUtil.getNewsByHtml(webContent, news);
						ne.setNews_title(news.getTitle().replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\t", "")
								.replaceAll(" ", "").replaceAll("&gt;", "").replaceAll("&lt;", "").replaceAll("&ldquo;", "")
								.replaceAll("&rdquo;", "").replaceAll("	", "").replaceAll("\\s+", ""));
					}
					if(StringUtils.isEmpty(ne.getNews_title())) {
						ne.setNews_title(getNewsTitle(this.task.getBeginTitle(),
								this.task.getEndTitle(), page));
					}
					if(StringUtils.isNotBlank(ne.getNews_title())) {
						String regex = "[\\u4E00-\\u9FA5]";
						String[] chinese = JavaUtil.match(ne.getNews_title(), regex);
						if (null == chinese || chinese.length < 0) {
							ne = null;
							return ne;
						}
					}
					ne.setNews_class(task.getNewsType());
					Elements eles = news.getContentElement().getElementsByTag("img");
					List<String> imgList = new ArrayList<String>();
					for (int i = 0; i < eles.size(); i++) {
						String imgUrl = "";
						String imgURL = ((Element) eles.get(i)).attr("src");
						if ((imgURL.endsWith(".jpg"))
								|| (imgURL.endsWith(".png"))
								|| (imgURL.endsWith(".gif"))
								|| (imgURL.endsWith(".JPG"))
								|| (imgURL.endsWith(".PNG"))
								|| (imgURL.endsWith(".GIF"))) {
							imgUrl = imgUrl+ completeImageUrl(imgURL, imgUrl,news.getUrl());
						}
						if(StringUtils.isNotBlank(imgUrl)) {
							imgList.add(imgUrl);
						}
					}
					if(!imgList.isEmpty())
						ne.setPage_image_url(imgList);
//					StringBuffer sdfContent = new StringBuffer();
					Elements pdf = news.getContentElement().getElementsByTag("a");
					List<String> accesList = new ArrayList<String>();
					if(null != pdf && pdf.size() > 0) {
						for (int i = 0; i < pdf.size(); i++) {
							String pdfURL = ((Element) pdf.get(i)).attr("src");
							pdfURL = completeImageUrl(pdfURL, pdfURL, news.getUrl());
							if (pdfURL.endsWith(".pdf") || pdfURL.endsWith(".docx") || pdfURL.endsWith(".doc") ||pdfURL.endsWith(".xls") || pdfURL.endsWith(".xlsx")) {
								/*String regex = "[\\u4E00-\\u9FA5]";
								String[] chinese = JavaUtil.match(pdfURL, regex);
								if ((chinese != null) && (chinese.length > 0)) {
									pdfURL = URLEncoder.encode(pdfURL);
								}
								sdfContent.append(PDFPaserUtil
										.pasePDF2TextByHttp(pdfURL));*/
								
									accesList.add(pdfURL);
							} 
							/*if ((pdfURL.endsWith("doc"))
									|| (imgUrl.endsWith("docx"))) {
								String regex = "[\\u4E00-\\u9FA5]";
								String[] chinese = JavaUtil.match(pdfURL, regex);
								if ((chinese != null) && (chinese.length > 0)) {
									pdfURL = URLEncoder.encode(pdfURL);
								}
								sdfContent.append(WordPaserUtil
										.paseDOCXTextByHttp(pdfURL));
							}*/
						}
						if(!accesList.isEmpty())
							ne.setNews_acces(accesList);
						/*if (!StringUtils.isEmpty(sdfContent.toString())) {
							ne.setNews_content(sdfContent.toString());
						}*/
					}
					String fileName = "";
					if (Config.getIsSaveSnapShot() == 1)
						fileName = savePageSnapShot(page);
					if(StringUtils.isNotBlank(fileName))
						ne.setPage_snapshot(fileName);
					this.rtask.setContentPages(this.rtask.getContentPages() + 1);
					String newsSource = getNewsSource(
							this.task.getBeginSource(),
							this.task.getEndSource(), page);
					if(StringUtils.isNotBlank(newsSource))
						ne.setPage_source(newsSource.replace(" ", "").replaceAll("来源", "").replace("来源:", "").replace("来源：", ""));
					String newsAuthor = getNewsAuthor(this.task.getBeginAuthor(),this.task.getEndAuthor(), page);
					String columnName = getNewsColumnName(
							this.task.getBeginColumnName(),
							this.task.getEndColumnName(), page);
					if(StringUtils.isNotBlank(columnName))
					ne.setNews_column(columnName);
					String documentNo = getNewsDocumentNo(
							this.task.getBeginDocumentName(),
							this.task.getEndDocumentName(), page);
					if(StringUtils.isNotBlank(documentNo))
						ne.setNews_notice_code(documentNo.replaceAll("发文字号：", ""));
					if ((newsAuthor.length() <= 1)
							|| (newsAuthor.length() >= 10)
							|| (ne.getPage_url().contains("http://bbs.oeeee.com/"))) {
						newsAuthor = "";
					}
					/*if (StringUtils.isEmpty(newsAuthor) || newsAuthor.length() <= 1) {
						String subTitle = "";
						if(StringUtils.isNotBlank(ne.getNews_title())) {
							
							if(ne.getNews_title().contains("：")) {
								subTitle = ne.getNews_title().substring(0,ne.getNews_title().indexOf("：")); 
							}else if(ne.getNews_title().contains(":")) {
								subTitle = ne.getNews_title().substring(0,ne.getNews_title().indexOf(":")); 
							}
							if(subTitle.length() >= 2 && subTitle.length() <= 4) {
								String regex = "(?s)(?i)([\\u4E00-\\u9FA5]{0}\\s*[\\u4E00-\\u9FA5]{2,4}：|\\s*[\\u4E00-\\u9FA5]{2,4}: )";
								String[] results = JavaUtil.match(ne.getNews_title(), regex);
								if ((results != null) && (results.length > 0)) {
									if (results[0].contains("："))
										newsAuthor = results[0].substring(0,
												results[0].indexOf("："));
									else if (results[0].contains(":")) {
										newsAuthor = results[0].substring(0,
												results[0].indexOf(":"));
									}
								}
							}
						}
					}*/
					String single = "";
					String multi = "";
					if(!StringUtils.isEmpty(newsAuthor)) {
						single = newsAuthor.substring(0,1);
						multi = newsAuthor.substring(0,2);
						boolean isSingle = this.baijiaS.isContentKeyWords(single);
						boolean isMulti = this.baijiaM.isContentKeyWords(multi);
						boolean boo = this.keyFilter.isContentKeyWords(newsAuthor);
						if(newsAuthor.length() > 4) {
							newsAuthor = "";
						} else {
							if(!isSingle && !isMulti) {
								newsAuthor = "";
							} else {
								if(boo) {
									newsAuthor = "";
								}
							}
						}
					}
					if(StringUtils.isNotBlank(newsAuthor))
						ne.setNews_author(newsAuthor);
					if(StringUtils.isEmpty(ne.getNews_title())) {
						ne = null;
						return ne;
					}
					if(StringUtils.isNotBlank(ne.getNews_title())) {
						boolean titleFilter = this.titleKeyFilter.isContentKeyWords(ne.getNews_title());
						if ((ne.getNews_title().length() <= 5 || (ne.getNews_content().startsWith("更多>>")) || (ne.getNews_title().endsWith("部长")) || (ne.getNews_title().endsWith("助理"))
								|| (ne.getNews_content().startsWith("安溪新闻网版权与免责声明:")) || titleFilter)) {
							ne = null;
							return ne;
						}
					} 
					if(StringUtils.isBlank(ne.getNews_content())) {
						if(imgList.isEmpty() && accesList.isEmpty()) {
							ne = null;
						}
					} else {
						if(ne.getNews_content().length() < 30) {
							ne = null;
						}
					}
					return ne;
				}
				this.rtask.setUnknownPages(this.rtask.getUnknownPages() + 1);
				return null;
			}

			this.rtask.setListPages(this.rtask.getListPages() + 1);
			return null;
		} catch (Exception e) {
			this.logger.error("解析页面出错误！" + page.getLink().getUrl(), e);
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 提取新闻标题
	 * 
	 * @param beginTitle
	 * @param endTitle
	 * @param webContent
	 * @return
	 */
	protected String getNewsTitle(String beginTitle, String endTitle, WebPage page) {
		String title = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String textBeginCode = beginTitle;
			String textEndCode = endTitle;
			if (!textBeginCode.equals("") && !textEndCode.equals("")) {
				String text = webContent.toLowerCase();
				int iPos0 = text.indexOf(textBeginCode.toLowerCase());
				if (iPos0 != -1) {
					int len0 = textBeginCode.length();
					int iPos1 = text.indexOf(textEndCode.toLowerCase(), iPos0
							+ len0);
					if (iPos1 != -1) {
						title = text.substring(iPos0 + len0, iPos1).replaceAll(
								"&nbsp;", " ").replaceAll("(?s)(?i)<.*?>", "");
					}
				}

			}

			if (title.equals("")) {
				String linkText = link.getTitle();
				if (linkText != null && linkText.trim().length() != 0) {
					title = linkText;
				} else {

					String[] temp = JavaUtil.match(webContent,
							"(?s)(?i)<title>(.*?)</title>");
					if (temp != null && temp.length > 0)
						title = temp[1];

				}

			}
		} catch (Throwable e) {
			logger.error("抓取新闻: " + link.getUrl() + " 标题出现异常", e);
		}
		if(StringUtils.isNotBlank(title))
			title = title.replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "")
			.replaceAll("&gt;", "").replaceAll("&lt;", "").replaceAll("&ldquo;", "").replaceAll("&rdquo;", "").replaceAll("\\s+", "");
		return title;
	}

	private void resolvePage(ArrayList<WebPage> pages) {
		List<NewsGeneric> newses = new ArrayList<NewsGeneric>();
		if (pages == null)
			return;
		for (int i = 0; i < pages.size(); i++) {
			try {
				WebPage page = (WebPage) pages.get(i);
				int pageType = 0;
				TaskLink link = page.getLink();
				if (link.getUrl().equals(this.task.getTurl())) {
					continue;
				}
				NewsGeneric ne = null;
				if (link.getLinkType() == 0)
					ne = resloveNewsPage(page);
				else if (link.getLinkType() == 2) {
					ne = resloveKnownPage(page);
				}
				if (ne != null) {
					this.logger.info("\r\n  保存成功,链接: " + ne.getPage_url()
							+ "\r\n  标题: " + ne.getNews_title() + "\r\n  发布时间："
							+ ne.getPage_publish_time() + "\r\n  层次：" + link.getLevel()
							+ "\r\n  页面标记: " + pageType);
					boolean insertIs = MongoBaseDao.getInterface().insert(ne);
					logger.info(insertIs + "入库------------");
					newses.add(ne);
				}
//				if(null == ne) {
//					logger.info("新闻设为空" + link.getUrl());
//				}
			} catch (Exception e) {
				this.logger.error("解析页面出错误！"
						+ ((WebPage) pages.get(i)).getLink().getUrl(), e);
			}
		}
//		NewsDao.getInstance().save(newses);
//		for (int i = 0; i < newses.size(); i++) {
//			boolean insertIs = MongoBaseDao.getInterface().insert(newses.get(i));
//			System.out.println(insertIs);
//		}
//		NewsTrsDo ntd = new NewsTrsDo(newses);
//		ntd.start();
	}

	public boolean isContentPage(String url, String pageType,
			String extractContent) {
		boolean flag = false;
		if (PageCrawler.isContentPage(url, pageType)) {
			flag = true;
		} else if (extractContent.length() > 200) {
			flag = true;
		} else if ((extractContent != null) && (!extractContent.equals(""))) {
			String[] keyWords = { "电", "讯", "消息", "报道", "报导", "来源"};
			int indexInitial = extractContent.length() >= 20 ? 20
					: extractContent.length() - 1;
			String initText = extractContent.substring(0, indexInitial);
			for (int i = 0; i < keyWords.length; i++) {
				if (initText.indexOf(keyWords[i]) != -1) {
					flag = true;
					break;
				}
			}
		}
		String regex = "(?s)(?i)(.*?index[a-zA-Z]*\\.[a-zA-Z]{3,5}|.*?index[0-9]*\\.[a-zA-Z]{3,5}"
				+ "|.*?list[a-zA-Z]*\\.[a-zA-Z]{3,5}|.*?list[0-9]*\\.[a-zA-Z]{3,5}"
				+ "|.*?index_[a-zA-Z]*\\.[a-zA-Z]{3,5}|.*?index_[0-9]*\\.[a-zA-Z]{3,5}"
				+ "|.*?list_[0-9]*\\.[a-zA-Z]{3,5}|.*?list_[0-9]*\\.[a-zA-Z]{3,5})";
		String[] temp = JavaUtil.match(url, regex);
		if ((temp != null && temp.length >= 0) || url.endsWith("/")) {
			flag = false;
		}
		return flag;
	}

	private String getNewsSource(String beginSource, String endSource,
			WebPage page) {
		String newsSource = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String textBeginCode = beginSource;
			String textEndCode = endSource;
			if ((!textBeginCode.equals("")) && (!textEndCode.equals(""))) {
				String text = webContent.toLowerCase();
				int iPos0 = text.indexOf(textBeginCode.toLowerCase());
				if (iPos0 != -1) {
					int len0 = textBeginCode.length();
					int iPos1 = text.indexOf(textEndCode.toLowerCase(), iPos0
							+ len0);
					if (iPos1 != -1) {
						newsSource = text.substring(iPos0 + len0, iPos1)
								.replaceAll("&nbsp;", " ")
								.replaceAll("(?s)(?i)<.*?>", "");
					}
				}

			}

			if (newsSource.equals("")) {
				String regex = "(?s)(?i)(来源\\s+|來源：|来源：|來源:|来源:|来自:)([\\u4E00-\\u9FA5]+[0-9]+|[\\u4E00-\\u9FA5]+-[\\u4E00-\\u9FA5]+|[\\u4E00-\\u9FA5]+)";
				webContent = webContent
						.replaceAll("(?i)(?s)<marquee.*?</marquee>", "")
						.replaceAll("(?s)(?i)<.*?>", " ")
						.replaceAll("\\s+", " ");
				String[] results = JavaUtil.match(webContent, regex);
				if ((results != null) && (results.length > 2)) {
					newsSource = results[2];
				}
			}
			if (newsSource.equals("")) {
				String regex = "\\d{2}:\\d{2}";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(webContent);
				while (m.find()) {
					String temp = webContent.substring(m.end(), m.end() + 10);
					String regexSource = "(?s)(?i)[\\s|\\pP]{1,2}([[\\u4E00-\\u9FA5][\\w]]{1,6}[网报])[\\s|\\pP]";
					String[] results = JavaUtil.match(temp, regexSource);
					if ((results != null) && (results.length > 1)) {
						newsSource = results[1];
						break;
					}
				}
			}
		} catch (Throwable e) {
			this.logger.error("抓取新闻来源出现异常: " + link.getUrl(), e);
		}
		if(newsSource.equals("作者") || newsSource.equals(" 作者") || newsSource.contains("人气") || newsSource.contains("日期")
				|| newsSource.contains("点击") || newsSource.contains("时间") || newsSource.length() <= 1 || newsSource.contains("其它")
				|| newsSource.contains("浏览") || newsSource.contains("分享") || newsSource.contains("其他"))
			newsSource = "";
		if(StringUtils.isNotBlank(newsSource)) {
			newsSource = newsSource.replaceAll("&nbsp;", " ").replaceAll(
					"(?s)(?i)<.*?>", "").replaceAll("\t", "").replaceAll("\r\n", "").replaceAll("\n", "").replaceAll(" ", "");
		}
		return newsSource;
	}

	private String getNewsAuthor(String beginAuthor, String endAuthor,
			WebPage page) {
		String newsAuthor = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String textBeginCode = beginAuthor;
			String textEndCode = endAuthor;
			if ((!textBeginCode.equals("")) && (!textEndCode.equals(""))) {
				String text = webContent.toLowerCase();
				int iPos0 = text.indexOf(textBeginCode.toLowerCase());
				if (iPos0 != -1) {
					int len0 = textBeginCode.length();
					int iPos1 = text.indexOf(textEndCode.toLowerCase(), iPos0
							+ len0);
					if (iPos1 != -1) {
						newsAuthor = text.substring(iPos0 + len0, iPos1)
								.replaceAll("&nbsp;", " ")
								.replaceAll("(?s)(?i)<.*?>", "");
					}
				}
			}
			webContent = webContent
					.replaceAll("(?i)(?s)<marquee.*?</marquee>", "")
					.replaceAll("(?s)(?i)<.*?>", "")
					.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
			if (newsAuthor.equals("")) {
				String regex = "(?s)(?i)(編輯\\s+|編輯：|編輯:|编辑：|编辑:|编辑\\s+|\\（记者：|\\（记者:|\\(记者：|\\(记者:|发布者：|发布者\\s+|发布者:|\\d+\\s+\\|\\s+|作者：|作者:|作者\\s+|作者︰|作者 :)([\\u4E00-\\u9FA5]{1}\\s+[\\u4E00-\\u9FA5]{1}|\\s*[\\u4E00-\\u9FA5]+\\s*|\\s*\\[[\\u4E00-\\u9FA5]+\\])";
				String[] results = JavaUtil.match(webContent, regex);
				if ((results != null) && (results.length > 2))
					newsAuthor = results[2];
				if(newsAuthor.equals("")) {
					regex = "(?s)(?i)([\\u4E00-\\u9FA5]{1}\\s+[\\u4E00-\\u9FA5]{1}|[\\u4E00-\\u9FA5]{2,4})(\\d{4}年\\s+)";
					results = JavaUtil.match(webContent, regex);
					if ((results != null) && (results.length > 1))
						newsAuthor = results[1];
				}
				if(newsAuthor.equals("")) {
					regex = "(?s)(?i)(編輯\\s+|編輯：|編輯:|编辑：|编辑:|编辑\\s+|\\（记者：|\\（记者:|\\(记者：|\\(记者:|发布者：|发布者\\s+|发布者:|\\d+\\s+\\|\\s+|作者：|作者:|作者\\s+|作者︰|作者 :)([\\u4E00-\\u9FA5]+\\s+[\\u4E00-\\u9FA5]+{2,4})";
					results = JavaUtil.match(webContent, regex);
					if ((results != null) && (results.length > 2))
						newsAuthor = results[2];
				}
			}
		} catch (Throwable e) {
			this.logger.error("抓取新闻作者出现异常: " + link.getUrl(), e);
		}
		String single = "";
		String multi = "";
		if(!StringUtils.isEmpty(newsAuthor)) {
			single = newsAuthor.substring(0,1);
			if(newsAuthor.length() >= 2) {
				multi = newsAuthor.substring(0,2);
			}
			boolean isSingle = this.baijiaS.isContentKeyWords(single);
			boolean isMulti = this.baijiaM.isContentKeyWords(multi);
			boolean boo = this.keyFilter.isContentKeyWords(newsAuthor);
			if(newsAuthor.length() > 4) {
				newsAuthor = "";
			} else {
				if(!isSingle && !isMulti) {
					newsAuthor = "";
				} else {
					if(boo) {
						newsAuthor = "";
					}
				}
			}
			if (newsAuthor.contains("报道")) {
				newsAuthor = newsAuthor.replaceAll("报道", "");
			}
			if(newsAuthor.length() <= 1) {
				newsAuthor = "";
			}
		}
		if(StringUtils.isNotBlank(newsAuthor)) 
			newsAuthor = newsAuthor.replaceAll("&nbsp;", " ").replaceAll(
					"(?s)(?i)<.*?>", "").replaceAll("\t", "").replaceAll("\r\n", "").replaceAll("\n", "").replaceAll(" ", "");
		return newsAuthor;
	}

	protected String getNewsColumnName(String beginColumnName,
			String endColumnName, WebPage page) {
		String columnName = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String textBeginCode = beginColumnName;
			String textEndCode = endColumnName;
			if ((!textBeginCode.equals("")) && (!textEndCode.equals(""))) {
				String text = webContent.toLowerCase();
				int iPos0 = text.indexOf(textBeginCode.toLowerCase());
				if (iPos0 != -1) {
					int len0 = textBeginCode.length();
					int iPos1 = text.indexOf(textEndCode.toLowerCase(), iPos0
							+ len0);
					if (iPos1 != -1) {
						columnName = text.substring(iPos0 + len0, iPos1)
								.replaceAll("&nbsp;", " ")
								.replaceAll("(?s)(?i)<.*?>", "");
					}
				}

			}

			if (columnName.equals("")) {
				String linkText = link.getDocumentName();
				if ((linkText != null) && (linkText.trim().length() != 0)) {
					columnName = linkText;
				} else {
					String regex = "(?s)(?i)(首页|位置：|位置:)(\\s*»\\s*|\\s*>>\\s*|\\s*>\\s*|\\s*->\\s*|\\s*-\\s*|\\s*\\|\\s*|\\s*/\\s*|\\s*—\\s*|\\s*→\\s*"
							+ "|\\s*[\\u4E00-\\u9FA5]+\\s*>>\\s*|\\s*[\\u4E00-\\u9FA5]+\\s*>\\s*|\\s*[\\u4E00-\\u9FA5]+\\s*-\\s*|\\s*[\\u4E00-\\u9FA5]+\\s*\\"
							+ "|\\s*\\s*)([\\u4E00-\\u9FA5]+[a-zA-Z]+|[\\u4E00-\\u9FA5]+)";
					webContent = webContent
							.replaceAll("(?is)<marquee.*?</marquee>", "")
							.replaceAll("(?s)(?i)<.*?>", "")
							.replaceAll("\\s+", " ").replaceAll("&nbsp;", "")
							.replaceAll("&gt;", ">").replaceAll("&#62;", ">")
							.replaceAll("&mdash;", "—")
							.replaceAll("&raquo;", ">").replaceAll("&gt", ">")
							.replaceAll("-&gt;&gt;", ">")
							.replaceAll("设为首页", "");
					String[] temp = JavaUtil.match(webContent, regex);
					if ((temp != null) && (temp.length >= 3))
						columnName = temp[3].replace("首页 >", "");
				}
				if ((!StringUtils.isEmpty(columnName))
						&& ((columnName.contains("首页")) || (columnName
								.contains("更多")))) {
					String regex = "(?s)(?i)(位置:\\s*|位置：\\s*)([\\u4E00-\\u9FA5]+)(\\s*>\\s*|\\s*>>)";
					webContent = webContent
							.replaceAll("(?is)<marquee.*?</marquee>", "")
							.replaceAll("(?s)(?i)<.*?>", "")
							.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
					String[] temp = JavaUtil.match(webContent, regex);
					if ((temp != null) && (temp.length >= 2))
						columnName = temp[2];
				}
				if (StringUtils.isEmpty(columnName) || columnName.contains("我们是")) {
					String regex = "(?s)(?i)(当前位置：\\s*|当前位置:\\s*|您的位置:\\s*|您的位置：\\s*|位置：首页/)([\\u4E00-\\u9FA5]+[a-zA-Z]+|[\\u4E00-\\u9FA5]+)";
					webContent = webContent
							.replaceAll("(?is)<marquee.*?</marquee>", "")
							.replaceAll("(?s)(?i)<.*?>", "")
							.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
					String[] temp = JavaUtil.match(webContent, regex);
					if ((temp != null) && (temp.length >= 2))
						columnName = temp[2];
				}
				if(columnName.contains("网站地图") && webContent.contains(">")) {
					String regex = "(?s)(?i)([\\u4E00-\\u9FA5]+\\s*>\\s+)([\\u4E00-\\u9FA5]+)";
					webContent = webContent
							.replaceAll("(?is)<marquee.*?</marquee>", "")
							.replaceAll("(?s)(?i)<.*?>", "")
							.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
					String[] temp = JavaUtil.match(webContent, regex);
					if ((temp != null) && (temp.length >= 2))
						columnName = temp[2];
				}
				if(!StringUtils.isEmpty(columnName)) {
					boolean boo = this.keyFilter.isContentKeyWords(columnName);
					if(boo) {
						columnName = "";
					}
				}
			}
		} catch (Throwable e) {
			this.logger.error("抓取新闻: " + link.getUrl() + " 栏目名称出现异常", e);
		}
		return columnName;
	}

	protected String getNewsDocumentNo(String beginDocumentName,
			String endDocumentName, WebPage page) {
		String documentName = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String textBeginCode = beginDocumentName;
			String textEndCode = endDocumentName;
			if ((!textBeginCode.equals("")) && (!textEndCode.equals(""))) {
				String text = webContent.toLowerCase();
				int iPos0 = text.indexOf(textBeginCode.toLowerCase());
				if (iPos0 != -1) {
					int len0 = textBeginCode.length();
					int iPos1 = text.indexOf(textEndCode.toLowerCase(), iPos0
							+ len0);
					if (iPos1 != -1) {
						documentName = text.substring(iPos0 + len0, iPos1)
								.replaceAll("&nbsp;", " ")
								.replaceAll("(?s)(?i)<.*?>", "");
					}
				}

			}

			if (documentName.equals("")) {
				String linkText = link.getDocumentName();
				if ((linkText != null) && (linkText.trim().length() != 0)) {
					documentName = linkText;
				} else {
					String regex = "(?s)(?i)发文字号：\\s*[\\u4E00-\\u9FA5]+\\[\\d+\\].*?号|\\d{4}年\\d{1,2}月\\d{1,2}日[\\u4E00-\\u9FA5]+第\\d+号|\\d{4}年第\\d+号|\\d{4}年[\\u4E00-\\u9FA5]+第\\d+号|\\s+[\\u4E00-\\u9FA5]+〔[0-9]+〕[0-9]+号|\\s+[\\u4E00-\\u9FA5]+第[0-9]+号|\\s+[\\u4E00-\\u9FA5]+\\[\\d+\\]号";
					webContent = webContent
							.replaceAll("(?is)<marquee.*?</marquee>", "")
							.replaceAll("(?s)(?i)<.*?>", "")
							.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
					String[] temp = JavaUtil.match(webContent, regex);
					if ((temp != null) && (temp.length > 0)) {
						documentName = temp[0];
					}
				}
			}
			if (documentName != null)
				documentName = documentName.replaceAll("&nbsp;", " ")
						.replaceAll("(?s)(?i)<.*?>", "")
						.replaceAll("发文字号：", "");
		} catch (Throwable e) {
			this.logger.error("抓取新闻: " + link.getUrl() + "新闻文号出现异常", e);
		}
		if ((documentName.length() > 20) || (documentName.startsWith("字"))) {
			documentName = "";
		}
		return documentName;
	}

	private List<String> getImgUrl(String webContent) {
		List<String> pics = new ArrayList<String>();
		Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(
				webContent);
		while (m.find()) {
			pics.add(m.group(1));
		}

		return pics;
	}

	public static String completeImageUrl(String url, String nowUrl,
			String pageUrl) {
		String return_url = "";
		if (StringUtils.isEmpty(url)) {
			return return_url;
		}

		if ((url.startsWith("http://")) || (url.startsWith("https://")) || url.startsWith("file://")) {
			return url;
		}
		String imgPre = pageUrl.substring(0, pageUrl.indexOf("://") + 3);
		pageUrl = pageUrl.substring(pageUrl.indexOf("://") + 3);
		return_url = getCompleteImgUrl(url, pageUrl, imgPre);

		return return_url;
	}

	private static synchronized ArrayList<WebPage> getSavePages(int count) {
		ArrayList<WebPage> pages = null;
		if (SysObject.getPageSize() > 0) {
			if (count > 0) {
				pages = new ArrayList<WebPage>();
				for (int i = 0; i < count; i++) {
					WebPage page = SysObject.getPage();
					if (page != null)
						pages.add(page);
				}
			} else {
				pages = new ArrayList<WebPage>();
				while (SysObject.getPageSize() > 0) {
					WebPage page = SysObject.getPage();
					if (page != null) {
						pages.add(page);
					}
				}
			}
		}
		return pages;
	}

	private String savePageSnapShot(WebPage page) {
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		String savePath = SiteCrawler.fileSavePath;
		if ((savePath.endsWith("\\")) || (savePath.endsWith("/")))
			savePath = savePath.substring(0, savePath.length()) + "/";
		else
			savePath = savePath + "/";
		savePath = savePath + this.task.getTaskid() + "/";
		MyDate now = new MyDate();
		String yyyymmdd = now.get_yyyymmdd();
		String hhmmsss = now.get_hh24miss();
		String foldPath = yyyymmdd.substring(0, 4) + "/"
				+ yyyymmdd.substring(4, 6) + "/" + yyyymmdd.substring(6, 8)
				+ "/" + hhmmsss.substring(0, 2) + "/" + hhmmsss.substring(2, 3);

		File savaFolder = new File(savePath + foldPath);
		if (!savaFolder.exists())
			savaFolder.mkdirs();
		String fileName = "WEB.4101000101013001." + yyyymmdd + "." + hhmmsss
				+ "." + (10000L + Math.round(Math.random() * 10000.0D))
				+ ".htm";
		File htmlFile = new File(savaFolder.getAbsolutePath() + "/" + fileName);
		try {
			if (!htmlFile.exists())
				htmlFile.createNewFile();
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(htmlFile), "utf-8");
			out.write(webContent);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			this.logger.error("保存文件出现错误:" + link.getUrl() + e.getMessage()
					+ " 请检查快照文件保存目录的设置！表【n_para】->filepath字段");
		} catch (IOException e) {
			this.logger.error("保存文件出现错误:" + link.getUrl() + e.getMessage()
					+ " 请检查快照文件保存目录的设置！表【n_para】->filepath字段");
		}

		return fileName;
	}

	private String clearHtml(String webSourcePage) {
		String target = webSourcePage
				.replaceAll("(?i)(?s)<style.*?</style>", "")
				.replaceAll("(?i)(?s)<(no)?script.*?</(no)?script>", "")
				.replaceAll("(?i)(?s)<select.*?</select>", "")
				.replaceAll("(?i)(?s)<!--.*?-->", "");
		target = target.replaceAll("&nbsp;?", " ").replaceAll("[ ]{2,}", " ")
				.replaceAll("\\s+", " ");
		return target;
	}

	public static String getCompleteImgUrl(String str, String old, String imgPre) {
		StringBuffer real = new StringBuffer();
		String realUrl = "";
		int lpos = StringUtils.lastIndexOf(str, "../");
		int mpos = StringUtils.lastIndexOf(str, "./");
		if ((lpos > -1) || (mpos > -1)) {
			String[] sp = StringUtils.split(old, "/");
			String[] sp_new = (String[]) Arrays.copyOf(sp, sp.length - 1);
			int lcount = lpos > -1 ? (StringUtils.substring(str, 0, lpos)
					.length() + 3) / 3 : 0;
			int mcount = mpos > -1 ? (StringUtils.substring(str, 0, mpos)
					.length() + 3) / 3 : 0;
			if ((lcount > 0) || ((lcount > 0) && (mpos <= -1))) {
				int i = 0;
				for (int count_new = sp_new.length - lcount; i < count_new; i++) {
					real.append(sp_new[i]).append("/");
				}
				real.append(StringUtils.substring(str, lpos + 3));
			} else if ((mcount > 0) && (lpos <= -1)) {
				for (int i = 0; i < sp_new.length; i++) {
					real.append(sp_new[i]).append("/");
				}
				real.append(StringUtils.substring(str, mpos + 2));
			}
			realUrl = imgPre + real.toString();
			return realUrl;
		}
		if (str.startsWith("/")) {
			old = old.substring(0, old.indexOf("/") + 1);
			realUrl = imgPre + old + str.substring(str.indexOf("/") + 1);
			return realUrl;
		}
		old = old.substring(0, old.indexOf("/") + 1);
		realUrl = imgPre + old + str;

		return realUrl;
	}

	public static void main(String[] args) {
		String content = "<?xml:namespaceprefix=iueuy/>";
		String replace = ""; 
		if(content.contains("<?xml:namespaceprefix=")) {
			replace = content.substring(content.indexOf("<?xml:namespaceprefix="));
			replace = replace.substring(0, replace.indexOf("/>") + 2);
		}
		System.out.println(content.replace("", ""));
	}
}