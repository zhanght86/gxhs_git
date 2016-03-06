package com.meiah.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.heaton.bot.Attribute;
import com.heaton.bot.AttributeList;
import com.heaton.bot.CookieParse;
import com.heaton.bot.HTTP;
import com.heaton.bot.HTTPSocket;
import com.meiah.po.RunningTask;
import com.meiya.client.Proxy;
import com.meiya.client.RequestSender;

import net.sf.json.JSONSerializer;

/**
 * 网页下载类
 * 
 * @author dingrr
 * @Date 2015年12月3日
 */
public class WebPageDownloader {
	public static final String COOKIE_SEPERATER = "###";
	private RunningTask rtask;
	protected Proxy proxy;
	protected Logger logger = Logger.getLogger(WebPageDownloader.class);
	protected AttributeList responseHead;
	protected AttributeList userCookieList;
	protected String url;
	protected String proxyUrl;
	protected String proxyPort;
	protected String proxyUser;
	protected String proxyPwd;
	protected boolean useProxyIf = false;// 是否使用代理
	protected boolean checkCookieIf = false;// 是否检查cookie设置
	protected boolean checkRedirectIf = false;// 是否检查url重定向
	protected String cookieStr;// cookie串设置
	protected String cookieGot = "";// cookie新获得的cookie
	protected int encode = -1;// 页面编码 默认-1（自动探测）
	protected String redirectUrl;
	public String customHeader;
	protected String contentType = "text";
	public boolean decodeEntityIf = true;

	public boolean isDecodeEntityIf() {
		return decodeEntityIf;
	}

