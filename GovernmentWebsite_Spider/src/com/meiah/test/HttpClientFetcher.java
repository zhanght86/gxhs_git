package com.meiah.test;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.meiah.util.JavaUtil;

public class HttpClientFetcher {
	private static Logger logger = Logger.getLogger(HttpClientFetcher.class);

	public byte[] getImageContent() throws Exception {
		byte[] ret = null;
		return ret;
	}

	public String getPageContent(String url, int retryCount) throws Exception {
		String ret = "";
		HttpContext context = new BasicHttpContext();

		HttpGet httpget = new HttpGet(url);

		HttpClient httpClient = HttpClientGenerator.getHttpClient();
		HttpResponse response = httpClient.execute(httpget, context);
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				byte[] temp = EntityUtils.toByteArray(entity);
				ret = JavaUtil.readBytes(temp, -1, true);
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

	public static String getPageContent(String url, HttpHost proxy) {

		String ret = "";
		HttpContext context = new BasicHttpContext();
		HttpGet httpget = new HttpGet(url);
		HttpClient httpClient = HttpClientGenerator.getHttpClient();

		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		httpget
				.addHeader(
						"Accept",
						"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-ms-application, application/x-ms-xbap, application/vnd.ms-xpsdocument, application/xaml+xml, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");

		httpget.addHeader("Accept-Language", "zh-cn");
		HttpResponse response;
		try {
			response = httpClient.execute(httpget, context);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != HttpStatus.SC_OK) {
				logger.warn("warn! at " + url + ",return code:" + responseCode);
				EntityUtils.consume(response.getEntity());
				return ret;
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				byte[] temp = EntityUtils.toByteArray(entity);
				ret = JavaUtil.readBytes(temp, -1, true);
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("err at " + url + ",errmsg:" + e.getMessage());
			httpget.abort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("err at " + url + ",errmsg:" + e.getMessage());
			httpget.abort();
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

	public static String getPageContent(String url, HttpContext context) {

		String ret = "";
		// HttpContext context = new BasicHttpContext();
		HttpGet httpget = new HttpGet(url);
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
				logger
						.error("warn! at " + url + ",return code:"
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
			}

			long readtime = System.currentTimeMillis() - start;

		} catch (Exception e) {
			logger.error("err at " + url + ",errmsg:" + e.getMessage());

		} finally {
			httpget.abort();

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

	public static String getPageContent(String url) {

		String ret = "";
		HttpContext context = new BasicHttpContext();
		HttpGet httpget = new HttpGet(url);
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
//			if (head.getValue().toLowerCase().indexOf("text") == -1)
//				return ret;
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != HttpStatus.SC_OK) {
				logger
						.error("warn! at " + url + ",return code:"
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
			}

			long readtime = System.currentTimeMillis() - start;

		} catch (Exception e) {
			logger.error("err at " + url + ",errmsg:" + e.getMessage());
			httpget.abort();
		} finally {

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

	public HttpResponse getResonse(String url) throws ClientProtocolException,
			IOException {

		HttpContext context = new BasicHttpContext();
		HttpGet httpget = new HttpGet(url);

		HttpClient httpClient = HttpClientGenerator.getHttpClient();
		HttpResponse response = httpClient.execute(httpget, context);

		return response;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HttpHost proxy = new HttpHost("127.0.0.1", 443);
		String url = "http://www.yzzk.com/cfm/contentxml.cfm?issue=2014-22&id=1401334864351";
		HttpClientFetcher wd = new HttpClientFetcher();
		String info = "";
		long t = System.currentTimeMillis();
		try {

			info = wd.getPageContent(url, proxy);
//			System.out.println(info);
			// String pattern = "<a.*?href=\"(.*?)\"";
			// Matcher m = Pattern.compile(pattern).matcher(info);
			// while (m.find()) {
			// System.out.println(StringUtil.fixURLEncodeError(m.group(1)));
			// System.out.println(m.group(1));
			// }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("time consumed:" + (System.currentTimeMillis() -
		// t)
		// + " ms");
		//
		// System.out.println(info);

	}
}
