package com.meiah.po;

import java.util.Date;
import java.util.Random;

/**
 * @author： 胡海斌
 * @Date： Jul 2, 2010
 * @For
 */
public class Task {
	public static final int RUNNING = 0;// 运行中
	public static final int PAUSE = 9;// 暂停
	public static final int STOP = 2;// 停止
	public static final int RESTART = 4;// 重启
	private SiteConfig siteConfig;
	public boolean pluginMode = false;
	public SiteConfig getSiteConfig() {
		return siteConfig;
	}

	public void setSiteConfig(SiteConfig siteConfig) {
		this.siteConfig = siteConfig;
	}

	private String taskid;// 任务ID
	private String turl;// URL地址
	private String subBoardUrl;// 任务子版块地址
	private String tname;// 任务名称
	private String type;// 解析类型

	private Integer sleeptime = 1;// 任务完成一轮后休息时间：s
	private Integer maxThreads = 20;// 一个任务最多使用线程数
	private Integer layers;// 爬虫爬取的层数（从当前链接开始，链接下的所有链接为下一层）
	private Integer pages;// 爬虫爬取最大页数

	private String prefix;// 任务前缀
	private String substr;// 任务排除的字词，用逗号隔开

	private String beginTitle;
	private String endTitle;
	private String beginCode;
	private String endCode;
	private String beginSource;
	private String endSource;
	private String beginAuthor;
	private String endAuthor;
	private String beginColumnName;
	private String endColumnName;
	private String beginDocumentName;
	private String endDocumentName;
	private String beginImgUrl;
	private String endImgUrl;
	private Date lastdate;// 上次更新时间
	private Date nowdate;// 上次更新时间
	private String dateFormat;
	private String datePrefix;
	private int datePos;
	private String pageType;
	private String proxyurl;// 请求时代理地址
	private String proxyport;// 请求时代理端口
	private String proxyuser;// 请求时代理用户名
	private String proxypwd;// 请求时代理密码
	private Integer Timeout;
	private Integer Status;
	private Integer Userid;
	private String ucookies;// cookies
	private int encode = -1;// 保存记录编码
	private String newsType;		//新闻类型（智库/财经/政府网站/发改委）
	public static Task getSampleTask(String url) {
		Task a = new Task();
		a.setUrl(url);
		Random r = new Random(new Date().getTime());
		String taskid = r.nextInt() + "";
		a.setTaskid(taskid);
		a.setMaxLevel(5);
		a.setMaxThreads(20);
		return a;
	};
	
	public String getNewsType() {
		return newsType;
	}

	public void setNewsType(String newsType) {
		this.newsType = newsType;
	}

	public String getBeginImgUrl() {
		return beginImgUrl;
	}

	public void setBeginImgUrl(String beginImgUrl) {
		this.beginImgUrl = beginImgUrl;
	}

	public String getEndImgUrl() {
		return endImgUrl;
	}

	public void setEndImgUrl(String endImgUrl) {
		this.endImgUrl = endImgUrl;
	}

	public String getBeginColumnName() {
		return beginColumnName;
	}

	public void setBeginColumnName(String beginColumnName) {
		this.beginColumnName = beginColumnName;
	}

	public String getEndColumnName() {
		return endColumnName;
	}

	public void setEndColumnName(String endColumnName) {
		this.endColumnName = endColumnName;
	}

	public String getBeginDocumentName() {
		return beginDocumentName;
	}

	public void setBeginDocumentName(String beginDocumentName) {
		this.beginDocumentName = beginDocumentName;
	}

	public String getEndDocumentName() {
		return endDocumentName;
	}

	public void setEndDocumentName(String endDocumentName) {
		this.endDocumentName = endDocumentName;
	}

	public int getEncode() {
		return encode;
	}

	public void setEncode(int encode) {
		this.encode = encode;
	}

	public Integer getSleeptime() {
		return sleeptime;
	}

	public void setSleeptime(Integer sleeptime) {
		this.sleeptime = sleeptime;
	}

	public String getUcookies() {
		return ucookies;
	}

	public void setUcookies(String ucookies) {
		this.ucookies = ucookies;
	}

	public String getProxyurl() {
		return proxyurl;
	}