	public void setDecodeEntityIf(boolean decodeEntityIf) {
		this.decodeEntityIf = decodeEntityIf;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public WebPageDownloader(String url) {

		this.url = url;

	}

	public int getEncode() {
		return encode;
	}

	public void setEncode(int encode) {
		this.encode = encode;
	}

	public String getCustomHeader() {
		return customHeader;
	}

	public void setCustomHeader(String customHeader) {
		this.customHeader = customHeader;
	}

	public boolean isCheckCookie() {
		return checkCookieIf;
	}

	public void setCheckCookie(boolean checkCookie) {
		this.checkCookieIf = checkCookie;
	}

	public boolean isCheckRedirect() {
		return checkRedirectIf;
	}

	public void setCheckRedirect(boolean checkRedirect) {
		this.checkRedirectIf = checkRedirect;
	}

	public boolean isUseProxyIf() {
		return useProxyIf;
	}

	public void setUseProxyIf(boolean useProxyIf) {
		this.useProxyIf = useProxyIf;
	}

	public String getProxyUrl() {
		return proxyUrl;
	}

	public void setProxyUrl(String proxyUrl) {
		this.proxyUrl = proxyUrl;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPwd() {
		return proxyPwd;
	}

	public void setProxyPwd(String proxyPwd) {
		this.proxyPwd = proxyPwd;
	}

	public String getCookieStr() {
		return cookieStr;
	}

	public void setCookieStr(String cookieStr) {
		this.cookieStr = cookieStr;
	}

	public byte[] getImageContent() throws Exception {
		String webContent = "";
		String tempUrl = this.url;//
		if (checkCookieIf == true) {
			if (null != cookieStr && !cookieStr.trim().equals("")) {
				if (userCookieList == null)
					userCookieList = new AttributeList();
				String[] cookies = cookieStr.split(COOKIE_SEPERATER);
				for (int i = 0; i < cookies.length; i++) {
					CookieParse cookie = new CookieParse();
					cookie.source = new StringBuffer(cookies[i]);
					cookie.get();
					cookie.setName(cookie.get(0).getName());
					if (userCookieList.get(cookie.get(0).getName()) == null) {
						userCookieList.add(cookie);
					}
				}
			}
		}
		// 验证转向
		byte[] _buff1 = null;
		int retryCount = 3;
		for (int i = 0; i < retryCount; i++) {
			long time = System.currentTimeMillis();
			try {
				HTTP _http = new HTTPSocket();
				_http.setUseCookies(true, true);// 设置会话cookie为真，持久性cookie为真
				if (null != userCookieList)
					_http.cookieStore = userCookieList;// 附加已经获取的cookie
				_http.setTimeout(60 * 1000);
				_http.getClientHeaders().add(
						new Attribute("Accept-Encoding", "gzip,*"));
				_http.getClientHeaders().add(
						new Attribute("Accept-Language", "zh-cn"));
				_http.getClientHeaders().add(
						new Attribute("Cache-Control", "no-cache"));
				_http.getClientHeaders().add(
						new Attribute("Connection", "close"));
				_http.setAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322)");
				if (useProxyIf == true) {
					String proxyStr = "https://" + proxyUrl + ":" + proxyPort
							+ "@" + proxyUser + ":" + proxyPwd + "";
					_http.proxyStr = proxyStr;
					_http.initProxy();
				}
				_http.SetAutoRedirect(false);// 设置屏蔽bot的httpclient自动判断跳转的功能，由本地判断是否跳转
				_http.send(tempUrl, null);
				responseHead = _http.getServerHeaders();
				if (checkCookieIf == true && userCookieList == null) {
					if (checkSetCookie()) {
						try {
							Thread.sleep(50);
						} catch (Exception e) {
						}
						continue;
					}
				}
				_buff1 = (_http.getBodyBytes());// 取得body二进制内容
				if (checkRedirectIf == true) {
					String rUrl = checkRedirect(tempUrl, webContent);
					if (rUrl == null || rUrl.length() == 0) {
						break;
					} else {
						redirectUrl = getAbsoluteURL(tempUrl, rUrl);
						if (redirectUrl.indexOf("/", 9) == -1)
							redirectUrl = redirectUrl + "/";
						if (redirectUrl.equals(tempUrl)) {
							break;
						}
						tempUrl = redirectUrl;
						logger.info("链接：" + this.url + " 存在转向。当前为第 " + i
								+ " 次。转向链接为：" + tempUrl);
						continue;
					}
				} else {
					if (_buff1.length < 2048)
						continue;
					else
						break;
				}
			} catch (Exception e) {
				if (i == retryCount - 1) {
					logger.error("下载图片出现异常，链接地址:   " + tempUrl
							+ "\r\n异常信息（time out请尝试减少任务的线程数！）："
							+ e.getMessage() + "\r\n连接花费时间："
							+ (System.currentTimeMillis() - time) + " ms");
					throw e;
				} else
					continue;
			}
		}
		return _buff1;
	}

	public String getPageContent(int retryCount) throws Exception {
		String ret = "";
		if (retryCount < 1) {
			logger.warn("错误的重试次数:" + retryCount + ",默认为1!");
			retryCount = 1;
		}
		for (int i = 0; i < retryCount; i++) {
			try {
				ret = getPageContent();
				if (!ret.equals(""))
					break;
			} catch (Exception e) {
				if (i == retryCount - 1) {
					logger.error("重试了 " + retryCount + " 次下载，还是不成功，出现异常!"
							+ this.url, e);
				}
			}
		}
		return ret;
	}

	/**
	 * 抓取html页面源代码，如果需要，设置cookie、验证转向，判断内容编码
	 * 
	 * @return
	 */
	public String getPageContent() throws Exception {
		String webContent = "";
		String tempUrl = this.url;
		if (checkCookieIf == true) {
			if (null != cookieStr && !cookieStr.trim().equals("")) {
				if (userCookieList == null)
					userCookieList = new AttributeList();
				String[] cookies = cookieStr.split(COOKIE_SEPERATER);
				for (int i = 0; i < cookies.length; i++) {
					CookieParse cookie = new CookieParse();
					cookie.source = new StringBuffer(cookies[i]);
					cookie.get();
					cookie.setName(cookie.get(0).getName());
					if (userCookieList.get(cookie.get(0).getName()) == null) {
						userCookieList.add(cookie);
					}
				}
			}
		}
		// if(countValues != 0 && countValues % 100 == 0) {}
		// 验证转向
		for (int i = 0; i < 5; i++) {
			long time = System.currentTimeMillis();
			byte[] _buff1 = null;
			try {
				HTTP _http = new HTTPSocket();
				_http.setUseCookies(true, true);// 设置会话cookie为真，持久性cookie为真
				_http.setTimeout(60 * 1000);
				_http.getClientHeaders().add(new Attribute("Accept-Encoding", "gzip"));
				_http.getClientHeaders().add(new Attribute("Accept-Language", "*"));
				_http.getClientHeaders().add(new Attribute("Connection", "close"));
				_http.setAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.3; .NET CLR 2.0.50727)");
				if (useProxyIf) {
					Proxy proxy = null;
					proxy = getProxyByUrl(tempUrl);
					String proxyStr = "http://" + proxy.getIp() + ":" + proxy.getPort() + "@" + proxy.getUser() + ":" + proxy.getPwd() + "";
					_http.proxyStr = proxyStr;
					_http.initProxy();
				}
				if (this.customHeader != null && !this.customHeader.equals("")) {
					try {
						String[] headers = customHeader.split("####");
						for (int j = 0; j < headers.length; j++) {
							String header = headers[j];
							int pos = header.indexOf("=");
							String key = header.substring(0, pos);
							String value = header.substring(pos + 1,
									header.length());
							_http.getClientHeaders().add(
									new Attribute(key, value));
						}
					} catch (Exception e) {
						logger.error("添加请求头出现异常：", e);
					}
				}
				_http.SetAutoRedirect(false);// 设置屏蔽bot的httpclient自动判断跳转的功能，由本地判断是否跳转
				if (null != userCookieList)
					_http.cookieStore = userCookieList;// 附加已经获取的cookie
				tempUrl = JavaUtil.encodeURL(tempUrl);
				_http.send(tempUrl, null);
				responseHead = _http.getServerHeaders();
				Attribute contenttype = responseHead.get("content-type");
				if (contenttype != null) {
					String contentType = contenttype.getValue().toLowerCase();
					if (contentType.indexOf("text") == -1
							&& contentType.indexOf("application/json") == -1
							&& contentType.indexOf("application/xml") == -1
							&& contentType.indexOf("application/javascript") == -1
							&& contentType.indexOf("application/octet-stream") == -1
							&& contentType.indexOf(this.contentType) == -1)
						return "";
				}
				if (checkCookieIf == true) {
					if (checkSetCookie()) {
						try {
							Thread.sleep(50);
						} catch (Exception e) {
						}
						continue;
					}
				}
				_buff1 = (_http.getBodyBytes());// 取得body二进制内容
				webContent = getHtmlByCharsetDetect(_buff1);
				// System.out.println(webContent);
				// webContent = JavaUtil.readBytes(_buff1, encode,
				// decodeEntityIf);
				if (checkRedirectIf == true) {
					String rUrl = checkRedirect(tempUrl, webContent);
					if (rUrl == null || rUrl.length() == 0) {
						break;
					} else {
						redirectUrl = getAbsoluteURL(tempUrl, rUrl);
						if (redirectUrl.indexOf("/", 9) == -1)
							redirectUrl = redirectUrl + "/";
						if (redirectUrl.equals(tempUrl)) {
							break;
						}
						tempUrl = redirectUrl;
						if (logger.isDebugEnabled())
							logger.debug("链接：" + this.url + " 存在转向。当前为第 "
									+ i + " 次。转向链接为：" + tempUrl);
					}
				} else {
					break;
				}
			} catch (Exception e) {
				logger.error("下载网页出现异常，链接地址:   " + tempUrl
						+ "\r\n异常信息（time out请尝试减少任务的线程数！）：" + e.getMessage()
						+ "\r\n连接花费时间：" + (System.currentTimeMillis() - time)
						+ " ms");
				// e.printStackTrace();
				throw e;
				
			}
		}
		return webContent;
	}
	public Proxy getProxyByUrl(String tempUrl) {
		int freshTime = 500;
		long startTime = new Date().getTime();
		Proxy proxy = null;
		while (true) {
			try {
				proxy = RequestSender.getProxy(match(tempUrl, "https?://([^/]*)")[1],Config.getProxyCenterUrl(),Integer.valueOf(Config.getProxyCenterPort()));
			} catch (Exception e1) {
				logger.error("Exception:", e1);
			}
			if (proxy != null) {
				this.proxy = proxy;
				logger.info("Proxy:"+ (JSONSerializer.toJSON(proxy).toString()));
				break;
			} else {
				try {
					logger.debug("请求延时：" + freshTime + "ms");
				} catch (Exception e) {
				}
			}
			if ((new Date().getTime() - startTime) > 10000) {
				break;
			}
		}
		return proxy;
	}

