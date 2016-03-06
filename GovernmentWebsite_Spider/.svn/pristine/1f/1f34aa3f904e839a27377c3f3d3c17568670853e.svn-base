package com.meiah.test;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class HttpClientGenerator {
	private static Logger logger = Logger.getLogger(HttpClientGenerator.class);
	private static HttpClient httpClient = null;
	private static PoolingClientConnectionManager cm = null;
	static final int TIMEOUT = 20000;// 连接超时时间
	static final int SO_TIMEOUT = 10000;// 数据传输超时
	static final long CON_TIMEOUT = 60000;// 连接使用超时
	static Timer idleCheckTimer = null;

	public static void cleanConnections() {
		cm.closeExpiredConnections();
		cm.closeIdleConnections(60, TimeUnit.SECONDS);
	}

	public static void shutdown() {
		cm.shutdown();
		idleCheckTimer.cancel();
	}

	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
				.getSocketFactory()));
		cm = new PoolingClientConnectionManager(schemeRegistry);

		// Increase max total connection to 200
		cm.setMaxTotal(300);
		// Increase default max connection per route to 20
		cm.setDefaultMaxPerRoute(20);
		// cm.
		// Increase max connections for localhost:80 to 50
		// HttpHost localhost = new HttpHost("locahost", 80);
		// cm.setMaxPerRoute(new HttpRoute(localhost), 50);
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
		// params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
		// false);
		params.setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, CON_TIMEOUT);
		params.setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);
		params
				.setParameter(
						CoreProtocolPNames.USER_AGENT,
						"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.3; .NET CLR 2.0.50727; InfoPath.2; .NET4.0C; .NET4.0E)");
		DefaultHttpClient dh = new DefaultHttpClient(cm, params);

		dh.addRequestInterceptor(new RequestAcceptEncoding());
		dh.addResponseInterceptor(new ResponseContentEncoding());
		// dh.setCookieSpecs(new BestMatchSpec());
		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
			private Logger logger = Logger
					.getLogger(HttpRequestRetryHandler.class);

			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {

				if (executionCount >= 1) {
					logger.debug(exception.getMessage() + ",executionCount:"
							+ executionCount);
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
		idleCheckTimer = new Timer("IdleConnectionMonitorThread");
		idleCheckTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {

				cleanConnections();

			}
		}, new Date(System.currentTimeMillis()), 60 * 1000);
		// logger.info();
	}

	public static HttpClient getHttpClient() {
		if (httpClient != null)
			return httpClient;
		else {
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
					.getSocketFactory()));
			schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
					.getSocketFactory()));
			cm = new PoolingClientConnectionManager(schemeRegistry);

			// Increase max total connection to 200
			cm.setMaxTotal(300);
			// Increase default max connection per route to 20
			cm.setDefaultMaxPerRoute(5);
			// cm.
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
			params
					.setParameter(
							CoreProtocolPNames.USER_AGENT,
							"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.3; .NET CLR 2.0.50727; InfoPath.2; .NET4.0C; .NET4.0E)");
			DefaultHttpClient dh = new DefaultHttpClient(cm, params);

			dh.addRequestInterceptor(new RequestAcceptEncoding());
			dh.addResponseInterceptor(new ResponseContentEncoding());
			// dh.setCookieSpecs(new BestMatchSpec());
			HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
				private Logger logger = Logger
						.getLogger(HttpRequestRetryHandler.class);

				public boolean retryRequest(IOException exception,
						int executionCount, HttpContext context) {

					if (executionCount >= 1) {
						logger.debug(exception.getMessage()
								+ ",executionCount:" + executionCount);
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

			// logger.info();
			return httpClient;
		}
	}
}
