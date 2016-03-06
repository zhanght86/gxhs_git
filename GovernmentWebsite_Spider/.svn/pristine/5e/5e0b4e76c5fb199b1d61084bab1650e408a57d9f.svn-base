package com.meiah.httpclient;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.meiah.util.JavaUtil;
import com.meiah.util.WebPageDownloader;

public class HttpFetcher extends WebPageDownloader {
	private Logger logger = Logger.getLogger(HttpFetcher.class);

	public HttpFetcher(String url) {
		super(url);
	}

	@Override
	public byte[] getImageContent() throws Exception {
		byte[] ret = null;
		return ret;
	}

	@Override
	public String getPageContent(int retryCount) throws Exception {
		String ret = "";
		HttpContext context = new BasicHttpContext();
		HttpGet httpget = new HttpGet(url);

		HttpClient httpClient = HttpClientGenerator.getHttpClient();
		HttpResponse response = httpClient.execute(httpget, context);
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				byte[] temp = EntityUtils.toByteArray(entity);
				ret = JavaUtil.readBytes(temp, -1, this.decodeEntityIf);
			}
		}
		return ret;
	}

	/**
	 * 抓取html页面源代码，如果需要，设置cookie、验证转向，判断内容编码
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */

	public String getPageContent() {

		String ret = "";
		String tempUrl = this.url;// 
		for (int i = 0; i < 5; i++) {
			HttpContext context = new BasicHttpContext();
			HttpGet httpget = new HttpGet(tempUrl);
			HttpClient httpClient = HttpClientGenerator.getHttpClient();
			// httpget
			// .addHeader(
			// "Accept",
			// "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-ms-application, application/x-ms-xbap, application/vnd.ms-xpsdocument, application/xaml+xml, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			//
			// httpget.addHeader("Accept-Language", "zh-cn");
			HttpResponse response;
			try {
				long start = System.currentTimeMillis();
				response = httpClient.execute(httpget, context);
				long taskTime = System.currentTimeMillis() - start;
				Header head = response.getFirstHeader("Content-Type");
				if (head.getValue().toLowerCase().indexOf("text") == -1)
					return ret;
				int responseCode = response.getStatusLine().getStatusCode();
				if (responseCode != HttpStatus.SC_OK) {
					logger.error("warn! at " + url + ",return code:"
							+ responseCode);
					EntityUtils.consume(response.getEntity());
					httpget.abort();
					return ret;
				}
				start = System.currentTimeMillis();
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					byte[] temp = EntityUtils.toByteArray(entity);
					ret = JavaUtil.readBytes(temp, -1, true);
					if (checkRedirectIf == true) {
						String rUrl = checkRedirect(tempUrl, ret);

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

				long readtime = System.currentTimeMillis() - start;

			} catch (Exception e) {
				logger.error("err at " + url + ",errmsg:" + e.getMessage());
				httpget.abort();
			} finally {

			}
		}

		return ret;
	}

//	/**
//	 * 抓取html页面源代码，如果需要，设置cookie、验证转向，判断内容编码
//	 * 
//	 * @return
//	 */
//	@Override
//	public String getPageContent() throws Exception {
//		String ret = "";
//		HttpContext context = new BasicHttpContext();
//		String tempUrl = this.url;// 
//		for (int i = 0; i < 5; i++) {
//			HttpGet httpget = new HttpGet(tempUrl);
//			httpget
//					.setHeader(
//							"Cookie",
//							"__cfduid=dbe9ed37779331e451f5d2e93e37c1d161367563397; cf_clearance=8923e0f0ea223f3fe96d9ddd09decff8-1367563407-604800;");
//			HttpClient httpClient = HttpClientGenerator.getHttpClient();
//			HttpResponse response = httpClient.execute(httpget, context);
//			if (response.getStatusLine().getStatusCode() == 200) {
//				HttpEntity entity = response.getEntity();
//				if (entity != null) {
//					byte[] temp = EntityUtils.toByteArray(entity);
//					ret = JavaUtil.readBytes(temp, -1, this.decodeEntityIf);
//					if (checkRedirectIf == true) {
//						String rUrl = checkRedirect(tempUrl, ret);
//
//						if (rUrl == null || rUrl.length() == 0) {
//							break;
//						} else {
//							redirectUrl = getAbsoluteURL(tempUrl, rUrl);
//							if (redirectUrl.indexOf("/", 9) == -1)
//								redirectUrl = redirectUrl + "/";
//							if (redirectUrl.equals(tempUrl)) {
//								break;
//							}
//
//							tempUrl = redirectUrl;
//							if (logger.isDebugEnabled())
//								logger.debug("链接：" + this.url + " 存在转向。当前为第 "
//										+ i + " 次。转向链接为：" + tempUrl);
//						}
//					} else {
//						break;
//
//					}
//				}
//			}
//		}
//		return ret;
//	}

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

		// 2.
		String bodyLocationStr = "";
		if (webcontent.length() > 5120) {
			bodyLocationStr = webcontent.substring(0, 5120);// 太长则截取部分内容
		} else {
			bodyLocationStr = webcontent;
		}
		bodyLocationStr = bodyLocationStr.replaceAll("<!--(?s).*?-->", "")
				.replaceAll("['\"]", "");// 去除注释和引号部分
		// System.out.println(bodyLocationStr);
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
			// redirectUrl = initUrl(url, redirectUrl);
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
			// redirectUrl = initUrl(url, redirectUrl);
			cs = null;

			return redirectUrl;
		}

		// if (webcontent.indexOf("></body>") != -1) {
		// Matcher locationMatch = Pattern
		// .compile(
		// "(?s)(?i)<script.{0,50}?>.*?((document)|(window)|(this)|(self\\.parent))(\\.)location(\\.href)?\\s*=")
		// .matcher(webcontent.toLowerCase());
		// if (locationMatch.find()) {
		// String[] cs = webcontent.substring(locationMatch.end()).trim()
		// .split("[> ;<]");
		//
		// redirectUrl = cs[0].replaceAll("[\"'\\}]", "");
		// // redirectUrl = initUrl(url, redirectUrl);
		// cs = null;
		// return redirectUrl;
		// }
		// }

		return "";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebPageDownloader wd = new HttpFetcher("http://dpaper.sxcm.net/");
		wd.setCheckRedirect(true);
		String info = "";
		long t = System.currentTimeMillis();
		try {

			info = wd.getPageContent();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("time consumed:" + (System.currentTimeMillis() - t)
//				+ " ms");
//		System.out.println(info);

	}

}