	/**
	 * 检查是否包含链接转向，3种方法<br>
	 * <ol>
	 * <li>头部包含“location:”，返回代号302</li>
	 * <li>内容部分包含“meta http-equiv=refresh content="2;URL=..."”</li>
	 * <li>js脚本刷新，正则为：
	 * "(?s)<script.{0,50}?>\\s*((document)|(window)|(this))\\.location(\\.href)?\\s*="
	 * </li>
	 * </ol>
	 */
	protected String checkRedirect(String url, String webcontent) {
		String redirectUrl = "";
		// 1.
		for (int i = 0; i < this.responseHead.length(); i++) {
			if (this.responseHead.get(i) == null)
				continue;
			if ("location".equals(this.responseHead.get(i).getName()
					.toLowerCase())) {
				redirectUrl = this.responseHead.get(i).getValue();
				// redirectUrl = initUrl(url, redirectUrl);
				return redirectUrl;
			}
		}

		// 2.
		String bodyLocationStr = "";
		if (webcontent.length() > 5120) {
			bodyLocationStr = webcontent.substring(0, 5120);// 太长则截取部分内容
		} else {
			bodyLocationStr = webcontent;
		}
		bodyLocationStr = bodyLocationStr.replaceAll("<!--(?s).*?-->", "")
				.replaceAll("['\"]", "");// 去除注释和引号部分
		int metaLocation = -1;
		metaLocation = bodyLocationStr.toLowerCase().indexOf(
				"http-equiv=refresh");
		if (metaLocation != -1) {
			int posOfEndTag = bodyLocationStr.indexOf("/>", metaLocation);
			int indexEnd = -1;
			if (posOfEndTag != -1) {
				indexEnd = posOfEndTag > bodyLocationStr.indexOf(">",
						metaLocation) ? bodyLocationStr.indexOf(">",
						metaLocation) : bodyLocationStr.indexOf("/>",
						metaLocation);
			} else {
				indexEnd = bodyLocationStr.indexOf(">", metaLocation);
			}

			String locationPart = bodyLocationStr.substring(metaLocation,
					indexEnd).replaceAll(" ", "");
			metaLocation = locationPart.toLowerCase().indexOf("url");
			if (metaLocation != -1) {
				// 假定url=...是在 > 之前最后的部分
				redirectUrl = locationPart.substring(metaLocation + 4,
						locationPart.length()).replaceAll("\\s+[^>]*", "");
				// redirectUrl = initUrl(url, redirectUrl);
				return redirectUrl;
			}
		}

		// 3.
		Matcher locationMath = Pattern
				.compile(
						"(?s)(?i)<script[^>]*?>\\s*?((document)|(window)|(this)|(self\\.parent)|(top))?(\\.)?location(\\.href)?\\s*=")
				.matcher(webcontent.toLowerCase());
		if (locationMath.find()) {
			String[] cs = webcontent.substring(locationMath.end()).trim()
					.split("[> ;<]");
			redirectUrl = cs[0].replaceAll("[\"|'\\\\]", "");
			cs = null;
			return redirectUrl;
		}
		Matcher locationMath1 = Pattern
				.compile(
						"(?s)(?i)<script[^>]*?>\\s*?((document)|(window)|(this)|(self\\.parent)|(top))?(\\.)?location\\.replace\\(")
				.matcher(webcontent.toLowerCase());
		if (locationMath1.find()) {
			String[] cs = webcontent.substring(locationMath1.end()).trim()
					.split("[> ;<]");
			redirectUrl = cs[0].replaceAll("[\"|'\\\\\\)\\(]", "");
			cs = null;
			return redirectUrl;
		}
		return "";
	}

