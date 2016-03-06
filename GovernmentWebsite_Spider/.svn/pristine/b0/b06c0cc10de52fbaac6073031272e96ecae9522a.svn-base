package com.meiah.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.heaton.bot.Attribute;
import com.heaton.bot.AttributeList;
import com.heaton.bot.CookieParse;
import com.heaton.bot.HTTP;
import com.heaton.bot.HTTPSocket;

public class BotFetcher extends WebContentFetcher {

	private Logger logger = Logger.getLogger(BotFetcher.class);
	private AttributeList responseHead;
	private AttributeList userCookieList;

	public BotFetcher(String url) {
		super(url);

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
	private String checkRedirect(String url, String webcontent) {
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
			String locationPart = bodyLocationStr.substring(metaLocation,
					bodyLocationStr.indexOf(">", metaLocation)).replaceAll(" ",
					"");
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
						"(?s)(?i)<script.{0,50}?>\\s*?((document)|(window)|(this)|(self\\.parent))?(\\.)?location(\\.href)?\\s*=")
				.matcher(webcontent.toLowerCase());
		if (locationMath.find()) {
			String[] cs = webcontent.substring(locationMath.end()).trim()
					.split("[> ;<]");

			redirectUrl = cs[0].replaceAll("\"|'", "");
			// redirectUrl = initUrl(url, redirectUrl);
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
	private boolean checkSetCookie() {
		boolean flag = false;
		Attribute a;

		int i = 0;
		while ((a = responseHead.get(i++)) != null) {
			if (a.getName().equalsIgnoreCase("set-cookie")) {
				flag = true;
				CookieParse cookie = new CookieParse();
				String cookieS = a.getValue();
				this.setCookieGot(cookieS);
				cookie.source = new StringBuffer(cookieS);
				cookie.get();
				cookie.setName(cookie.get(0).getName());
				if (userCookieList == null)
					userCookieList = new AttributeList();
				if (userCookieList.get(cookie.get(0).getName()) == null) {
					userCookieList.add(cookie);
				}
			}
		}

		return flag;
	}

	public String getPageContent() throws Exception {
		if (contentType == null || contentType.equals("")) {
			contentType = "text/html";// 默认html
		}
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

			// else {
			// logger.warn("cookie为空！");
			// }
		}
		// 验证转向

		for (int i = 0; i < 5; i++) {
			long time = System.currentTimeMillis();
			byte[] _buff1 = null;
			try {
				HTTP _http = new HTTPSocket();
				// if (logger.isDebugEnabled())

				// Log.setLevel(1);
				_http.setUseCookies(true, true);// 设置会话cookie为真，持久性cookie为真
				if (null != userCookieList)
					_http.cookieStore = userCookieList;// 附加已经获取的cookie
				_http.setTimeout(60 * 1000);
				_http.getClientHeaders().add(new Attribute("Accept", "*/*"));
				_http.getClientHeaders().add(
						new Attribute("Accept-Encoding", "gzip,*"));
				_http.getClientHeaders().add(
						new Attribute("Accept-Language", "zh-tw, zh-cn"));
				_http.getClientHeaders().add(
						new Attribute("Cache-Control", "no-cache"));

				_http.getClientHeaders().add(
						new Attribute("Connection", "close"));
				if (this.customHeader != null && !this.customHeader.equals("")) {

					try {
						String[] headers = customHeader.split("####");
						for (int j = 0; j < headers.length; j++) {
							String header = headers[j];
							String key = header.split("=")[0];
							String value = header.split("=")[1];
							_http.getClientHeaders().add(
									new Attribute(key, value));
						}
					} catch (Exception e) {
						logger.error("添加请求头出现异常：", e);
					}
				}
				// _http.getClientHeaders().add(
				// new Attribute("Connection", "Keep-Alive"));
				// _http.getClientHeaders().add(
				// new Attribute("Content-Type",
				// "application/x-www-form-urlencoded"));
				// _http.getClientHeaders().add(
				// new Attribute("x-requested-with",
				// "XMLHttpRequest"));
				if (eTag != null && !eTag.equals(""))
					_http.getClientHeaders().add(
							new Attribute("If-None-Match", eTag));
				else if (lastModifedTime != null && !lastModifedTime.equals(""))
					_http.getClientHeaders()
							.add(
									new Attribute("If-Modified-Since",
											lastModifedTime));
				_http
						.setAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
				if (useProxyIf == true) {
					String proxyStr = "https://" + proxyUrl + ":" + proxyPort
							+ "@" + proxyUser + ":" + proxyPwd + "";
					_http.proxyStr = proxyStr;
					_http.initProxy();
				}
				_http.SetAutoRedirect(false);// 设置屏蔽bot的httpclient自动判断跳转的功能，由本地判断是否跳转

				// long t = System.currentTimeMillis();
				if (this.isPost == false)
					_http.send(tempUrl, null);
				else
					_http.send(tempUrl, postString);
//				this.responseStatus = _http.statusCode;
//				if (responseStatus == 304) {
//					if (logger.isDebugEnabled())
//						logger.debug("网页内容没有发生改变：" + this.url);
//					return "";
//				}
				// logger.info(tempUrl + ",下载花费时间"
				// + (System.currentTimeMillis() - t) + " ms");
				responseHead = _http.getServerHeaders();

				for (int j = 0; j < responseHead.length(); j++) {
					Attribute header = responseHead.get(j);
					if (header == null)
						continue;
					if ("content-type".equals(header.getName().toLowerCase())) {
						String contentType = header.getValue().toLowerCase();
						if (contentType.indexOf(this.contentType) == -1)
							return "";
					} else if ("etag".equals(header.getName().toLowerCase())) {
						this.setETag(header.getValue());
					}
				}
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

				webContent = JavaUtil.readBytes(_buff1, encode, decodeEntityIf);

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
					}
				} else {
					break;

				}

			} catch (Exception e) {

				logger.error("下载网页出现异常，链接地址:   " + tempUrl
						+ "\r\n异常信息（time out请尝试减少任务的线程数！）：" + e.getMessage()
						+ "\r\n连接花费时间：" + (System.currentTimeMillis() - time)
						+ " ms", e);

				throw e;
			}

		}
		// logger.info("下载成功：" + this.url);
		return webContent;
	}

	public void addRequestHeader() {
		if (this.customHeader == null || this.customHeader.equals(""))
			return;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebContentFetcher wd = new BotFetcher(
				"http://api.baiyue.baidu.com/sn/api/recommendlist");

		// wd.setPost(true);
		// wd.setPostString("outType=3&m=get&id=5719385");
		wd.setCheckCookie(true);
		wd.setCheckRedirect(true);
		wd.setCustomHeader("User-Agent=bdnews_android_phone####Content-Type=application/x-www-form-urlencoded");
		wd.setPost(true);
		wd
				.setPostString("ts=0&ln=200&an=20&topic=%E4%BA%92%E8%81%94%E7%BD%91&from=news_smart&mid=352762051278546_1c%3Ab0%3A94%3A8a%3A4a%3A0b&mb=villec2&withtoppic=1&ver=2&internet-subscribe=1&cuid=5528DF3F85D1122F145FA2A11D0121FF&manu=HTC&token=H4sIAAA");
		// wd.setUseProxyIf(false);
		// wd.setProxyUrl("202.66.30.251");
		// wd.setProxyPort("443");
		// wd.setProxyUser("pico");
		// wd.setProxyPwd("pico2009server");

		// wd.setCustomHeader("Content-Type=application/x-www-form-urlencoded");
		try {
			String info = wd.getPageContent();
			// System.out.println("状态码：" + wd.getResponseStatus());
//			System.out.println(info);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
