package com.meiah.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.heaton.bot.Attribute;
import com.heaton.bot.AttributeList;
import com.heaton.bot.CookieParse;
import com.heaton.bot.HTTP;
import com.heaton.bot.HTTPSocket;

/**
 * 基本工具类
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-08-06
 */
public class JavaUtil {
	private static Logger logger = Logger.getLogger(JavaUtil.class);

	private static String filePath = "/doc/";

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

	public static int getMatchChineseCharCount(String str) {
		int count = 0;
		Matcher m = Pattern.compile("[\u4e00-\u9fa5]").matcher(str);

		while (m.find()) {
			// System.out.println(i);
			count++;
		}
		return count;
	}

	public static String encodeURL(String url, String encode) {
		String ret = url;
		String invalidURLchars = "[^abcdefghijklmnopqrstuvwxyz0123456789%\\-\\._~:/\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=]";
		// if (JavaUtil.isAllMatch(ret, invalidURLchars)) {
		Pattern p = Pattern.compile(invalidURLchars);
		Matcher m = p.matcher(ret);
		while (m.find()) {
			String replacechar = m.group();
			try {
				ret = ret.replace(replacechar, URLEncoder.encode(replacechar,
						encode));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static String encodeURL(String url) {
		return encodeURL(url, "utf-8");
	}

	public static String decodeUnicode(String source) {

		String str = source;
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}

		try {
			str = URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return str;
	}

	public static String getHost1(String host) {
		host = host.trim().toLowerCase();// 格式化
		host = getHost2(host);// 先获取二级域名
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
		} else if (host.endsWith(".com.tw")) {
			host = host.substring(0, host.length() - 7);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".com.tw";
		} else if (host.endsWith(".ca")) {
			host = host.substring(0, host.length() - 3);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".ca";
		} else if (host.endsWith(".com.my")) {
			host = host.substring(0, host.length() - 7);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".com.my";
		} else if (host.endsWith(".my")) {
			host = host.substring(0, host.length() - 3);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".my";
		} else if (host.endsWith(".com.au")) {
			host = host.substring(0, host.length() - 7);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".com.au";
		} else if (host.endsWith(".au")) {
			host = host.substring(0, host.length() - 3);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".au";
		} else if (host.endsWith(".com.bn")) {
			host = host.substring(0, host.length() - 7);
			String[] _s = host.split("\\.");
			if (_s.length > 0)
				domain1 = _s[_s.length - 1] + ".com.bn";
		} else if (host.endsWith(".hk")) {
			host = host.substring(0, host.length() - 3);
			String[] _s = host.split("\\.");
			if(_s.length > 0)
				domain1 = _s[_s.length - 1] + ".hk";
		}else if (host.endsWith(".net:8081")) {
			host = host.substring(0, host.length() - 9);
			String[] _s = host.split("\\.");
			if(_s.length > 0)
				domain1 = _s[_s.length - 1] + ".net:8081";
		}
		return domain1;
	}

	// 获取二级域名?
	public static String getHost2(String host) {
		if (host.startsWith("http://"))
			host = host.substring(7);
		else if (host.startsWith("https://"))
			host = host.substring(8);
		int n = host.indexOf("/");
		if (n != -1)
			host = host.substring(0, n);

		return host;
	}

	// 判断一级域名或二级域名
	public static int getDomianType(String host) {
		int DomianType = 0;
		host = getHost2(host);
		String host1 = getHost1(host);

		if (host.equals(host1))
			DomianType = 1;
		else {
			if (host.indexOf(host1) > 1)
				DomianType = 2;
		}
		return DomianType;
	}

	/**
	 * 数据流直接写入文件
	 * 
	 * @param is
	 * @param fileName
	 */
	public static void writeFile(InputStream is, String fileName) {
		try {
			URL path = JavaUtil.class.getResource("/");
			String dir = URLDecoder.decode(path.getPath(), "utf-8");
			FileOutputStream fs = new FileOutputStream(dir + filePath
					+ fileName);
			byte[] buf = new byte[1024];
			int len = is.read(buf);
			while (len != -1) {
				fs.write(buf, 0, len);
				len = is.read(buf);
			}
			fs.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
	}

	/**
	 * 文字写入文件
	 * 
	 * @param src
	 * @param fileName
	 */
	public static void writeFile(String src, String fileName) {
		try {
			URL path = JavaUtil.class.getResource("/");
			String dir = URLDecoder.decode(path.getPath(), "utf-8");
			FileOutputStream fs = new FileOutputStream(dir + filePath
					+ fileName);
			fs.write(src.getBytes("iso-8859-1"));// 如果不指定编码，在中英文平台上运行时可能会出现意想不到的结果
			fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件写入文件并制定编码方式
	 * 
	 * @param src
	 * @param charset
	 * @param fileName
	 */
	public static void writeFile(String src, String charset, String fileName) {
		try {
			URL path = JavaUtil.class.getResource("/");
			String dir = URLDecoder.decode(path.getPath(), "utf-8");
			FileOutputStream fs = new FileOutputStream(dir + filePath
					+ fileName);
			fs.write(src.getBytes(charset));
			fs.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
	}

	/**
	 * 文字写入文件
	 * 
	 * @param src
	 * @param fileName
	 */
	public static void writeFileAppend(String src, String fileName) {
		try {
			URL path = JavaUtil.class.getResource("/");
			String dir = URLDecoder.decode(path.getPath(), "utf-8");
			FileOutputStream fs = new FileOutputStream(dir + filePath
					+ fileName, true);
			fs.write(src.getBytes("iso-8859-1"));// 如果不指定编码，在中英文平台上运行时可能会出现意想不到的结果
			fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readFile(String fileName) {
		URL path = JavaUtil.class.getResource("/");
		FileInputStream fs;
		String content = "";
		try {
			String dir = URLDecoder.decode(path.getPath(), "utf-8");
			fs = new FileInputStream(dir + filePath + fileName);
			byte data[] = new byte[1024];
			int len = fs.read(data);
			while (len != -1) {
				content = content + new String(data, 0, len, "iso-8859-1");
				len = fs.read(data);
			}
			fs.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
		return content;
	}

	/**
	 * 以指定编码方式读取文件
	 * 
	 * @param fileName
	 * @param charset
	 * @return
	 */
	public static String readFile(String fileName, String charset) {
		URL path = JavaUtil.class.getResource("/");
		FileInputStream fs;
		String content = "";
		try {
			String dir = URLDecoder.decode(path.getPath(), "utf-8");
			fs = new FileInputStream(dir + filePath + fileName);
			byte data[] = new byte[1024];
			int len = fs.read(data);
			while (len != -1) {
				content = content + new String(data, 0, len, charset);
				len = fs.read(data);
			}
			fs.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
		return content;
	}

	/**
	 * 读取流内容
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String readStream(InputStream is) throws IOException {
		String cs = null;
		try {
			ByteArrayOutputStream buffer = null;
			BufferedInputStream in = new BufferedInputStream(is);
			buffer = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int len = -1;
			// 从socket连接中获取输出流，主要为请求的响应报头和HTML编码

			while ((len = in.read(buff)) != -1) {
				buffer.write(buff, 0, len);
			}// 由于使用BufferOutputStream会出现一个连接被分割在两行的情况，因此只能利用字节流将所有源代码取得，而后换成String

			logger.debug("get stream over");
			HTMLDecoder htmd = new HTMLDecoder();
			SinoDetect sd = new SinoDetect();

			if (buffer != null) {

				try {
					int i = sd.detectEncoding(buffer.toByteArray());
					cs = buffer.toString(Encoding.htmlname[i]);
				} catch (RuntimeException e) {
					cs = buffer.toString("GBK");
				}
				// try{
				// cs=buffer.toString(Encoding.htmlname[i]);
				// }catch(Exception e){
				// cs=buffer.toString("GBK");
				// }
				cs = cs.replace("&nbsp;", "");
				cs = htmd.ASCIIToGB(cs);
			}
			logger.debug("analyse stream over");

			is.close();
		} catch (IOException e) {
			throw e;
		}
		return cs;
	}

	/**
	 * 读取字节内容
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String readBytes(byte[] buff, int encode,
			boolean decodeEntityIf) throws IOException {
		String cs = null;
		try {

			HTMLDecoder htmd = new HTMLDecoder();
			SinoDetect sd = new SinoDetect();

			if (buff != null) {

				if (encode < 0) {
					encode = sd.detectEncoding(buff);
				}
				// System.out.println(new String(buff, "gbk"));
				switch (encode) {
				case 0: {
					cs = new String(buff, "GBK");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 1: {
					cs = new String(buff, "GBK");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 2: {
					cs = new String(buff, "GB18030");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 3: {
					cs = new String(buff, "ASCII");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 4: {
					cs = new String(buff, "BIG5");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 5: {
					cs = new String(buff, "EUC-TW");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 6: {
					cs = new String(buff, "GB2312");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 7: {
					cs = new String(buff, "UTF8");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 8: {
					cs = new String(buff, "UTF8");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 9: {
					cs = new String(buff, "UNICODE");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 10: {
					cs = new String(buff, "UNICODE");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 11: {
					cs = new String(buff, "UNICODE");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 12: {
					cs = new String(buff, "ISO2022CN");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 13: {
					cs = new String(buff, "ISO2022CN_CNS");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 14: {
					cs = new String(buff, "ISO2022CN_GB");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 15: {
					cs = new String(buff, "EUC_KR");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 16: {
					cs = new String(buff, "MS949");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 17: {
					cs = new String(buff, "ISO2022KR");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 18: {
					cs = new String(buff, "Johab");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 19: {
					cs = new String(buff, "SJIS");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 20: {
					cs = new String(buff, "EUC_JP");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 21: {
					cs = new String(buff, "ISO2022JP");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 22: {
					cs = new String(buff, "ASCII");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 23: {
					cs = new String(buff, "ISO8859_1");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					// System.out.println(cs);
					break;
				}
				default: {
					cs = new String(buff, "GBK");
					cs = cs.replace("&nbsp;", " ");
					if (decodeEntityIf)
						cs = htmd.ASCIIToGB(cs);
					break;
				}
				}

			}

		} catch (IOException e) {
			throw e;
		}
		return cs;
	}

	/**
	 * 读取字节内容
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String readBytes(byte[] buff, int encode) throws IOException {
		String cs = null;
		try {

			HTMLDecoder htmd = new HTMLDecoder();
			SinoDetect sd = new SinoDetect();

			if (buff != null) {

				if (encode < 0) {
					encode = sd.detectEncoding(buff);
				}

				switch (encode) {
				case 1: {
					cs = new String(buff, "GBK");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 2: {
					cs = new String(buff, "GB18030");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 3: {
					cs = new String(buff, "ASCII");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 4: {
					cs = new String(buff, "BIG5");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 5: {
					cs = new String(buff, "EUC-TW");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 6: {
					cs = new String(buff, "UTF8");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 7: {
					cs = new String(buff, "UTF8");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 8: {
					cs = new String(buff, "UTF8");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 9: {
					cs = new String(buff, "UNICODE");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 10: {
					cs = new String(buff, "UNICODE");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 11: {
					cs = new String(buff, "UNICODE");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 12: {
					cs = new String(buff, "ISO2022CN");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 13: {
					cs = new String(buff, "ISO2022CN_CNS");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 14: {
					cs = new String(buff, "ISO2022CN_GB");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 15: {
					cs = new String(buff, "EUC_KR");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 16: {
					cs = new String(buff, "MS949");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 17: {
					cs = new String(buff, "ISO2022KR");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 18: {
					cs = new String(buff, "Johab");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 19: {
					cs = new String(buff, "SJIS");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 20: {
					cs = new String(buff, "EUC_JP");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 21: {
					cs = new String(buff, "ISO2022JP");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				case 22: {
					cs = new String(buff, "ASCII");
					cs = cs.replace("&nbsp;", " ");
					cs = htmd.ASCIIToGB(cs);
					break;
				}
				default: {
					cs = new String(buff, "gbk");
					cs = cs.replace("&nbsp;", " ");

					cs = htmd.ASCIIToGB(cs);
					// System.out.println(cs);
					break;
				}
				}

			}

		} catch (IOException e) {
			throw e;
		}
		return cs;
	}

	/**
	 * 读取流内容，指定编码方式
	 * 
	 * @param is
	 * @param charset
	 * @return
	 */
	public static String readStream(InputStream is, String charset) {
		StringBuilder sb = new StringBuilder();
		// String content="";
		try {
			byte data[] = new byte[1024];
			for (int n; (n = is.read(data)) != -1;) {
				sb.append(new String(data, 0, n, charset));
			}

			is.close();
		} catch (IOException e) {

		}
		return sb.toString();
		// return content;
	}

	/**
	 * 正则匹配
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static String[] matchD0TALL(String s, String pattern) {
		Matcher m = Pattern.compile(pattern, Pattern.DOTALL).matcher(s);

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

	/**
	 * 正则匹配
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static String[] match(String s, String pattern) {
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

	/**
	 * 正则匹配
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static List<String[]> matchAll(String s, String pattern) {
		Matcher m = Pattern.compile(pattern).matcher(s);
		List<String[]> result = new ArrayList<String[]>();

		while (m.find()) {
			int n = m.groupCount();
			String[] ss = new String[n + 1];
			for (int i = 0; i <= n; i++) {
				ss[i] = m.group(i);
				// System.out.println(ss[i]);
			}
			result.add(ss);
		}
		return result;
	}

	/**
	 * 正则匹配，指定开始位置
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static String[] firstMatch(String s, String pattern, int startIndex) {
		Matcher m = Pattern.compile(pattern).matcher(s);

		if (m.find(startIndex)) {
			int n = m.groupCount();
			String[] ss = new String[n + 1];
			for (int i = 0; i <= n; i++) {
				ss[i] = m.group(i);
			}
			return ss;
		}
		return null;
	}

	/**
	 * 正则匹配
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static boolean isMatch(String s, String pattern) {
		return s.matches(pattern);
	}

	public static boolean isAllMatch(String s, String pattern) {
		Matcher m = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(
				s);

		while (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 正则匹配，忽略大小写
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static String[] matchWeak(String s, String pattern) {
		Matcher m = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(
				s);

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

	public static String unescape(String s) {
		s = s.replaceAll("<pre>[\\s]*", "");
		s = s.replaceAll("</pre>.*$", "\n");
		// cdmackie: sometimes we get only "</"
		s = s.replaceAll("</$", "\n");

		// Clean up the end of line, and replace HTML tags
		s = s.replaceAll("&#13;&#10; &#13;&#10;", "\n");
		s = s.replaceAll("&#9;", "\t");
		s = s.replaceAll("&#09;", "\t");
		s = s.replaceAll("&#10;", "\n");
		s = s.replaceAll("&#13;", "");
		// s = s.replaceAll("&#27;", "\27");
		s = s.replaceAll("&#32;", " ");
		s = s.replaceAll("&#33;", "!");
		s = s.replaceAll("&#35;", "#");
		s = s.replaceAll("&#36;", "\\$");
		// cdmackie: this should be escaped
		s = s.replaceAll("&#37;", "\\%");
		s = s.replaceAll("&#38;", "&");
		s = s.replaceAll("&#39;", "'");
		s = s.replaceAll("&#40;", "(");
		s = s.replaceAll("&#41;", ")");
		s = s.replaceAll("&#42;", "*");
		s = s.replaceAll("&#43;", "+");
		s = s.replaceAll("&#44;", ",");
		s = s.replaceAll("&#45;", "-");
		s = s.replaceAll("&#46;", ".");
		s = s.replaceAll("&#47;", "/");
		s = s.replaceAll("&#58;", ":");
		s = s.replaceAll("&#59;", ";");
		s = s.replaceAll("&#60;", "<");

		s = s.replaceAll("&#61;", "=");
		s = s.replaceAll("&#62;", ">");
		s = s.replaceAll("&#63;", "?");
		s = s.replaceAll("&#64;", "@");
		s = s.replaceAll("&#91;", "[");
		s = s.replaceAll("&#92;", "\\\\");// ////////
		s = s.replaceAll("&#93;", "]");
		s = s.replaceAll("&#94;", "^");
		s = s.replaceAll("&#95;", "_");
		s = s.replaceAll("&#96;", "`");
		s = s.replaceAll("&#123;", "{");
		s = s.replaceAll("&#124;", "|");
		s = s.replaceAll("&#125;", "}");
		s = s.replaceAll("&#126;", "~");
		// s = s.replaceAll("&#199;", "?");

		s = s.replaceAll("\r", "");
		s = s.replaceAll("\n", "\r\n");
		s = s.replaceAll("&#34;", "\"");

		// body = string.gsub(body, "<!%-%-%$%$imageserver%-%->",
		// internalState.strImgServer)

		s = s.replaceAll("\r\n\\.", "\r\n\\.\\.");
		// s = s.replaceAll("&#(\\d*);","");
		Pattern p = null;
		Matcher m = null;
		p = Pattern.compile("&#(\\d*);");
		m = p.matcher(s);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, numToString(m.group(1)));
		}
		m.appendTail(sb);
		String x = sb.toString().replaceAll("&#[^;]*;", "");
		return x;
	}

	public static String numToString(String num) {
		String result = "";
		int n = Integer.parseInt(num);
		if (n > 255 && n < 19968)
			result = new String("&#" + n + ";");
		else {
			result = new String("" + (char) n);
		}
		return result;
	}

	/**
	 * 检查给定参数是否全部不为空，并关系
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNullOrEmpty(String... strs) {
		for (int i = 0; i < strs.length; i++) {
			if (strs[i] == null || strs[i].trim().length() == 0)
				return true;
		}
		return false;
	}

	public static String getThreadsPartitonID(String taskid) {
		return String.valueOf(Integer.valueOf(taskid) + 1);
	}

	public static String getPostsPartitonID(String taskid, String tid) {
		String partitionid = tid.replaceAll("[^\\d]*", "").replaceAll("(\\d)*",
				"$1");
		// 分区位置计算方法 3*taskid+tid获取最后一位数字%3-1
		Integer partition = 3 * Integer.valueOf(taskid)
				+ Integer.valueOf(partitionid) % 3 - 1;
		return String.valueOf(partition);
	}

	public static AttributeList getHttpCookieStore(String tempUrl,
			String cookieStr) {
		AttributeList userCookieList = null;// 需要转换这个类

		// 设置cookie
		if (null != cookieStr && !cookieStr.trim().equals("")) {
			String[] cookies = cookieStr.split(";");
			userCookieList = new AttributeList();
			for (int i = 0; i < cookies.length; i++) {
				int _i = cookies[i].indexOf("=");
				if (_i != -1) {
					CookieParse _cookie = new CookieParse();
					_cookie.source = new StringBuffer(cookies[i]);
					_cookie.get();
					_cookie.setName(_cookie.get(0).getName());
					userCookieList.add(_cookie);
				}
			}
		}

		for (int i = 0; i < 5; i++) {
			try {
				HTTP _http = new HTTPSocket();

				// com.heaton.bot.Log.setLevel(com.heaton.bot.Log.LOG_LEVEL_DUMP);

				_http.setUseCookies(true, true);// 设置会话cookie为真，持久性cookie为真
				if (null != userCookieList)
					_http.cookieStore = userCookieList;// 附加已经获取的cookie
				_http.setTimeout(60 * 1000);
				_http.getClientHeaders().add(
						new Attribute("Accept-Encoding", "gzip,deflate"));
				_http.getClientHeaders().add(
						new Attribute("Connection", "close"));
				_http
						.setAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; SV1; .NET CLR 1.1.4322)");

				logger.debug("send begin");
				_http.send(tempUrl, null);

				logger.debug("send end");
				return _http.cookieStore;
			} catch (Exception e) {
				logger.debug("Exception   ", e);
			}
		}
		return null;
	}

	/**
	 * 发送请求
	 * 
	 * @param tempUrl
	 * @param cookieStr
	 * @return
	 */
	public static String getHttpBody(String tempUrl, String cookieStr) {
		AttributeList userCookieList = null;// 需要转换这个类
		String myBody = null;

		// 设置cookie
		if (null != cookieStr && !cookieStr.trim().equals("")) {
			String[] cookies = cookieStr.split(";");
			userCookieList = new AttributeList();
			for (int i = 0; i < cookies.length; i++) {
				int _i = cookies[i].indexOf("=");
				if (_i != -1) {
					CookieParse _cookie = new CookieParse();
					_cookie.source = new StringBuffer(cookies[i]);
					_cookie.get();
					_cookie.setName(_cookie.get(0).getName());
					userCookieList.add(_cookie);
				}
			}
		}

		// 验证转向
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 6;) {
				byte[] _buff1 = null;
				try {
					HTTP _http = new HTTPSocket();
					_http.setUseCookies(true, true);// 设置会话cookie为真，持久性cookie为真
					if (null != userCookieList)
						_http.cookieStore = userCookieList;// 附加已经获取的cookie
					_http.setTimeout(60 * 1000);
					_http.getClientHeaders().add(
							new Attribute("Accept-Encoding", "gzip,deflate"));
					_http.getClientHeaders().add(
							new Attribute("Connection", "close"));
					_http
							.setAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; SV1; .NET CLR 1.1.4322)");

					logger.debug("send begin");
					_http.send(tempUrl, null);
					logger.debug("send end");
					_buff1 = (_http.getBodyBytes());// 取得body二进制内容
					logger.debug(tempUrl + " before read stream");
					myBody = JavaUtil.readBytes(_buff1, 0);
					logger.debug(tempUrl + " end read stream");
					// if(_http.getReferrer()!=null||_http.getReferrer().length()>0){
					// this.httpHead.add(new
					// Attribute("location",_http.getReferrer()));
					// }
					// timeout = false;
					break;
				} catch (Exception e) {
					logger.debug("Exception   ", e);
				}
			}
			// logger.debug("返回头为："+this.httpHead);
			if (myBody == null || myBody.length() < 100) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				continue;
			}
		}
		return myBody;
	}

	public static List<org.dom4j.Element> getRSSEntries(org.dom4j.Document doc) {
		List<org.dom4j.Element> ret = null;
		try {
			Iterator it = doc.getRootElement().elementIterator("entry");
			if (it.hasNext()) {
				ret = new ArrayList<org.dom4j.Element>();
				Element ele = null;
				for (; it.hasNext();) {
					ele = (Element) it.next();

					ret.add(ele);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public static List<org.dom4j.Element> getRSSEntries(String xmlContent) {
		return getRSSEntries(xmlContent, null);
	}

	public static List<org.dom4j.Element> getRSSEntries(String xmlContent,
			String itemName) {
		String name = "entry";
		if (itemName != null && !itemName.equals(""))
			name = itemName;
		List<org.dom4j.Element> ret = null;
		try {
			org.dom4j.Document doc = getXMLDocument(xmlContent);
			Iterator it = doc.getRootElement().elementIterator(name);
			if (it.hasNext()) {
				ret = new ArrayList<org.dom4j.Element>();
				Element ele = null;
				for (; it.hasNext();) {
					ele = (Element) it.next();

					ret.add(ele);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public static List<org.dom4j.Element> getRSSEntries(String xmlContent,
			String channel, String itemName) {
		String name = "entry";
		if (itemName != null && !itemName.equals(""))
			name = itemName;
		List<org.dom4j.Element> ret = null;
		try {
			org.dom4j.Document doc = getXMLDocument(xmlContent);
			Iterator it = doc.getRootElement().elementIterator(name);
			if (it.hasNext()) {
				ret = new ArrayList<org.dom4j.Element>();
				Element ele = null;
				for (; it.hasNext();) {
					ele = (Element) it.next();

					ret.add(ele);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public static org.dom4j.Document getXMLDocument(String source) {
		org.dom4j.Document doc = null;
		try {
			doc = DocumentHelper.parseText(source);
		} catch (DocumentException e) {
			logger.error("解析xml出现错误！", e);
			logger.debug(source);
		}
		return doc;
	}

	/**
	 * 将String 解析成Documnet
	 * 
	 * @param tempbodyStr
	 * @return
	 * @throws Exception
	 */
	public static Document getDocument(String tempbodyStr) throws Exception {
		String bodyStr = tempbodyStr.replace("　", " ")
				.replaceAll("&nbsp;", " ");
		// 生成html parser
		DOMParser parser = new DOMParser();
		// 设置网页的默认编码
		parser.setFeature("http://xml.org/sax/features/namespaces", false);
		parser.setProperty(
				"http://cyberneko.org/html/properties/default-encoding",
				"UTF-8");
		java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(
				bodyStr.getBytes("UTF-8"));
		InputStreamReader fileIn = new InputStreamReader(bis, "UTF-8");
		BufferedReader in = new BufferedReader(fileIn);
		parser.parse(new InputSource(in));
		Document doc = parser.getDocument();
		return doc;
	}

	public static String XmlToString(Node node) {
		try {
			// org.apache.xml.serializer.TreeWalker;
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString().replaceAll("<\\?.*\\?>",
					"");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取结点的Text值
	 * 
	 * @param node
	 * @return
	 */
	public static String getTextContent(Node node) {
		if (node == null)
			return null;
		String textContent = node.getTextContent();
		if (textContent == null)
			return textContent;
		return textContent.trim();
	}

	/**
	 * 获取结点的属性值
	 * 
	 * @param node
	 * @param attrName
	 * @return
	 */
	public static String getNodeValue(Node node, String attrName) {
		if (node == null || node.getAttributes() == null)
			return null;
		Node attrNode = node.getAttributes().getNamedItem(attrName);
		if (attrNode == null || attrNode.getNodeValue() == null)
			return null;
		return attrNode.getNodeValue().trim();
	}

	/**
	 * 获取结点的Tag (XML)
	 * 
	 * @param node
	 * @return
	 */
	public static String getTagContent(Node node) {
		return JavaUtil.XmlToString(node);
	}

	/**
	 * 修复标签
	 * 
	 * @param node
	 * @return
	 */
	public static String fixTags(String node) {
		final StringReader sr = new StringReader(node);
		final XMLInputSource in = new XMLInputSource(null, "foo", null, sr,
				null);

		DOMParser d = new DOMParser();
		try {
			d
					.setFeature(
							"http://cyberneko.org/html/features/balance-tags/ignore-outside-content",
							true);
			d.parse(in);
		} catch (Exception e) {
			logger.error("修复标签错误", e);
		}
		fixhtml = "";
		fixhtml = writeDoc(d.getDocument().getDocumentElement());
		return fixhtml;
	}

	private static String fixhtml = "";

	private static String writeDoc(Node node) {
		short type = node.getNodeType();
		switch (type) {

		case Node.ELEMENT_NODE: {
			String name = "<" + node.getNodeName();
			NamedNodeMap attrs = node.getAttributes();
			if (attrs != null) {
				int length = attrs.getLength();
				for (int i = 0; i < length; i++) {
					Node attr = attrs.item(i);
					name += " " + attr.getNodeName();
					name += "=\"" + attr.getNodeValue() + "\"";
				}
			}
			name += ">";
			fixhtml = fixhtml + name;

			NodeList children = node.getChildNodes();
			if (children != null) {
				int length = children.getLength();
				for (int i = 0; i < length; i++)
					writeDoc(children.item(i));
			}
			fixhtml = fixhtml + "</" + node.getNodeName() + ">";
			break;
		}
		case Node.TEXT_NODE: {
			fixhtml = fixhtml + node.getNodeValue();
			break;
		}
		}
		return fixhtml;
	}

	public static String clearHtml(String webSourcePage) {
		String target = webSourcePage.replaceAll("(?i)(?s)<style.*?</style>",
				"").replaceAll("(?i)(?s)<(no)?script.*?</(no)?script>", "")
				.replaceAll("(?i)(?s)<select.*?</select>", "").replaceAll(
						"(?i)(?s)<!--.*?-->", "");
		target = target.replaceAll("&nbsp;?", " ").replaceAll("[ ]{2,}", " ")
				.replaceAll("\\s+", " ");
		return target;
	}

	public static boolean checkPidExist(String pid) {
		boolean ret = false;
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			String line;
			int lineNum = 0;
			try {
				Process proc = Runtime.getRuntime().exec(
						"tasklist /fi \"pid eq " + pid + "\"");
				BufferedReader input = new BufferedReader(
						new InputStreamReader(proc.getInputStream()));
				//				
				while ((line = input.readLine()) != null) {
					lineNum++;
				}
				input.close();
				if (lineNum > 1)
					ret = true;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			String line;
			int lineNum = 0;
			try {
				Process proc = Runtime.getRuntime().exec("ps -p " + pid);
				BufferedReader input = new BufferedReader(
						new InputStreamReader(proc.getInputStream()));
				//				
				while ((line = input.readLine()) != null) {
					lineNum++;
				}

				input.close();
				if (lineNum > 1)
					ret = true;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

		}
		return ret;
	}

	/**
	 * Create an absolute URL from a relative link.
	 * 
	 * @param link
	 *            The reslative portion of a URL.
	 * @return The fully qualified URL or the original link if it was absolute
	 *         already or a failure occured.
	 */
	public static String getAbsoluteURL(String baseUrl, String link) {
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
	public static String getAbsoluteURL(String baseUrl, String link,
			boolean strict) {
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
				// if (link.indexOf("javascript:") == -1)
				// logger.error(baseUrl + "解析超链接出现异常：" + link, murle);
				ret = null;
			}

		return (ret);
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
	public static URL constructUrl(String link, String base, boolean strict)
			throws MalformedURLException {
		String path;
		boolean modified;
		boolean absolute;
		int index;
		URL url; // constructed URL combining relative link and base

		// Bug #1461473 Relative links starting with ?
		if (!strict && ('?' == link.charAt(0))) { // remove query part of base
			// if any
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
}