	public void setProxyurl(String proxyurl) {
		this.proxyurl = proxyurl;
	}

	public String getProxyport() {
		return proxyport;
	}

	public void setProxyport(String proxyport) {
		this.proxyport = proxyport;
	}

	public String getProxyuser() {
		return proxyuser;
	}

	public void setProxyuser(String proxyuser) {
		this.proxyuser = proxyuser;
	}

	public String getProxypwd() {
		return proxypwd;
	}

	public void setProxypwd(String proxypwd) {
		this.proxypwd = proxypwd;
	}

	// public String getDeword() {
	// return deword;
	// }
	//
	// public void setDeword(String deword) {
	// this.deword = deword;
	// }

	public String getUrl() {
		return turl;
	}

	public void setUrl(String url) {
		this.turl = url;
	}

	// public String getType() {
	// return type;
	// }
	//
	// public void setType(String type) {
	// this.type = type;
	// }

	public int getMaxthread() {
		return maxThreads;
	}

	public void setMaxthread(int maxthread) {
		this.maxThreads = maxthread;
	}

	public Date getLastdate() {
		return lastdate;
	}

	public void setLastdate(Date lastdate) {
		this.lastdate = lastdate;
	}

	public Date getNowdate() {
		return nowdate;
	}

	public void setNowdate(Date nowdate) {
		this.nowdate = nowdate;
	}

	/**
	 * ID相同则认为任务相同
	 */
	@Override
	public boolean equals(Object task) {
		boolean result = false;

		if (this.taskid.equals(((Task) task).getTaskid())) {
			result = true;
		}

		return result;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public int getMaxLevel() {
		return layers;
	}

	public void setMaxLevel(int maxLevel) {
		this.layers = maxLevel;
	}

	public Integer getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(Integer maxThreads) {
		this.maxThreads = maxThreads;
	}

	public String getTurl() {
		return turl;
	}

	public void setTurl(String turl) {
		this.turl = turl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public Integer getLayers() {
		return layers;
	}

	public void setLayers(Integer layers) {
		this.layers = layers;
	}

	public Integer getTimeout() {
		return Timeout;
	}

	public void setTimeout(Integer timeout) {
		Timeout = timeout;
	}

	public Integer getStatus() {
		return Status;
	}

	public void setStatus(Integer status) {
		Status = status;
	}

	public Integer getUserid() {
		return Userid;
	}

	public void setUserid(Integer userid) {
		Userid = userid;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSubstr() {
		return substr;
	}

	public void setSubstr(String substr) {
		this.substr = substr;
	}

	public String getBeginTitle() {
		return beginTitle;
	}

	public void setBeginTitle(String beginTitle) {
		this.beginTitle = beginTitle;
	}

	public String getEndTitle() {
		return endTitle;
	}

	public void setEndTitle(String endTitle) {
		this.endTitle = endTitle;
	}

	public String getBeginCode() {
		return beginCode;
	}

	public void setBeginCode(String beginCode) {
		this.beginCode = beginCode;
	}

	public String getEndCode() {
		return endCode;
	}

	public void setEndCode(String endCode) {
		this.endCode = endCode;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public int getDatePos() {
		return datePos;
	}

	public void setDatePos(int datePos) {
		this.datePos = datePos;
	}

	public String getDatePrefix() {
		return datePrefix;
	}

	public void setDatePrefix(String datePrefix) {
		this.datePrefix = datePrefix;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getBeginSource() {
		return beginSource;
	}

	public void setBeginSource(String beginSource) {
		this.beginSource = beginSource;
	}

	public String getEndSource() {
		return endSource;
	}

	public void setEndSource(String endSource) {
		this.endSource = endSource;
	}

	public String getBeginAuthor() {
		return beginAuthor;
	}

	public void setBeginAuthor(String beginAuthor) {
		this.beginAuthor = beginAuthor;
	}

	public String getEndAuthor() {
		return endAuthor;
	}

	public void setEndAuthor(String endAuthor) {
		this.endAuthor = endAuthor;
	}

	public String getSubBoardUrl() {
		return subBoardUrl;
	}

	public void setSubBoardUrl(String subBoardUrl) {
		this.subBoardUrl = subBoardUrl;
	}

}
