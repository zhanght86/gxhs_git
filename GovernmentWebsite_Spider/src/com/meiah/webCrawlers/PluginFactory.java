package com.meiah.webCrawlers;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.meiah.po.SiteConfig;
import com.meiah.po.Task;
import com.meiah.po.TaskLink;
import com.meiah.util.JavaUtil;

public class PluginFactory {
	private static Logger logger = Logger.getLogger(PluginFactory.class);

	private Map<String, SiteConfig> typeMapping = new HashMap<String, SiteConfig>();// 类型映射
	private static PluginFactory factory = new PluginFactory();
	private static String configPath;// 类型映射文件地址

	public PluginFactory() {

		loadTypeConfig();

	}

	public static PluginFactory getInstance() {
		if (factory == null)
			factory = new PluginFactory();

		return factory;
	}

	/**
	 * 根据任务URL分析获取任务类型
	 * 
	 * @return PageCrawler子类实例
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public PageCrawler getPageCrawler(TaskLink link, Task task)
			throws ClassNotFoundException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		// if (task.pluginMode == true
		// && (typeMapping == null || typeMapping.size() == 0))
		// this.loadTypeConfig();
		//
		// if (typeMapping.size() == 0) {
		// // logger.info("配置文件中没有任何类型！");
		// // logger.info("爬取插件不存在，默认爬取方式");
		// return null;
		// }
		SiteConfig siteConfig = task.getSiteConfig();
		// SiteConfig siteConfig = typeMapping.get(task.getType());
		String clazzName = null;
		if (siteConfig != null) {
			clazzName = siteConfig.getCrawlerClassname();
			if (clazzName == null || clazzName.trim().equals("")) {
				// logger.info("爬取插件不存在，默认爬取方式");
				return null;
			} else {
				// logger.info("爬取插件:" + clazzName);
			}
		} else {
			logger.info("爬取插件不存在，默认爬取方式");
			return null;
		}

		// 构造类的构造函数参数列表
		Class[] constructorParams = new Class[] { TaskLink.class, Task.class };
		Object[] paras = new Object[] { link, task };

		Class Object = Class.forName(clazzName);

		// 构造函数
		Constructor constr = Object.getConstructor(constructorParams);

		// 获得实例，并指定到抽象父类
		return (PageCrawler) constr.newInstance(paras);
	}

	public SiteConfig getSiteConfig(Task task) {
		SiteConfig conf = null;

		if (task.pluginMode == true
				&& (typeMapping == null || typeMapping.size() == 0))
			loadTypeConfig();

		if (typeMapping.size() == 0) {
			logger.warn("配置文件中没有任何插件类型！");
			// System.exit(-1);
			return null;
		}
		String webDomain = JavaUtil.getHost2(task.getUrl());
		String topDomain = JavaUtil.getHost1(task.getUrl());
		// String clazzName = typeMapping.get(topDomain);

		conf = typeMapping.get(webDomain);
		if (conf == null)
			conf = typeMapping.get(topDomain);
		String clazzName = null;
		if (conf != null) {
			clazzName = conf.getResolverClassname();
			if (clazzName == null || clazzName.equals("")) {
				logger.error("解析插件[" + topDomain + "]不存在，检查配置文件！！");
				// System.exit(-1);
				return null;
			} else {
				try {
					Class.forName(clazzName);
				} catch (Exception e) {
					logger.error("解析插件[" + clazzName + "],插件不存在，检查配置文件！！");
					return null;
				}
			}
			clazzName = conf.getCrawlerClassname();
			if (clazzName != null && !clazzName.equals(""))
				try {
					Class.forName(clazzName);
				} catch (Exception e) {
					logger.error("抓取插件[" + clazzName + "],插件不存在，检查配置文件！！");
					return null;
				}
		} else {
			logger.debug("解析插件不存在");
			// System.exit(-1);
			return null;
		}

		return conf;
	}

	/**
	 * 根据任务URL分析获取任务类型
	 * 
	 * @return PageCrawler子类实例
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public PageResolver getPageResolver(Task task) {
		// if (task.pluginMode == true
		// && (typeMapping == null || typeMapping.size() == 0))
		// loadTypeConfig();
		//
		// if (typeMapping.size() == 0) {
		// logger.info("配置文件中没有任何类型！");
		// System.exit(-1);
		// return null;
		// }
		//

		// // String clazzName = typeMapping.get(topDomain);
		String topDomain = JavaUtil.getHost1(task.getUrl());
		SiteConfig siteConfig = task.getSiteConfig();
		String clazzName;
		if (siteConfig != null) {
			clazzName = siteConfig.getResolverClassname();
			if (clazzName == null || clazzName.trim().equals("")) {
				logger.error("解析插件[" + topDomain + "]不存在，检查配置文件！！");
				System.exit(-1);
				return null;
			}
		} else {
			logger.error("解析插件不存在，检查配置文件！！");
			System.exit(-1);
			return null;
		}
		// if (clazzName == null) {
		// logger.error("解析插件不存在，检查配置文件！！");
		// System.exit(-1);
		// return null;
		// } else {
		// logger.info("爬取插件:" + clazzName);
		// }

		// 构造类的构造函数参数列表
		Class[] constructorParams = new Class[] { Task.class };
		Object[] paras = new Object[] { task };

		Class Object;
		Constructor constr;
		PageResolver resolver = null;
		try {
			Object = Class.forName(clazzName);
			// 构造函数
			constr = Object.getConstructor(constructorParams);
			resolver = (PageResolver) constr.newInstance(paras);
		} catch (Exception e) {
			logger.error("解析插件不存在，检查配置文件！！", e);
			System.exit(-1);
		}

		// 获得实例，并指定到抽象父类
		return resolver;
	}

	@SuppressWarnings("unchecked")
	public boolean getPluginMode(Task task) {
		boolean pluginMode = false;

		if (getSiteConfig(task) != null)
			pluginMode = true;
		return pluginMode;
	}

	/**
	 * 读取配置文件
	 */
	@SuppressWarnings("unchecked")
	private void loadTypeConfig() {
		configPath = PluginFactory.class.getResource("/").getPath().replaceAll("%20", " ") + "type-mapping.xml";
		SAXReader reader = new SAXReader();
		Document doc = null;
		Iterator it1 = null;
		Element ele1 = null;

		try {
			doc = reader.read(new File(configPath));
			for (it1 = doc.getRootElement().elementIterator("process-mapping"); it1.hasNext();) {
				ele1 = (Element) it1.next();
				SiteConfig siteConfig = new SiteConfig();
				String typename = ele1.elementTextTrim("typename");
				String crawlerClassname = ele1.elementTextTrim("crawlerClassname");
				String resolverClassname = ele1.elementTextTrim("resolverClassname");
				String contentUrlRegex = ele1.elementTextTrim("contentUrlRegex");
				String titleLocation = ele1.elementTextTrim("titleLocation");
				String contentLocation = ele1.elementTextTrim("contentLocation");
				String sourceSiteLocation = ele1.elementTextTrim("sourceSiteLocation");
				String authorLocation = ele1.elementTextTrim("authorLocation");
				String publishTimeLocation = ele1.elementTextTrim("publishTimeLocation");
				String imageUrlLocation = ele1.elementTextTrim("imageUrlLocation");
				siteConfig.setCrawlerClassname(crawlerClassname.trim());
				siteConfig.setResolverClassname(resolverClassname.trim());
				siteConfig.setContentUrlRegex(contentUrlRegex.trim());
				siteConfig.setTitleLocation(titleLocation);
				siteConfig.setContentLocation(contentLocation);
				siteConfig.setSourceSiteLocation(sourceSiteLocation);
				siteConfig.setAuthorLocation(authorLocation);
				siteConfig.setPublishTimeLocation(publishTimeLocation);
				siteConfig.setImageUrlLocation(imageUrlLocation);
				
				typeMapping.put(typename, siteConfig);
			}
		} catch (DocumentException e) {
			logger.error("配置文件[type-mapping.xml]不存在或格式读取异常,将启用自动模式");
		}
	}

	public static void main(String[] args) {

//		System.out.println(JavaUtil
//				.getHost1("http://bm.malaysia-chronicle.com/"));
		// TaskLink link = new TaskLink();
		// link.setTaskid("1");
		// link.setUrl("http://v.youku.com/v_show/id_XMzkxNjI5ODQ=.html");
		// link.setLevel(1);
		// // Task t = TaskDao.getInstance().getTask("1");
		// Task t = new Task();
		// t.setTurl("http://www.epochtimes.com/");
		// PluginFactory pf = new PluginFactory();
		// try {
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}