	/**
	 * 检查返回请求头是否要求设置COOKIE
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	protected boolean checkSetCookie() {
		boolean flag = false;
		Attribute a;

		int i = 0;
		while ((a = responseHead.get(i++)) != null) {
			if (a.getName().equalsIgnoreCase("set-cookie")) {
				CookieParse cookie = new CookieParse();
				String cookieS = a.getValue();
				cookie.source = new StringBuffer(cookieS);
				cookie.get();
				cookie.setName(cookie.get(0).getName());
				if (userCookieList == null) {
					flag = true;
					userCookieList = new AttributeList();
					userCookieList.add(cookie);
					this.setCookieGot(cookieS);
				} else if (userCookieList.get(cookie.get(0).getName()) == null) {
					flag = true;
					userCookieList.add(cookie);
					this.setCookieGot(cookieS);
				}
			}
		}

		return flag;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Build a URL from the link and base provided.
	 * 
	 * @param link
	 *            The (relative) URI.
	 * @param base
	 *            The base URL of the page, either from the &lt;BASE&gt; tag or,
	 *            if none, the URL the page is being fetched from.
	 * @param strict
	 *            If <code>true</code> a link starting with '?' is handled
	 *            according to <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC
	 *            2396</a>, otherwise the common interpretation of a query
	 *            appended to the base is used instead.
	 * @return An absolute URL.
	 * @exception MalformedURLException
	 *                If creating the URL fails.
	 */
	public URL constructUrl(String link, String base, boolean strict)
			throws MalformedURLException {
		String path;
		boolean modified;
		boolean absolute;
		int index;
		URL url;
		if (!strict && ('?' == link.charAt(0))) { // remove query part of base
			if (-1 != (index = base.lastIndexOf('?')))
				base = base.substring(0, index);
			url = new URL(base + link);
		} else
			url = new URL(new URL(base), link);
		path = url.getFile();
		modified = false;
		absolute = link.startsWith("/");
		if (!absolute) { // we prefer to fix incorrect relative links
			// this doesn't fix them all, just the ones at the start
			while (path.startsWith("/.")) {
				if (path.startsWith("/../")) {
					path = path.substring(3);
					modified = true;
				} else if (path.startsWith("/./") || path.startsWith("/.")) {
					path = path.substring(2);
					modified = true;
				} else
					break;
			}
		}
		// fix backslashes
		while (-1 != (index = path.indexOf("/\\"))) {
			path = path.substring(0, index + 1) + path.substring(index + 2);
			modified = true;
		}
		if (modified)
			url = new URL(url, path);

		return (url);
	}

