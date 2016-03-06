package com.meiah.webCrawlers.SitePlugin.Gxhs.bidding;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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

public class PageResolver_Fjbid extends PageResolver_General {
	
	public PageResolver_Fjbid(Task task) {
		super(task);
	}
	protected NewsGeneric resloveNewsPage(WebPage page) {
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		Document doc = null;
		try {
			doc = JavaUtil.getDocument(webContent);
		} catch (Exception e) {
			logger.error("解析新闻页面出现异常！" + link.getUrl());
			return null;
		}
		webContent = clearHtml(webContent);
		NewsGeneric ne = new NewsGeneric();
		ne.setTask_id(task.getTaskid());
		ne.setPage_url(link.getUrl());
		ne.set_id(MD5Utils.getMD5(ne.getPage_url().getBytes()));
		String fileName = "";
		if (Config.getIsSaveSnapShot() == 1) {
			fileName = savePageSnapShot(page);
		}
		if(StringUtils.isNotBlank(fileName))
			ne.setPage_snapshot(fileName);
//		ne.setIslist(SysConstants.PAGETYPE_CONTENT);
//		ne.setSpot_code(Config.getSpotID());
		String webdomain = ne.getPage_url();
		String domain = webdomain.substring(webdomain.indexOf("://") + 3);
		domain = domain.substring(0, domain.indexOf("/"));
		ne.setWebsite_domain(SiteCrawler.topDomain);
		String prefixUrl = link.getRefererUrl().substring(0,link.getRefererUrl().indexOf("://") + 3);
		ne.setWebsite_url(prefixUrl + domain + "/");
		ne.setPage_size(String.valueOf(webContent.getBytes().length));
		Map<String, Task> ts = TaskDao.getInstance().getAllTaskMap();
		String siteName = ((Task) ts.get(ne.getTask_id())).getTname().replaceAll("\r\n", "");
		ne.setWebsite_name(siteName);
		ne.setTask_name(siteName);
		String ipUrl = ((Task) ts.get(ne.getTask_id())).getUrl();
		String ipName = "";
		String ip = JavaUtil.matchWeak(ipUrl, "http://([^/]*)")[1];
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
		ne.setNews_class(task.getNewsType());
//		if (SiteCrawler.homePageUrls != null && SiteCrawler.homePageUrls.contains(ne.getUrl()))
//			ne.setIsHomePageNews(1);
		/** end default notChange * */
		String newsTitle = getNewsTitle(task.getSiteConfig(), page, doc);
		ne.setNews_title(newsTitle);
		if(StringUtils.isNotBlank(ne.getNews_title())) {
			String regex = "[\\u4E00-\\u9FA5]";
			String[] chinese = JavaUtil.match(ne.getNews_title(), regex);
			if (null == chinese || chinese.length < 0) {
				ne = null;
				return ne;
			}
		}
		long tStart = System.currentTimeMillis(), t2 = 0, t3 = 0, t4 = 0;
		String content = getNewsContent(task.getSiteConfig(), page, doc);
		if (logger.isDebugEnabled()) {
			t3 = System.currentTimeMillis() - tStart - t2;
			logger.debug("正文提取耗费时间：" + t3 + "ms");
		}
//		String replace = "";
//		if(content.contains("<?xml:namespace")) {
//			replace = content.substring(content.indexOf("<?xml:namespace"));
//			replace = replace.substring(0, replace.indexOf("/>") + 2);
//		}
		content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>","(?s)(?i)<.*?/>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;" }, new String[] { "", "" ,"" , "","",""});
		ne.setNews_content(content.replaceAll("　", "").replaceAll("\r\n", "").replaceAll("\n", "").trim());
		
		Date dateDublished = getNewsPublishTime(task, page, doc);
		if(null != dateDublished)
			ne.setPage_publish_time(dateDublished);
		ne.setPage_save_time(new Date());
		
		InetAddress a = null;
		try {
			ne.setServer_ip(a.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (logger.isDebugEnabled()) {
			t4 = System.currentTimeMillis() - tStart - t2 - t3;
			logger.debug("时间提取耗费时间：" + t4 + "ms");
		}
		link.setLinkType(SysConstants.PAGETYPE_CONTENT);
		String newsSource = getNewsSource(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(newsSource))
			ne.setPage_source(newsSource);
		String newsAuthor = getNewsAuthor(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(newsAuthor))
			ne.setNews_author(StringUtils.replaceEach(newsAuthor, new String[] { "等", "撰写","作者：","作者:"," ","?" }, new String[] { "", "" ,"" , "","",""}));
		List<String> newsImage = getNewsImages(task.getSiteConfig(), page, doc,ne.getPage_url());
		if(!newsImage.isEmpty())
			ne.setPage_image_url(newsImage);
		//获取附件地址
		List<String> accessList = getNewsAccess(task.getSiteConfig(), page, doc, ne.getPage_url());
		if(!accessList.isEmpty()) 
			ne.setPage_acces(accessList);
		String columnName = getNewsColumnName(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(columnName))
			ne.setNews_column(columnName);
		String documentNo = getNewsDocumentNo(task.getSiteConfig(), page, doc);
		if(StringUtils.isNotBlank(documentNo))
			ne.setNews_notice_code(documentNo);
		rtask.setContentPages(rtask.getContentPages() + 1);
		
		if(StringUtils.isEmpty(ne.getNews_title())) {
			ne = null;
			return ne;
		}
		if(StringUtils.isNotBlank(ne.getNews_title())) {
			boolean titleFilter = titleKeyFilter.isContentKeyWords(ne.getNews_title());
			if(titleFilter || ne.getNews_title().length() < 5) {
				ne = null;
				return ne;
			}
		}
		if(StringUtils.isBlank(ne.getNews_content())) {
			if(newsImage.isEmpty() && accessList.isEmpty()) {
				ne = null;
			}
		} else {
			if(ne.getNews_content().length() < 100) {
				ne = null;
			}
		}
		return ne;
	}
	/**
	 * 获取新闻文号
	 * @param count
	 * @return
	 */
	protected String getNewsDocumentNo(SiteConfig conf, WebPage page, Document doc) {
		String documentNo = "";
		String webContent = page.getWebContent();
		TaskLink link = page.getLink();
		try {
			String documentNumber = page.getLink().getDocumentName();
			if (documentNumber != null && !documentNumber.trim().equals(""))
				return documentNumber;
			documentNo = getLocText(conf.getDocumentLocation(), page, doc);
			
			if (documentNo == null || documentNo.equals("")) {
				String regex = "(?s)(?i)[\\u4E00-\\u9FA5]+〔\\d+〕\\d+号|[\\u4E00-\\u9FA5]+\\[\\d{4}\\]\\d+|发文字号：\\s*[\\u4E00-\\u9FA5]+\\[\\d+\\].*?号|\\d{4}年\\d{1,2}月\\d{1,2}日[\\u4E00-\\u9FA5]+第\\d+号|\\d{4}年第\\d+号|\\d{4}年[\\u4E00-\\u9FA5]+第\\d+号|\\s+[\\u4E00-\\u9FA5]+〔[0-9]+〕[0-9]+号|\\s+[\\u4E00-\\u9FA5]+第[0-9]+号|[\\u4E00-\\u9FA5]+\\[\\d+\\]号";
				webContent = webContent
						.replaceAll("(?is)<marquee.*?</marquee>", "")
						.replaceAll("(?s)(?i)<.*?>", "")
						.replaceAll("\\s+", " ").replaceAll("&nbsp;", "");
				String[] temp = JavaUtil.match(webContent, regex);
				if ((temp != null) && (temp.length > 0)) {
					documentNo = temp[0];
				}
			}
			if (documentNo != null)
				documentNo = documentNo.replaceAll("&nbsp;", " ").replaceAll(
						"(?s)(?i)<.*?>", "").replaceAll("发文字号：", "").replaceAll("	", "");
		} catch (Throwable e) {
			logger.error("抓取文号出现异常: " + link.getUrl(), e);
		}
		return documentNo;
	}
	public static void main(String[] args) {
		String content = "<?xml:namespaceprefix=st1ns=\"urn:schemas-microsoft-com:office:smarttags\"/>2010年12月15日，福建省招标投标协会在福州成立；2006年8月4日，《福建省招标投标条例》通过；1999年8月30日，第九届全国人民代表大会常务委员会通过《中华人民共和国招投标法》一个全新的行业协会迈开了奔跑的步伐，开始与这个已经崛起多年的行业一道前行，有哪些困难，有哪些希冀？《福建招标投标》问：福建省招投标行业协会建立对福建省招投标事业的影响？陈会长答：2004年国内投融资体制改革进一步规范了政府投资，众多配套体制改革也被提上议程，例如政府投资决策机制、责任机制等，在执行的过程中如何保证合理地配置资源，提高资金的使用效率，建立公正的环境和更加完善的市场体制，都需要思考和探索。招投标制度更是与政府投资行为密切相关，走向规范、公开、透明是行业需求，也是政策的必然走向。实际上，《福建省招标投标条例》2006年便得以通过，《中华人民共和国招标投标法》在2000年前也已经颁布实施，因为种种原因，福建省招标投标协会没有立即成立。目前，主要行业主管部门都有一些对应协会，负责政府采购、市场交易等，但招标投标全行业的自律和规范就显得越来越重要。目前招投标市场还存在许多问题，除部门之间需要协调外，很多需要规范、需要自律的制度，都需要一个平台来协调管理。福建省招投标协会成立以后，将逐步有计划地规范招投标的一些行为。《福建招标投标》问：我们了解到，规范招投标行为存在很大的困难，目前各个行业主管部门在各自领域的招投标管理上都有各自的职责，在国家法律层面，招投标行业就有三部法律。福建省招标投标协会成立后，计划如何展开工作？陈会长答：福建省招标投标协会主要的工作有三项：协调全省的招投标工作，规范全省的招投标行为和建立公平公正的招投标市场。协会首先是政府和企业之间沟通的平台，政府政策导向通过这个平台投递到企业去，企业对招投标的诉求通过协会的平台传递给政府。其次，福建省招投标协会通过规范招投标行为，推动行业自律。另外，协会承担的一个职能就是根据相关法律法规提出各项政策建议，上报相关部门。这三大方面是协会大有可为的领域。福建省招标投标协会受福建省发改委指导，《福建省招标投标条例》规定指导协调招标投标工作是省发改委的只能，之所以这样规定，出于几点考虑。首先省发改委不是运动员，没有部门利益，容易保证公正的立场，比较合适当裁判员；其次，福建省发改委在政府部门分工中，它具备宏观调控、主要项目管理、稽查的职能，对政府投资项目具备较多调控手段。福建省招标投标协会目前刚刚搭起架子，还有很多事情需要去努力。尽管目前还无法一下子系统展开工作，但我们需要朝这个方向去努力。协会每年踏实做一些事情，积少成多，一口气想解决所有问题不大现实，但建立一个协调解决的平台有总比没有好。《福建招标投标》问：会员单位对协会目前提出了哪些诉求？陈会长答：福建招标投标协会是年轻的协会，与招投标有关的单位机构众多，大家都在密切地关注着协会的举动，需要有个认识和了解的过程，能否取得大家的认同有待协会的努力。实际上，现在很多会员希望协会提供一些服务，越是有实力的会员单位越要求招投标市场规范，只有规范了实力才能体现。政策宣传和业务培训是第一项的工作，实际上也是一种服务。原来培训的工作已经在抓了，但是还要加强，宣传的工作今后的力度也在不断加大。此外，很多会员希望协会成立后能推动行业建立标准，走向规范化。目前，招标师、评标专家培训和管理都在进行，今后需要持续教育，有进有出，建立退出机制。福建省招投标协会拥有众多福建省招投标行业的标杆企业，协会完全可以依托这些标杆企业一起努力，推动行业不断走上规范健康发展之路。《福建招标投标》问：会长，在参与福建省招标投标协会筹备的过程中，您有哪些感想？陈会长答：筹备过程中难度很大，首先要突破各个行业主管部门的认识问题，“为什么还要成立一个专门的招标投标协会”是很多人不解的问题。我担任会长以来，跑一个部门宣传一个部门，使得协会工作逐步为一些行业主管部门所认同。就是在发改委本系统也经历了一个逐步认识的过程。目前，协会刚刚起步，面临经费、办公场所、人员的问题，尤其是精通业务又操行高尚的专业人员难以物色。此外，还有发展会员的难题，目前还有一些很有实力，在招投标行业中具备较高影响力的单位没有加入，协会还要进一步加强宣传推广工作。有为才有位，协会要不断有为，影响大了，对会员的吸引力和凝聚力才会相应增强。尽管目前协会经费并不宽裕，但协会依然执行了全国最低标准的会费，力求不增加会员单位负担，树立“大局、服务、自律”三大意识，希望看到协会这个大家庭不断壮大。《福建招标投标》问：会长，您对协会的发展寄予哪些期望？心里有没有一个发展时刻表？陈会长答：我认为首先要树立一个大行业的概念，福建招标投标协会不同于一般的行业协会，是服务全省的招投标各行业。协会自身要不断加强建设和学习，培训、网站管理、宣传工作要加强，要持之以恒；另一方面，协会要积极排解会员单位困难，接受投诉。在今后的工作开展上，要找准切入点，建立信用体系，规范行业行为，提升招投标队伍素质。长期来看，目前全省招投标网站已经统一了，评标专家库已经统一了，建立全省统一的招投标交易平台也已提上议程，协会的工作也将逐步走向纵深。陈毓寰：福建省发展和改革委原党组副书记、副主任，福建省工程咨询中心党组书记、主任，福建省政协港澳台侨和外事委员会主任，福建省招标投标协会筹备组组长。现任福建招标投标协会会长。";
		String replace = "";
		if(content.contains("<?xml:namespace")) {
			replace = content.substring(content.indexOf("<?xml:namespace"));
			replace = replace.substring(0, replace.indexOf("/>") + 2);
		}
		content = StringUtils.replaceEach(content, new String[] { "(?s)(?i)<.*?>", "&ldquo;","&lsquo;","&rsquo;","&rdquo;",replace }, new String[] { "", "" ,"" , "","",""});
	}
}
