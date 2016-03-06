package com.meiah.po;

import java.util.Date;

import org.jsoup.nodes.Element;

import com.meiah.util.WordTypeEnum;

public class News {
	private int id;
	private String taskid;			//任务id
	private String topDomain;		//顶级域名
	private String webDomain;		//二级域名
	private String page_url;		//网页url
	private String title;			//标题
	private int pageSize;			//网页长度
	private String userName;		//网页作者
	private Date datePublished;		//发布时间
	private Date savetime;			//保存时间
	private String page_type;		//网页类型
	private String filename;		//文件名称
	private String spot_code;		//系统编号
	private String source_type;		//系统类型
	private int islist;				//是否是列表
	private String conform;			//相似新闻 
	private String ip;				
	private String ipArea;			//ip所在地
	private String content;			//网页内容
	private KwScore ks;			
	private String webName;			//网站名称
	private String alarmType;
	private String referUrl;
	private int isHomePageNews;		//是否是首页新闻
	private String author;			//作者
	private String sourceSite;
	private String pageUrlMD5;		//pageUrl的MD5值
	private String imgUrl;			//正文图片链接地址
	private WordTypeEnum wordType;	//文章类型
	private String columnName;		//栏目名
	private String DocumentNo;		//文号
	private Element contentElement = null;    //用来保存带标签的正文，以便于获取正文中的图片链接地址
	private String newsType;		//新闻类型（智库/财经/政府网站/发改委）
	private String ipName;
	
	public String getIpName() {
		return ipName;
	}

	public void setIpName(String ipName) {
		this.ipName = ipName;
	}

	public String getWebName() {
		return webName;
	}

	public void setWebName(String webName) {
		this.webName = webName;
	}

	public Element getContentElement() {
		return contentElement;
	}

	public String getNewsType() {
		return newsType;
	}

	public void setNewsType(String newsType) {
		this.newsType = newsType;
	}

	public void setContentElement(Element contentElement) {
		this.contentElement = contentElement;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDocumentNo() {
		return DocumentNo;
	}

	public void setDocumentNo(String documentNo) {
		DocumentNo = documentNo;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getPageUrlMD5() {
		return pageUrlMD5;
	}

	public void setPageUrlMD5(String pageUrlMD5) {
		this.pageUrlMD5 = pageUrlMD5;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSourceSite() {
		return sourceSite;
	}

	public void setSourceSite(String sourceSite) {
		this.sourceSite = sourceSite;
	}

	public int getIsHomePageNews() {
		return isHomePageNews;
	}

	public void setIsHomePageNews(int isHomePageNews) {
		this.isHomePageNews = isHomePageNews;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;

		// TaskAlarm alarm = TaskAlarm.getTaskAlarm(this.taskid);
		// String alarm1 = "";
		// // String alarm2 = "";
		// try {
		// alarm1 = alarm.isContentAlarm(this.content);
		// // alarm2 = alarm.isUserAlarm(this.user.getUname());
		// } catch (RuntimeException e) {
		// }
		// // String are = alarm1
		// // + ((alarm2 != null && alarm2.trim().length() > 0) ? "," : "")
		// // + alarm2 + ",";
		// if (",".equals(alarm1))
		// alarm1 = null;
		// this.setAlarmType(alarm1);
	}

	public String getTopDomain() {
		return topDomain;
	}

	public void setTopDomain(String topDomain) {
		this.topDomain = topDomain;
	}

	public String getWebDomain() {
		return webDomain;
	}

	public void setWebDomain(String webDomain) {
		this.webDomain = webDomain;
	}

	public String getUrl() {
		return page_url;
	}

	public void setUrl(String url) {
		page_url = url;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getLength() {
		return pageSize;
	}

	public void setLength(int length) {
		pageSize = length;
	}

	public Date getDateline() {
		return datePublished;
	}

	public void setDateline(Date dateline) {
		datePublished = dateline;
	}

	public Date getSavetime() {
		return savetime;
	}

	public void setSavetime(Date savetime) {
		this.savetime = savetime;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getConform() {
		return conform;
	}

	public void setConform(String conform) {
		this.conform = conform;
	}

	public int getIslist() {
		return islist;
	}

	public void setIslist(int islist) {
		this.islist = islist;
	}

	public String getContent() {
//		 if (content == null) {
//	        if (contentElement != null) {
//	            content = contentElement.text();
//	            content = content.replaceAll(" ", "").replaceAll("\n", "").replaceAll(" ", "").replaceAll("\r\n", "").replaceAll("　　", "");
//	        }
//	     }
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPage_type() {
		return page_type;
	}

	public void setPage_type(String page_type) {
		this.page_type = page_type;
	}

	public String getSpot_code() {
		return spot_code;
	}

	public void setSpot_code(String spot_code) {
		this.spot_code = spot_code;
	}

	public String getSource_type() {
		return source_type;
	}

	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public KwScore getKs() {
		return ks;
	}

	public void setKs(KwScore ks) {
		this.ks = ks;
	}

	public String getReferUrl() {
		return referUrl;
	}

	public void setReferUrl(String referUrl) {
		this.referUrl = referUrl;
	}

	public String getIpArea() {
		return ipArea;
	}

	public void setIpArea(String ipArea) {
		this.ipArea = ipArea;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public WordTypeEnum getWordType() {
		return wordType;
	}

	public void setWordType(WordTypeEnum wordType) {
		this.wordType = wordType;
	}


	
}
