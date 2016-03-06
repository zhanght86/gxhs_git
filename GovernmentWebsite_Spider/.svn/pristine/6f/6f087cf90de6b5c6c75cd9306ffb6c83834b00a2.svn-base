package com.meiah.test;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.meiah.util.JavaUtil;
import com.meiah.util.WebPageDownloader;
import com.meiah.webCrawlers.SiteCrawler;

public class TestPageDownkoad {
	
	private static Logger logger = Logger.getLogger(TestPageDownkoad.class);

	/**
	 * @author 胡海斌
	 * @throws ParseException
	 * @throws IOException
	 * @date Jul 7, 2010 返回值：void
	 */
	public static void main(String[] args) throws ParseException, IOException {
		String url = "http://ip.qq.com/cgi-bin/searchip?searchip1=116.255.158.120";
		WebPageDownloader wd = new WebPageDownloader(url);
//		System.out.println("accepted" + url);
//		System.out.println("downloading...");


		wd.setCheckCookie(true);
		wd.setCheckRedirect(true);
//		wd.setUseProxyIf(true);
//
////		wd.setProxyUrl("203.176.237.47");
//		wd.setProxyUrl("xmhk39.computerforensic.cn");
////		wd.setProxyUrl("218.5.73.241");
//		wd.setProxyPort("443");
//		wd.setProxyUser("pico");
//		wd.setProxyPwd("pico2009server");
//		
//		String cookies = "JSESSIONID=63A71845199C9086E544925E145D965A; _xauthTK=%7B%22uid%22%3A1069182125%2C%22uname%22%3A%22mtbs%22%2C%22time%22%3A1432624490288%7D; _xauthSG=c9295b24d11efd6d2aae9b5ff1621e12; SSO=TzTSunUWaTFEY6SSRMPGT8MrvqOdCKUGnLp2wvIZDmUIvJQiMIslRREJ%2BcaOXD3UqEYbWXCCxDFM%0Aj2WAtVg6ldlfjxHaFhfk6ttDrZD6Lt7R3pQipjqrUFzCrDATVraS";
//		wd.setCookieStr(cookies);
		try {
			String info = wd.getPageContent();
//			byte info[] = wd.getImageContent();
			JavaUtil.writeFile(info, "GBK", "t.txt");
			logger.info("result：" + info);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}