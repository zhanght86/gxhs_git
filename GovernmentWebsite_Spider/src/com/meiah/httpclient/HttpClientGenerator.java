package com.meiah.httpclient;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import com.meiah.dao.ScanDao;

public class HttpClientGenerator {
	private static HttpClient httpClient = null;
	static final int TIMEOUT = 20000;// 连接超时时间
	static final int SO_TIMEOUT = 60000;// 数据传输超时
	static final long CON_TIMEOUT = 60000;// 连接使用超时

	public static HttpClient getHttpClient() {
		if (httpClient != null)
			return httpClient;
		else {
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
					.getSocketFactory()));
			schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
					.getSocketFactory()));
			PoolingClientConnectionManager cm = new PoolingClientConnectionManager(
					schemeRegistry);

			// Increase max total connection to 200
			cm.setMaxTotal(30);
			// Increase default max connection per route to 20
			cm.setDefaultMaxPerRoute(5);
			// Increase max connections for localhost:80 to 50
			// HttpHost localhost = new HttpHost("locahost", 80);
			// cm.setMaxPerRoute(new HttpRoute(localhost), 50);
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					TIMEOUT);
			params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
			// params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
			// false);
			params.setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, CON_TIMEOUT);
			params.setParameter(ClientPNames.COOKIE_POLICY,
					CookiePolicy.BROWSER_COMPATIBILITY);
			DefaultHttpClient dh = new DefaultHttpClient(cm, params);

			// dh.setCookieSpecs(new BestMatchSpec());
			HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
				private Logger logger = Logger.getLogger(ScanDao.class);

				public boolean retryRequest(IOException exception,
						int executionCount, HttpContext context) {
					logger.warn(exception.getMessage() + ",executionCount"
							+ executionCount);
					if (executionCount >= 4) {
						// Do not retry if over max retry count
						return false;
					}
					if (exception instanceof InterruptedIOException) {
						// Timeout
						if (exception instanceof SocketTimeoutException)
							return true;
						else
							return false;
					}
					if (exception instanceof UnknownHostException) {
						// Unknown host
						return true;
					}
					if (exception instanceof ConnectException) {
						// Connection refused
						return false;
					}
					if (exception instanceof SSLException) {
						// SSL handshake exception
						return false;
					}
					HttpRequest request = (HttpRequest) context
							.getAttribute(ExecutionContext.HTTP_REQUEST);
					boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
					if (idempotent) {
						// Retry if the request is considered idempotent
						return true;
					}
					return false;
				}
			};
			dh.setHttpRequestRetryHandler(myRetryHandler);

			httpClient = dh;
			return httpClient;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
