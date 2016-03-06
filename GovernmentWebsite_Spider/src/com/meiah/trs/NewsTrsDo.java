package com.meiah.trs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.util.StringUtil;

import com.heaton.bot.Attribute;
import com.heaton.bot.HTTP;
import com.heaton.bot.HTTPSocket;
import com.meiah.dao.TaskDao;
import com.meiah.po.News;
import com.meiah.po.Task;
import com.meiah.util.Config;
import com.meiah.util.JavaUtil;
import com.meiah.util.KeywordFilter;
import com.meiah.util.MD5Utils;
import com.meiah.util.MyDate;
import com.mytools.util.DateUtils;

/**
 * @author huhb 接收数据库新闻数据，生产索引数据文件
 */
public class NewsTrsDo extends Thread {
	private Logger logger = Logger.getLogger(NewsTrsDo.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD");
	private List<News> allnews;
	private KeywordFilter  keyFilter;
	// ##########################################
	// TRS入库时间保存
	public static int preAdddate = 0;
	public static int nowDate = 0;

	public static int WRITE_POSTSTRSFILE = 0;// 1 的时候马上写trs
	public static int WRITE_POSTSTRSFILE1 = 0;// 1 的时候马上写trs
	public static Map<String, String> ips = null;

	public static int iTrsNews = 0;// 内存中保存的trs格式数量，超过50个写
	public static int iTrsNews1 = 0;// 内存中保存的trs格式数量，超过50个写

	private StringBuffer trs_news = new StringBuffer("");
	private StringBuffer trs_news1 = new StringBuffer("");
	protected static String proxyUrl;
	protected static String proxyPort = "9048";
	protected static String proxyUser = "test";
	protected static String proxyPwd = "qwer1011";
	/**超时时间*/
	private final static int CONNECT_TIME_OUT = 5 * 60 * 1000;
	
	/**request 超时时间*/
	private final static int  REQUEST_TIME_OUT = 5 * 60 * 1000;
	
	/**链接超时时间*/
	private final static int SOCKET_TIME_OUT = 5 * 60 * 1000;
	
	/**
	 * 客户端类型
	 */
	private static  String USER_AGENT = "NTES Android";
	
	/**
	 * 客户端类型
	 * Web浏览器 针对UC浏览器验证
	 */
	private static  String USER_AGENT_WEB = "Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; HUAWEI P6 S-U06 Build/HuaweiP6S-U06) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
	public static String getUserAgent() {
		return USER_AGENT;
	}

	public static  void setUserAgent(String userAgent) {
		NewsTrsDo.USER_AGENT = userAgent;
	}
	public NewsTrsDo(List<News> allposts) {
		this.allnews = allposts;
		this.keyFilter = KeywordFilter.getInstance();
	}

	public void run() {
		try {
			doTrsFiles();
		} catch (Exception e) {
			logger.error("生成NEWS的TRS数据错误", e);
		}
	}

	public void doTrsFiles() {
		logger.debug("开始生成TRS文件，共 " + allnews.size() + " 条数据");
		for (int i = 0; i < allnews.size(); i++) {
			if (i == allnews.size() - 1) {
				WRITE_POSTSTRSFILE = 1;
				WRITE_POSTSTRSFILE1 = 1;
			}
//			 doTrs(allnews.get(i));
			try {
				doTrsYK(allnews.get(i));
			} catch (Exception e) {
				logger.error("加入单个trs信息失败", e);
			}

		}
	}

	private void doTrs(News n) {
		Map<String, Task> ts = TaskDao.getInstance().getAllTaskMap();
		String ss = "";
		Date nowDate = new Date();
		String getDate = DateUtils.getDate(nowDate, DateUtils.YYYYMMDD);
		String getTime = DateUtils.getDate(nowDate, DateUtils.YYYYMMDDHHMMSS);
		String addDate = DateUtils.getDate(nowDate, DateUtils.YYYYMMDD);
		String addTime = DateUtils.getDate(nowDate, DateUtils.YYYYMMDDHHMMSS);
		try {
			ss = "<REC>\r\n" 
					+ "<GUID>=" + MD5Utils.getMD5(n.getUrl().getBytes()) + "\r\n"
					+ "<SrcIP>=" + "" + "\r\n"
					+ "<SrcPort>=" + "" + "\r\n"
					+ "<DestIP>=" + "" + "\r\n"
					+ "<DestPort>=" + "" + "\r\n"
					+ "<IPName>=" + "" + "\r\n"
					+ "<IPSetPID>=" + "" + "\r\n"
					+ "<IPSetCID>=" + "" + "\r\n"
					+ "<IPAreaType>=" + n.getIpArea() + "\r\n"
					+ "<SiteName>=" + ts.get(n.getTaskid()).getTname() + "\r\n"
					+ "<SiteUrl>=" + "" + "\r\n"
					+ "<PageType>=" + n.getPage_type() + "\r\n"
					+ "<Title>=" + n.getTitle() + "\r\n"
					+ "<DataSource>=67" + "\r\n"
					+ "<PageUrl>=" + n.getUrl() + "\r\n"
					+ "<Content>=" + n.getContent() + "\r\n"
					+ "<Poster>=" + "" + "\r\n"
					+ "<MediaType>=" + "" + "\r\n"
					+ "<FileName>=" + "" + "\r\n"
					+ "<PublishDate>=" + sdf.format(n.getDateline()) + "\r\n"
					+ "<PublishTime>=" + getDate + "\r\n"
					+ "<AddDate>=" + getTime + "\r\n"
					+ "<AddTime>=" + addDate + "\r\n"
					+ "<GetTime>=" + addTime + "\r\n"
					+ "<RelID>=" + "" + "\r\n"
					+ "<IsTopic>=" + "" + "\r\n"//主题帖
					+ "<IsRead>=" + "" + "\r\n"
					+ "<ServerIndex>=" + "" + "\r\n"
					+ "<DBName>=" + "" + "\r\n"
					+ "<TopDomain>=" + n.getTopDomain() + "\r\n"
					+ "<SpotCode>=" + n.getSpot_code() + "\r\n"
					+ "<Conform>=" + "" + "\r\n"
					+ "<Referer>=" + "" + "\r\n"
					+ "<TextLength>=" + (n.getContent() == null ? 0 : n.getContent().length()) + "\r\n"
					+ "<SiteUrlCRC32>=" + "" + "\r\n"
					+ "<Extend1>=" + "" + "\r\n"
					+ "<Extend2>=" + "" + "\r\n"
					+ "<Extend3>=" + "" + "\r\n"
					+ "<Extend4>=" + "" + "\r\n"
					+ "<Extend5>=" + "" + "\r\n"
					+ "<Extend6>=" + "" + "\r\n"
					+ "<Extend7>=" + "" + "\r\n"
					+ "<Extend8>=" + n.getIslist() + "\r\n"
					+ "<Extend9>=" + "" + "\r\n"
					+ "<Extend10>=" + "" + "\r\n"
					+ "<Extend11>=" + "" + "\r\n"
					+ "<Extend12>=" + "" + "\r\n"
					+ "\r\n";
		} catch (Exception e) {
			logger.error("生成NEWS的TRS数据错误", e);
			return;
		}
		addTRSNews(Config.TRS_NEWS, ss);
	}

	/**
	 * 添加到NEWS的TRS库中，每天插入昨天的数据
	 * 
	 * @param table_name
	 * @param trsFormat
	 */

	public synchronized void addTRSNews(String table_name, String trsFormat) {
		if (preAdddate == 0) {
			preAdddate = Integer.valueOf(new MyDate().get_yyyymmdd());
		} else {
			nowDate = Integer.valueOf(new MyDate().get_yyyymmdd());

			// 如果该条数据是第二天的数据，则马上把内存中的数据写入TRS文件中入库。
			if (nowDate > preAdddate) {
				WRITE_POSTSTRSFILE = 1;
				preAdddate = nowDate;
			}
		}

		if (iTrsNews >= 50 || WRITE_POSTSTRSFILE == 1) {
			if (WRITE_POSTSTRSFILE == 1) {
				if (!table_name.equals("")) {
					iTrsNews++;
					trs_news.append(trsFormat).append("\r\n");
				}
			}
			try {
				iTrsNews = 0;
				String path = Config.getFileSavePath()
						+ "_trs/";
				File folder = new File(path);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				String ss = path + table_name + "." + preAdddate
						+ (new MyDate().get_yyyymmddhh24miss().substring(8))
						+ "." + (10000 + Math.round(Math.random() * 10000));
				// System.out.println(ss);
				File ff = new File(ss + ".trs0");
				ff.createNewFile();
				FileWriter fw = new FileWriter(ff);
				String trs_str = trs_news.toString();
				fw.write(trs_str);
				fw.flush();
				fw.close();
				fw = null;
				ff.renameTo(new File(ss + ".trs"));
				trs_news = new StringBuffer("");
				WRITE_POSTSTRSFILE = 0;
				preAdddate = 0;

			} catch (Exception e) {
				logger.error("生成TRS文件时错误 ", e);// @iilldd2005
			}
		}
		if (preAdddate == 0) {
			preAdddate = Integer.valueOf(new MyDate().get_yyyymmdd());
		}
		if (!table_name.equals("")) {
			iTrsNews++;
			trs_news.append(trsFormat).append("\r\n");
		}
		// logger.debug("内存中还有"+iTrsPosts+"条数据没有读出！");
	}

	/**
	 * 生成TRS文件
	 * 
	 * @param t
	 * @throws UnknownHostException
	 */
	private void doTrsYK(News ne) throws UnknownHostException {
		Map<String, Task> ts = TaskDao.getInstance().getAllTaskMap();

		String site = JavaUtil.matchWeak(ts.get(ne.getTaskid()).getUrl(), "http://([^/]*)")[1];
		String ipName = "未知";
		String IPAreaType = "";
		if (NewsTrsDo.ips == null)
			NewsTrsDo.ips = new HashMap<String, String>();
		if (NewsTrsDo.ips.containsKey(site)) {
			site = NewsTrsDo.ips.get(site);
		} else {
			InetAddress a = InetAddress.getByName(site);
			site = a.getHostAddress();
			NewsTrsDo.ips.put(site, a.getHostAddress());
		}

		//String _ipName = SOCKClient.getInstance().getIPName(site);
		//http://ip.qq.com/cgi-bin/searchip?searchip1=ip地址
		/**调用淘宝ip api来查询ip归属地**/
		ipName = getAddressByIP(site);
//		String[] _ss_ipName = ipName.split("`");
//		if (_ipName.length == 3) {
//			ipName = _ss_ipName[0];
//			IPAreaType = String.valueOf(getIPArea(_ss_ipName[0], Integer.valueOf(_ss_ipName[2]), _ss_ipName[1]));
//		}
		String ss = "";
		String SpotID = Config.getSpotID();

		String isTopic = "";
		switch (ne.getIslist()) {
		case 0: {
			isTopic = "1";
			break;
		}
		case 1: {
			isTopic = "2";
			break;
		}
		case 2: {
			isTopic = "3";
			break;
		}
		case 4: {
			isTopic = "0";
			break;
		}
		}

		CRC32 c = new CRC32();
		c.update(ts.get(ne.getTaskid()).getUrl().getBytes());
		long SiteUrlCRC32 = c.getValue();
		String siteName = ts.get(ne.getTaskid()).getTname();
		if (siteName == null || siteName.equals("")) {
			logger.error("空的网站名：" + ne.getTaskid());
		}
		try {
			ss = "<REC>\r\n" 
					+ "<GUID>=" + MD5Utils.getMD5(ne.getUrl().getBytes()) + "\r\n"
					+ "<SrcIP>=" + "" + "\r\n"
					+ "<SrcPort>=" + "" + "\r\n"
					+ "<DestIP>=" + site + "\r\n"
					+ "<DestPort>=" + "80" + "\r\n"
					+ "<IPName>=" + ipName + "\r\n"
					+ "<IPSetPID>=" + "" + "\r\n"
					+ "<IPSetCID>=" + "" + "\r\n"
					+ "<IPAreaType>=" + IPAreaType + "\r\n"
					+ "<SiteName>=" + siteName + "\r\n"
					+ "<SiteUrl>=" + ts.get(ne.getTaskid()).getUrl() + "\r\n"
					+ "<PageType>=" + "新闻" + "\r\n" 
					+ "<Title>=" + ne.getTitle() + "\r\n"
					+ "<DataSource>=" + "67"  + "\r\n"
					+ "<PageUrl>=" + ne.getUrl() + "\r\n"
					+ "<Content>=　" + ne.getContent() + "\r\n"
					+ "<Poster>=" + ne.getAuthor() + "\r\n"
					+ "<MediaType>=" + "" + "\r\n"
					+ "<FileName>=" + "" + "\r\n"
					+ "<PublishTime>=" + new SimpleDateFormat("yyyyMMddHHmmss").format(ne.getDateline()) + "\r\n"
					+ "<PublishDate>=" + new SimpleDateFormat("yyyyMMdd").format(ne.getDateline()) + "\r\n"
					+ "<AddDate>=" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "\r\n"
					+ "<AddTime>=" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "\r\n"
					+ "<GetTime>=" + new SimpleDateFormat("yyyyMMddHHmmss").format(ne.getSavetime()) + "\r\n"
					+ "<RelID>=" + "67*" + SpotID.substring(14, 16) + "*" + ne.getTaskid() + "*" + ne.getId() + "\r\n"
					+ "<IsTopic>=" + isTopic + "\r\n"
					+ "<IsRead>=" + "0" + "\r\n"
					+ "<ServerIndex>=" + "0" + "\r\n"
					+ "<DBName>=" + "YK_" + new MyDate().get_yyyymmdd() + "\r\n"
					+ "<TopDomain>=" + getHost1(JavaUtil.matchWeak(ts.get(ne.getTaskid()).getUrl(), "http://([^/]*)")[1]) + "\r\n"
					+ "<SpotCode>=" + SpotID + "\r\n" 
					+ "<Conform>=" + ne.getConform() + "\r\n"
					+ "<Referer>=" + ne.getReferUrl() + "\r\n"
					+ "<TextLength>=" + ne.getContent().length() + "\r\n"
					+ "<SiteUrlCRC32>=" + SiteUrlCRC32 + "\r\n"
					+ "<Extend1>=" + "" + "\r\n"
					+ "<Extend2>=" + "" + "\r\n"
					+ "<Extend3>=" + "" + "\r\n"
					+ "<Extend4>=" + "" + "\r\n"
					+ "<Extend5>=" + "" + "\r\n"
					+ "<Extend6>=" + "" + "\r\n"
					+ "<Extend7>=" + "" + "\r\n"
					+ "<Extend8>=" + ne.getIsHomePageNews() + "\r\n"
					+ "<Extend9>=" + ne.getSourceSite() + "\r\n"
					+ "<Extend10>=" + "" + "\r\n"
					+ "<Extend11>=" + "" + "\r\n"
					+ "<Extend12>=" + "" + "\r\n"
					+ "\r\n";

		} catch (Exception e) {
			logger.error("生成News的TRS数据错误", e);
			return;
		}
		addTRSNewsYK(Config.TRS_YK, ss);
	}
	/**
	 * 根据ip地址获取地域信息
	 * @return
	 */
	public static String getAddressByIP(String IP) {
		String resout = "";
		try {
			if(StringUtils.isNotBlank(IP)) {
				String str = getIpName("http://ip.taobao.com/service/getIpInfo.php?ip=" + IP);
//			System.out.println(str + "jsonjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
				if(!StringUtils.isEmpty(str)) {
					JSONObject obj = JSONObject.fromObject(str);
					JSONObject obj2 = (JSONObject) obj.get("data");
					String code = String.valueOf(obj.get("code"));
					if (code.equals("0")) {
						resout =  obj2.get("region") + "" + obj2.get("city") + "" + obj2.get("county");
					} else {
						resout = "IP地址有误";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resout = "获取IP地址异常：" + e.getMessage();
		}
		if(resout.contains("获取IP地址异常：") || resout.contains("ipNameMapping1")) 
			resout = "";
		return resout;
//		/*String address = "";
//		try {
//			URL url = new URL("http://ip.qq.com/cgi-bin/searchip?searchip1=" + strIP);
//			URLConnection conn = url.openConnection();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					conn.getInputStream(), "GBK"));
//			String line = null;
//			StringBuffer result = new StringBuffer();
//			while ((line = reader.readLine()) != null) {
//				result.append(line);
//			}
//			reader.close();
//			strIP = result.substring(result.indexOf("该IP所在地为："));
//			strIP = strIP.substring(strIP.indexOf("：") + 1);
//			String province = strIP.substring(6, strIP.indexOf("省"));
//			String city = strIP.substring(strIP.indexOf("省") + 1, strIP.indexOf("市"));
//			address = province + city;
//			boolean boo = keyFilter.isContentKeyWords(address);
//			if(boo) {
//				address = "未知";
//			}
//		} catch (Exception e) {
//			return "读取失败";
//		}*/
////		return address;
	}
	/**
	 * 利用淘宝接口获得ip所在地的json串
	 * @param urlStr
	 * @return
	 * @throws IOException 
	 */
	public static String getIpName(String urlStr) {
		boolean useProxy = true;
		String ipName = "";
		int count = 0;
		boolean status = false;
		while(true){
			if (count < 10) {
				try {
					ipName = getJsonContent(urlStr,useProxy);
				} catch (Exception e) {
					status = true;
					e.printStackTrace();
					try {
						Thread.sleep(1000 * 60);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					status = false;
					count++;
				}
				if(StringUtils.isEmpty(ipName)) {
					continue;
				}
				if (!status) {
					break;
				}
			}else {
				break;
			}
		}
		return ipName;
	}
	/**
	 * 利用淘宝接口获得ip所在地的json串
	 * @param urlStr
	 * @return
	 * @throws ClientProtocolException 
	 * @throws IOException 
	 */
	public static String getJsonContent(String url , boolean useProxy) throws IOException {
		
		String response = "";
		HTTP _http = new HTTPSocket();
		_http.setUseCookies(true, true);
		_http.setTimeout(REQUEST_TIME_OUT);
		_http.getClientHeaders().add(new Attribute("Accept-Encoding", "gzip"));
		_http.getClientHeaders().add(new Attribute("Accept-Language", "*"));
		_http.getClientHeaders().add(new Attribute("Connection", "close"));
		_http.setAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.3; .NET CLR 2.0.50727)");
		_http.SetAutoRedirect(false);
		if(useProxy) {
			int min = 6;
			int max = 253;
			String proxyUrls = "125.208.18.";
			Random random = new Random();
			int ipValue = random.nextInt(max)%(max-min+1) + min;
			proxyUrl = proxyUrls + ipValue;
			String proxyStr = "http://" + proxyUrl + ":" + proxyPort + "@" + proxyUser + ":" + proxyPwd + "";
//			System.out.println("ip地址是" + proxyUrl + "----------------------------");
			_http.proxyStr = proxyStr;
			_http.initProxy();
		}
		try {
			_http.send(url, "UTF-8");
		} catch (Exception e) {
//			System.out.println("发送请求:"+url+"\t过程中发生异常:\n"+e);
		} 
		byte[] _buff1= (_http.getBodyBytes());
		if(_buff1!=null){
			response = JavaUtil.readBytes(_buff1, 8,true);
		}else{
			return "";
		}
	
		/*final String  url = urlStr;
		String response = "";
		CloseableHttpClient httpclient = HttpClients.createMinimal();
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(CONNECT_TIME_OUT)
				.setConnectionRequestTimeout(REQUEST_TIME_OUT)
				.setSocketTimeout(SOCKET_TIME_OUT)  
				.build();
       
            HttpGet httpGet = new HttpGet(url); 
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
            httpGet.setConfig(requestConfig);
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                public String handleResponse(
                        final HttpResponse response) throws  IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                    	return "";
                    }
                }
            };
            response =  httpclient.execute(httpGet, responseHandler);
            httpclient.close();*/
        return response;
	}

	/*private static String ConvertStream2Json(InputStream inputStream) {
		String jsonStr = "";
		// ByteArrayOutputStream相当于内存输出流
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		// 将输入流转移到内存输出流中
		try {
			while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, len);
			}
			// 将内存流转换为字符串
			jsonStr = new String(out.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return jsonStr;
	}*/
	/**
	 * 添加到的TRS库中，每天插入昨天的数据
	 * 
	 * @param table_name
	 * @param trsFormat
	 */

	public synchronized void addTRSNewsYK(String table_name, String trsFormat) {
		if (preAdddate == 0) {
			preAdddate = Integer.valueOf(new MyDate().get_yyyymmdd());
		} else {
			nowDate = Integer.valueOf(new MyDate().get_yyyymmdd());

			// 如果该条数据是第二天的数据，则马上把内存中的数据写入TRS文件中入库。
			if (nowDate > preAdddate) {
				WRITE_POSTSTRSFILE1 = 1;
				preAdddate = nowDate;
			}
		}

		if (iTrsNews1 >= 50 || WRITE_POSTSTRSFILE1 == 1) {
			if (WRITE_POSTSTRSFILE1 == 1) {
				if (!table_name.equals("")) {
					iTrsNews1++;
					trs_news1.append(trsFormat).append("\r\n");
				}
			}
			try {
				iTrsNews1 = 0;
				String path = ClassLoader.getSystemResource("").getPath().replaceAll("file:/", "") + "trsYK/";
				File folder = new File(path);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				String ss = path + table_name + "_67_" + preAdddate
						+ (new MyDate().get_yyyymmddhh24miss().substring(8))
						+ "_" + new java.util.Date().getTime() + "_"
						+ (100000 + Math.round(Math.random() * 100000));
				File ff = new File(ss + ".index0");
				ff.createNewFile();
				String trs_str = trs_news1.toString();

				FileWriter fw = new FileWriter(ff);
				fw.write(trs_str);
				fw.flush();
				fw.close();
				fw = null;

				ff.renameTo(new File(ss + ".index"));
				trs_news1 = new StringBuffer("");
				WRITE_POSTSTRSFILE1 = 0;
				preAdddate = 0;

			} catch (Exception e) {
				logger.error("生成TRS文件时错误 ", e);// @iilldd2005
			}
		}
		if (preAdddate == 0) {
			preAdddate = Integer.valueOf(new MyDate().get_yyyymmdd());
		}
		if (!table_name.equals("")) {
			iTrsNews1++;
			trs_news1.append(trsFormat).append("\r\n");
		}
		// logger.debug("内存中还有"+iTrsPosts+"条数据没有读出！");
	}

	// 获取域名的函数
	// 包含全部的cn域名后缀
	static String[] xCN = { ".com.cn", ".net.cn", ".gov.cn", ".edu.cn",
			".org.cn", ".mil.cn", ".ac.cn", ".bj.cn", ".sh.cn", ".tj.cn",
			".cq.cn", ".he.cn", ".sx.cn", ".nm.cn", ".ln.cn", ".jl.cn",
			".hl.cn", ".js.cn", ".zj.cn", ".ah.cn", ".fj.cn", ".jx.cn",
			".sd.cn", ".ha.cn", ".hb.cn", ".hn.cn", ".gd.cn", ".gx.cn",
			".hi.cn", ".sc.cn", ".gz.cn", ".yn.cn", ".xz.cn", ".sn.cn",
			".gs.cn", ".qh.cn", ".nx.cn", ".xj.cn", ".tw.cn", ".hk.cn",
			".mo.cn" };

	// 传入任意网址，获取网站域名
	private String getHost2(String host) {
		return JavaUtil.match(host, "http://([^/]*+\\.?)+?/?")[1];
	}

	// 顶级域名
	private String getHost1(String host) {
		host = host.trim().toLowerCase();// 格式化
		String domain1 = "";
		if (host.endsWith(".cn")) {
			// 判断cn分类域名以及区域域名
			for (int i = 0; i < xCN.length; i++) {
				if (host.endsWith(xCN[i])) {
					host = host.substring(0, host.length() - xCN[i].length());
					String[] _s = host.split("\\.");
					if (_s.length > 0) {
						domain1 = _s[_s.length - 1] + xCN[i];
					}
					return domain1;
				}
			}
			// else if(host.endsWith(".cn")){
			host = host.substring(0, host.length() - 3);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".cn";
			// }
		} else if (host.endsWith(".com")) {
			host = host.substring(0, host.length() - 4);
			String[] _s = host.split("\\.");
			domain1 = _s[_s.length - 1] + ".com";
		}

		else if (host.endsWith(".net")) {
			host = host.substring(0, host.length() - 4);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".net";
		}

		else if (host.endsWith(".org")) {
			host = host.substring(0, host.length() - 4);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".org";
		}

		else if (host.endsWith(".gov")) {
			host = host.substring(0, host.length() - 4);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".gov";
		}

		else if (host.endsWith(".edu")) {
			host = host.substring(0, host.length() - 4);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".edu";
		}

		else if (host.endsWith(".biz")) {
			host = host.substring(0, host.length() - 4);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".biz";
		}

		else if (host.endsWith(".tv")) {
			host = host.substring(0, host.length() - 3);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".tv";
		}

		else if (host.endsWith(".cc")) {
			host = host.substring(0, host.length() - 3);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".cc";
		}

		else if (host.endsWith(".be")) {
			host = host.substring(0, host.length() - 3);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".be";
		}

		else if (host.endsWith(".info")) {
			host = host.substring(0, host.length() - 5);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".info";
		}

		else if (host.endsWith(".name")) {
			host = host.substring(0, host.length() - 5);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".name";
		}

		else if (host.endsWith(".co.uk")) {
			host = host.substring(0, host.length() - 6);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".co.uk";
		}

		else if (host.endsWith(".me.uk")) {
			host = host.substring(0, host.length() - 6);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".me.uk";
		}

		else if (host.endsWith(".org.uk")) {
			host = host.substring(0, host.length() - 7);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".org.uk";
		}

		else if (host.endsWith(".ltd.uk")) {
			host = host.substring(0, host.length() - 7);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".ltd.uk";
		}

		else if (host.endsWith(".plc.uk")) {
			host = host.substring(0, host.length() - 7);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".plc.uk";
		}
		return domain1;
	}

	private int getIPArea(String ip, int cid, String pid) {
		int IPAreaType = 0;
		if (cid == Config.getCid()) {
			IPAreaType = 1;
		} else if (pid.equals("000000"))// 0境内[异地]，1境内[本地]，2香港，3澳门，4台湾，5境外
		{
			switch (cid) {
			case 852: {
				IPAreaType = 2;// 香港
				break;
			}
			case 853: {
				IPAreaType = 3;// 澳门
				break;
			}
			case 886: {
				IPAreaType = 4;// 台湾
				break;
			}
			default: {
				IPAreaType = 5;// 境外
				break;
			}
			}
		}

		return IPAreaType;
	}

	private String getCrawlTime(Date date) {

		String yyyymmddhh24miss = "";
		Calendar Date_time = Calendar.getInstance();
		Date_time.setTime(date);
		String Tyear = String.valueOf(Date_time.get(Calendar.YEAR));
		String Tmonth = String.valueOf(Date_time.get(Calendar.MONTH) + 1);
		if (Tmonth.length() < 2)
			Tmonth = "0" + Tmonth;
		String Tday = String.valueOf(Date_time.get(Calendar.DAY_OF_MONTH));
		if (Tday.length() < 2)
			Tday = "0" + Tday;
		String Thour = String.valueOf(Date_time.get(Calendar.HOUR_OF_DAY));
		if (Thour.length() < 2)
			Thour = "0" + Thour;
		String Tminute = String.valueOf(Date_time.get(Calendar.MINUTE));
		if (Tminute.length() < 2)
			Tminute = "0" + Tminute;
		String Tsecond = String.valueOf(Date_time.get(Calendar.SECOND));
		if (Tsecond.length() < 2)
			Tsecond = "0" + Tsecond;
		yyyymmddhh24miss = Tyear + Tmonth + Tday + Thour + Tminute + Tsecond;
		return yyyymmddhh24miss;
	}

	 public static void main(String[] args) {
//	 Date d = new Date();
//	 System.out.println(getCrawlTime(d));
		 
		 for (int i = 0, count = 10; i < count; i++) {
			 String urlStr = "61.49.18.66";
//				System.out.println(i + " == " +getAddressByIP(urlStr));
		 }
		 
	 }
}