	/**
	 * Create an absolute URL from a relative link.
	 * 
	 * @param link
	 *            The reslative portion of a URL.
	 * @return The fully qualified URL or the original link if it was absolute
	 *         already or a failure occured.
	 */
	public String getAbsoluteURL(String baseUrl, String link) {
		return (getAbsoluteURL(baseUrl, link, false));
	}

	/**
	 * Create an absolute URL from a relative link.
	 * 
	 * @param link
	 *            The reslative portion of a URL.
	 * @param strict
	 *            If <code>true</code> a link starting with '?' is handled
	 *            according to <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC
	 *            2396</a>, otherwise the common interpretation of a query
	 *            appended to the base is used instead.
	 * @return The fully qualified URL or the original link if it was absolute
	 *         already or a failure occured.
	 */
	public String getAbsoluteURL(String baseUrl, String link, boolean strict) {
		String base;
		URL url;
		String ret;

		if ((null == link) || ("".equals(link)))
			ret = "";
		else
			try {
				base = baseUrl;
				url = constructUrl(link, base, strict);
				ret = url.toExternalForm();
			} catch (MalformedURLException murle) {
				ret = link;
			}
		return (ret);
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public String getCookieGot() {
		return cookieGot;
	}

	public void setCookieGot(String cookieGot) {
		if (this.cookieGot.length() == 0)
			this.cookieGot = cookieGot;
		else {
			this.cookieGot = this.cookieGot + COOKIE_SEPERATER + cookieGot;
		}
	}

	/**
	 * 获取页面编码
	 *  
	 * @return
	 */
	public String getHtmlByCharsetDetect(byte[] content) {
		if (content == null) {
			return null;
		}
		String charset = CharsetDetector.guessEncoding(content);
		try {
			String html = new String(content, charset);
			return html;
		} catch (Exception ex) {
			logger.info("Exception", ex);
			return null;
		}
	}

	public static void main(String[] args) {
		String random = new Random().nextDouble() + "";

		WebPageDownloader wd = new WebPageDownloader(
				"http://www.xzzbtb.gov.cn/xz/publish-notice!view.do?searchType=SCCINNOTICE&SID=4028818a5262852a01527d403e101f81");
		wd.setDecodeEntityIf(false);
		wd.setCheckCookie(true);
		wd.setCheckRedirect(true);
		// wd.setProxyUrl("125.208.18.122");
		// wd.setProxyPort("9048");
		// wd.setProxyUser("test");
		// wd.setProxyPwd("qwer1011");
		try {
			String info = wd.getPageContent();
			System.out.println(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 作者: huangqj 版本: Nov 18, 2014 4:16:03 PM v1.0 日期: Nov 18, 2014 参数: @param
	 * s 参数: @param pattern 参数: @return 描述: 匹配字符串
	 */
	public String[] match(String s, String pattern) {
		Matcher m = Pattern.compile(pattern).matcher(s);

		while (m.find()) {
			int n = m.groupCount();
			String[] ss = new String[n + 1];
			for (int i = 0; i <= n; i++) {
				ss[i] = m.group(i);
			}
			return ss;
		}
		return null;
	}
}