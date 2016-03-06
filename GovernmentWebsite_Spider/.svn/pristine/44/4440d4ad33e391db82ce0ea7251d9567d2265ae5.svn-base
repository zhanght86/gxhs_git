package com.meiah.util;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.heaton.bot.Attribute;
import com.heaton.bot.AttributeList;
import com.heaton.bot.CookieParse;
import com.heaton.bot.HTTP;
import com.heaton.bot.HTTPSocket;
import com.meiya.client.Proxy;
import com.meiya.client.RequestSender;

import net.sf.json.JSONSerializer;

/**
 * 网页下载类
 * 
 * @author dingrr
 * @Date 2015年12月3日
 */
public class NanyangWebPageDownloader extends WebPageDownloader {
	public NanyangWebPageDownloader(String url) {
		super(url);
	}

	/**
	 * 抓取html页面源代码，如果需要，设置cookie、验证转向，判断内容编码
	 * 
	 * @return
	 */
	public String getPageContent() throws Exception {
		String webContent = "";
		String tempUrl = this.url;
		if (tempUrl.endsWith(".pdf")) {
			String regex = "[\\u4e00-\\u9fa5]";
			String[] chinese = JavaUtil.match(tempUrl, regex);
			if (null != chinese && chinese.length > 0) {
				tempUrl = URLEncoder.encode(tempUrl);
			}
			webContent = PDFPaserUtil.pasePDF2HTMLByHttp(tempUrl);
			return webContent;
		}
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
		for (int i = 0; i < 5; i++) {
			long time = System.currentTimeMillis();
			byte[] _buff1 = null;
			try {
				HTTP _http = new HTTPSocket();
				_http.setUseCookies(true, true);// 设置会话cookie为真，持久性cookie为真
				_http.setTimeout(60 * 1000);
				_http.getClientHeaders().add(
						new Attribute("Accept-Encoding", "gzip"));
				_http.getClientHeaders().add(
						new Attribute("Accept-Language", "*"));
				_http.getClientHeaders().add(
						new Attribute("Connection", "close"));
				_http.setAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.3; .NET CLR 2.0.50727)");
				Proxy proxy = null;
				int freshTime = 500;
				long startTime = new Date().getTime();
				String port = Config.getProxyCenterPort();
				int proxyPort = 0;
				if(StringUtils.isNotBlank(port)) {
					proxyPort = Integer.valueOf(port);
				}
				while (true) {
					try {
						proxy = RequestSender.getProxy(match(url, "https?://([^/]*)")[1],Config.getProxyCenterUrl(),proxyPort);
					} catch (Exception e1) {
						logger.error("Exception:", e1);
					}
					if (proxy != null) {
						this.proxy = proxy;
						logger.info("Proxy:"
								+ (JSONSerializer.toJSON(proxy).toString()));
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
					// if (useProxyIf) {
					String proxyStr = "http://" + proxy.getIp() + ":"
							+ proxy.getPort() + "@" + proxy.getUser() + ":"
							+ proxy.getPwd() + "";
					_http.proxyStr = proxyStr;
					_http.initProxy();
					// }
					if (this.customHeader != null
							&& !this.customHeader.equals("")) {
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
					if (this.customHeader != null
							&& !this.customHeader.equals("")) {
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
					_http.SetAutoRedirect(true);// 设置屏蔽bot的httpclient自动判断跳转的功能，由本地判断是否跳转
					if (null != userCookieList)
						_http.cookieStore = userCookieList;// 附加已经获取的cookie
					tempUrl = JavaUtil.encodeURL(tempUrl);
					_http.send(tempUrl, null);
					responseHead = _http.getServerHeaders();
					Attribute contenttype = responseHead.get("content-type");
					if (contenttype != null) {
						String contentType = contenttype.getValue()
								.toLowerCase();
						if (contentType.indexOf("text") == -1
								&& contentType.indexOf("application/json") == -1
								&& contentType.indexOf("application/xml") == -1
								&& contentType
										.indexOf("application/javascript") == -1
								&& contentType
										.indexOf("application/octet-stream") == -1
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

	public static void main(String[] args) {
		String random = new Random().nextDouble() + "";

		NanyangWebPageDownloader wd = new NanyangWebPageDownloader(
				"http://www.nydt.cn/News/ShowClass.asp?ClassID=34");
		wd.setDecodeEntityIf(true);
		wd.setCheckCookie(true);
		wd.setCheckRedirect(true);
		// wd.setProxyUrl("125.208.18.122");
		// wd.setProxyPort("9048");
		// wd.setProxyUser("test");
		// wd.setProxyPwd("qwer1011");
		// wd.setUseProxyIf(true);
		try {
			String info = wd.getPageContent();
//			System.out.println("内容：" + info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
