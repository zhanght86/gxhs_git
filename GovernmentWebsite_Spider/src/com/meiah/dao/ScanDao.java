package com.meiah.dao;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.meiah.po.KeyWord;
import com.meiah.po.KwScore;
import com.meiah.po.News;
import com.meiah.trs.NewsTrsDo;
import com.meiah.util.Config;
import com.meiah.util.Converter;
import com.meiah.util.MyDate;

/**
 * 统计类Dao。统计当前各种数量，获取临时内容插入缓存表
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-08-06
 */
public class ScanDao {

	private static Logger logger = Logger.getLogger(ScanDao.class);
	private BaseDao baseDao = new BaseDao();

	private static ScanDao dao;

	private ScanDao() {
	}

	public static ScanDao getInstance() {
		if (dao == null)
			dao = new ScanDao();

		return dao;
	}

	public void createNewsTrs() {
		String base = ClassLoader.getSystemResource("").toString().replaceAll("file:/", "");
		String t1 = "", t2 = "";
		File f = new File(base + "config");
		t1 = new MyDate().get_yyyymmddhh24miss();
		boolean has = false;
		for (File o : f.listFiles()) {
			if (o.getName().matches("\\d{14}.indextime")) {
				t1 = o.getName().replaceAll(".indextime", "");
				o.renameTo(new File(base + "config/" + new MyDate().get_yyyymmddhh24miss() + ".indextime"));
				has = true;
				break;
			}
		}
		try {
			if (!has)
				new File(base + "config/" + new MyDate().get_yyyymmddhh24miss() + ".indextime").createNewFile();
		} catch (Exception e) {
			logger.error("create timefile error", e);
		}
		t2 = new MyDate().get_yyyymmddhh24miss();
		t1 = t1.replaceAll("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})", "$1-$2-$3 $4:$5:$6");
		t2 = t2.replaceAll("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})", "$1-$2-$3 $4:$5:$6");
		
		String sqlStr = "select taskid,isnull(TopDomain,'')TopDomain,isnull(WebDomain,'')WebDomain,page_url,isnull(title,'')title,isnull(pageSize,0)pageSize,datePublished,Savetime,isnull(Filename,'')Filename,isnull(islist,'')islist,"
				+ "isnull(Conform,'')Conform,isnull(IP,'')IP,isnull(content,'')content,isnull(username,'')username,isnull(page_type,'')page_type,isnull(spot_code,'')spot_code,isnull(source_type,'')source_type,id,isnull(refererurl,'')refererurl,isnull(isHomePageUrl,0)isHomePageUrl from n_realtime "
				+ " where trstime>'"
				+ t1
				+ "' "
				+ "and trstime<='"
				+ t2
				+ "' and islist=0 ";
		logger.info("trs_sql: " + new Date() + sqlStr);
		List<Object[]> allNewsObj = new ArrayList<Object[]>();
		List<News> allNews = new ArrayList<News>();
		try {
			allNewsObj = baseDao.query(sqlStr);
			logger.info("查询统计生成News的TRS数据完成。共：" + allNewsObj.size() + " 条数据");

			for (int i = 0; i < allNewsObj.size(); i++) {
				News n = new News();
				n.setTaskid(allNewsObj.get(i)[0].toString());
				n.setTopDomain(allNewsObj.get(i)[1].toString());
				n.setWebDomain(allNewsObj.get(i)[2].toString());
				n.setUrl(allNewsObj.get(i)[3].toString());
				n.setTitle(allNewsObj.get(i)[4].toString());
				n.setLength((Integer) allNewsObj.get(i)[5]);
				n.setDateline(Converter.getUtilDateFromString(allNewsObj.get(i)[6].toString(), "yyyy-MM-dd HH:mm:ss"));
				n.setSavetime(Converter.getUtilDateFromString(allNewsObj.get(i)[7].toString(), "yyyy-MM-dd HH:mm:ss"));
				n.setFilename(allNewsObj.get(i)[8].toString());
				n.setIslist((Integer) allNewsObj.get(i)[9]);
				n.setConform(allNewsObj.get(i)[10].toString());
				n.setIp(allNewsObj.get(i)[11].toString());
				n.setContent(allNewsObj.get(i)[12].toString());
				n.setUserName(allNewsObj.get(i)[13].toString());
				n.setPage_type(allNewsObj.get(i)[14].toString());
				n.setSpot_code(allNewsObj.get(i)[15].toString());
				n.setSource_type(allNewsObj.get(i)[16].toString());
				n.setId((Integer) allNewsObj.get(i)[17]);
				n.setReferUrl(allNewsObj.get(i)[18].toString());
				n.setIsHomePageNews(Integer.parseInt(allNewsObj.get(i)[19].toString()));
				allNews.add(n);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("统计生成News的TRS数据失败", e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();

			logger.error("统计生成News的TRS数据失败", e);
		}
		// logger.info("查询统计生成post的TRS数据结束，开始生成TRS文件");
		// if (Config.getYkdatapath() != null
		// && Config.getYkdatapath().trim().length() > 0)
		// new PostsDataToYK(allNews).start();
		new NewsTrsDo(allNews).start();
	}

	public void createKWScores() {
		String base = ClassLoader.getSystemResource("").toString().replaceAll(
				"file:/", "");
		String t1 = "", t2 = "";
		File f = new File(base + "config");
		t1 = new MyDate().get_yyyymmddhh24miss();
		boolean has = false;
		for (File o : f.listFiles()) {
			if (o.getName().matches("\\d{14}.kwtime")) {
				t1 = o.getName().replaceAll(".kwtime", "");
				o.renameTo(new File(base + "config/"
						+ new MyDate().get_yyyymmddhh24miss() + ".kwtime"));
				has = true;
				break;
			}
		}
		try {
			if (!has)
				new File(base + "config/" + new MyDate().get_yyyymmddhh24miss()
						+ ".kwtime").createNewFile();
		} catch (Exception e) {
			logger.error("create time1file error", e);
		}
		t2 = new MyDate().get_yyyymmddhh24miss();
		t1 = t1.replaceAll("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})",
				"$1-$2-$3 $4:$5:$6");
		t2 = t2.replaceAll("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})",
				"$1-$2-$3 $4:$5:$6");
		String sqlStr = "select id,taskid,isnull(content,'')content from n_realtime "
				+ " where trstime>'"
				+ t1
				+ "' "
				+ "and trstime<='"
				+ t2
				+ "' and islist=0 ";
		logger.debug("kwscore_sql: " + new Date() + sqlStr);
		List<Object[]> allNewsObj = new ArrayList<Object[]>();
		List<News> allNews = new ArrayList<News>();
		try {
			allNewsObj = baseDao.query(sqlStr);
			logger.info("查询统计生成聚焦新闻分数数据完成，共：" + allNewsObj.size() + " 条");

			for (int i = 0; i < allNewsObj.size(); i++) {
				News n = new News();
				n.setId((Integer) allNewsObj.get(i)[0]);
				n.setTaskid(allNewsObj.get(i)[1].toString());
				n.setContent(allNewsObj.get(i)[2].toString());

				allNews.add(n);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("查询统计生成聚焦新闻分数数据失败", e);
		}
		List<KeyWord> keyWordsList = KeyWordDao.getInstance().getKeyWord();
		String sqlStrKeywordScore = "insert n_kwscore (taskid,nid,sortid,kwlist,score) "
				+ "values (?,?,?,?,?)";
		List<Object[]> parasValueKWScores = new ArrayList<Object[]>();
		for (int i = 0; i < allNews.size(); i++) {

			News ne = allNews.get(i);
			HitBaseKeyWord(ne, keyWordsList);
			if (ne.getKs() != null) {
				// logger.info("得到匹配的7大类---hepl");
				Object[] parasValueScore1 = new Object[] { ne.getTaskid(),
						ne.getId(), ne.getKs().getSortid(),
						ne.getKs().getKwlist(), ne.getKs().getScore() };
				parasValueKWScores.add(parasValueScore1);
			}
		}
		try {
			baseDao.save(sqlStrKeywordScore, parasValueKWScores);
		} catch (SQLException e) {
			if (e.getMessage().indexOf("IX_n_kwscore") == -1)
				logger.debug("插入聚焦错误：" + e.getMessage(), e);
		}

	}

	public int createKWScores1(int idstart) {

		int id = 0;
		String base = ClassLoader.getSystemResource("").toString().replaceAll(
				"file:/", "");
		String t1 = "", t2 = "";
		File f = new File(base + "config");
		t1 = new MyDate().get_yyyymmddhh24miss();
		boolean has = false;
		for (File o : f.listFiles()) {
			if (o.getName().matches("\\d{14}.kwtime")) {
				t1 = o.getName().replaceAll(".kwtime", "");
				o.renameTo(new File(base + "config/"
						+ new MyDate().get_yyyymmddhh24miss() + ".kwtime"));
				has = true;
				break;
			}
		}
		try {
			if (!has)
				new File(base + "config/" + new MyDate().get_yyyymmddhh24miss()
						+ ".kwtime").createNewFile();
		} catch (Exception e) {
			logger.error("create time1file error", e);
		}
		t2 = new MyDate().get_yyyymmddhh24miss();
		t1 = t1.replaceAll("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})",
				"$1-$2-$3 $4:$5:$6");
		t2 = t2.replaceAll("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})",
				"$1-$2-$3 $4:$5:$6");
		String sqlStr = "select top 5000 id,taskid,isnull(content,'')content from n_realtime "
				+ "where datediff(dd,trstime,getdate())<3 and islist=0 and id>"
				+ idstart + " order by id";
		// + " where trstime>'"
		// + t1
		// + "' "
		// + "and trstime<='"
		// + t2
		// + "' and islist=0 ";
		logger.info("kwscore_sql: " + new Date() + sqlStr);
		List<Object[]> allNewsObj = new ArrayList<Object[]>();
		List<News> allNews = new ArrayList<News>();
		try {
			allNewsObj = baseDao.query(sqlStr);
			logger.info("查询统计生成聚焦新闻分数数据完成");

			for (int i = 0; i < allNewsObj.size(); i++) {
				News n = new News();
				n.setId((Integer) allNewsObj.get(i)[0]);
				n.setTaskid(allNewsObj.get(i)[1].toString());
				n.setContent(allNewsObj.get(i)[2].toString());

				allNews.add(n);
				if (i == allNewsObj.size() - 1) {
					id = (Integer) allNewsObj.get(i)[0];
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("查询统计生成聚焦新闻分数数据失败", e);
		}
		List<KeyWord> keyWordsList = KeyWordDao.getInstance().getKeyWord();
		String sqlStrKeywordScore = "insert n_kwscore (taskid,nid,sortid,kwlist,score) "
				+ "values (?,?,?,?,?)";
		List<Object[]> parasValueKWScores = new ArrayList<Object[]>();
		for (int i = 0; i < allNews.size(); i++) {

			News ne = allNews.get(i);
			HitBaseKeyWord(ne, keyWordsList);
			if (ne.getKs() != null) {
				// logger.info("得到匹配的7大类---hepl");
				Object[] parasValueScore1 = new Object[] { ne.getTaskid(),
						ne.getId(), ne.getKs().getSortid(),
						ne.getKs().getKwlist(), ne.getKs().getScore() };
				parasValueKWScores.add(parasValueScore1);
			}
		}
		try {
			baseDao.save(sqlStrKeywordScore, parasValueKWScores);
		} catch (SQLException e) {
			if (e.getMessage().indexOf("IX_n_kwscore") == -1)
				logger.debug("插入聚焦错误：" + e.getMessage(), e);
		}

		return id;

	}

	/**
	 * 分析命中
	 * 
	 * @param keywordList
	 * @param newsContent
	 */

	private void HitBaseKeyWord(News ne, List<KeyWord> keywordList) {
		String newsContent = ne.getContent();
		if (keywordList != null && keywordList.size() > 0) {
			List<KeyWord> hitKwList = new ArrayList<KeyWord>();

			int hitScore = 0;
			String strKWList = "";
			for (int i = 0; i < keywordList.size(); i++) {
				KeyWord keyword = keywordList.get(i);
				// String[] separator = { " ", ",", "、", ";", ";" };// 分隔符
				if (keyword.getKeywordsmust() != null
						&& !keyword.getKeywordsmust().equals("")) {
					String[] wordMustArray = keyword.getKeywordsmust().split(
							" ");
					boolean flagMust = true;// 对“全包含关键词”，是否全部命中各关键词
					for (String word : wordMustArray) {
						if (word == null || word.trim().equals("")) {
							continue;
						}
						// #region 遍历“全包含关键词”的各词
						int index = newsContent.indexOf(word);
						// 如果有一个未命中，退出
						if (index == -1) {
							flagMust = false;
							break;
						} else {
							// 碰撞到的关键词列表
							if (strKWList == null || strKWList.equals("")) {
								strKWList = "," + word + ",";
							} else {
								if (strKWList.indexOf("," + word + ",") == -1)
									strKWList += word + ",";
							}
						}
					}
					// 对“全包含关键词”，没有命中全部关键词时，继续下一组关键词
					if (!flagMust) {
						strKWList = "";
						continue;
					}
				}

				if (keyword.getKeywords() != null
						&& !keyword.getKeywords().equals("")) {
					// #region “半包含关键词” --类别ID列表 --关键词ID列表 --分值 --"类别-词碰撞次数"

					String[] wordOneArray = keyword.getKeywords().split(" ");
					int wordOneHitCount = 0;// 对“半包含关键词”，关键词命中个数
					for (String word : wordOneArray) {
						if (word == null || word.trim().equals("")) {
							continue;
						}
						// #region 遍历“半包含关键词”的各词
						int index = newsContent.indexOf(word);
						// 命中
						if (index != -1) {
							wordOneHitCount++;
							// 碰撞到的关键词列表
							if (strKWList == null || strKWList.equals("")) {
								strKWList = "," + word + ",";
							} else {
								if (strKWList.indexOf("," + word + ",") == -1)
									strKWList += word + ",";
							}
						}
					}
					// 对“半包含关键词”，没有命中任意一个关键词时，继续下一组关键词
					if (wordOneHitCount == 0) {
						strKWList = "";
						continue;
					} else {
						strKWList = strKWList.substring(1);
					}

					// #region 分值，即敏感度
					// String[] separatorkw = { "," };// 分隔符
					int kwcount = strKWList.split(",").length;
					hitScore += kwcount * keyword.getScore();

					keyword.setKeywords(strKWList);// 将命中关键词暂时寄放这里
					hitKwList.add(keyword);// 保存命中的关键词
				}
			}
			// System.out.println(strKWList);
			if (hitKwList.size() > 0) {// 如果有命中关键词，则填冲关键词信息
				int sortid = 0;
				int allScore = 0;
				String allStrKw = "";
				for (KeyWord kw : hitKwList) {// 关键词列表必须按
					// 最高weight排序，则命中后只保留第一个高级weight的类别
					if (sortid == 0) {
						sortid = kw.getSortid();
					} else if (kw.getSortid() != sortid) {
						break;
					}
					allScore += kw.getScore();
					allStrKw += kw.getKeywords();
				}
				// ne.getKs() = new KwScore();
				ne.setKs(new KwScore());
				ne.getKs().setKwlist(allStrKw);
				ne.getKs().setScore(hitScore);
				ne.getKs().setSortid(sortid);
				ne.getKs().setTaskid(ne.getTaskid().toString());
				// ks.setTid(id + "");
			}
		}
	}

	/**
	 * 清理超过指定日期的数据
	 */
	public void clearOlddatas() {
		int keepDay = 0;
		try {
			keepDay = Integer.parseInt(Config.getDeldays());
		} catch (Exception e) {
			logger.error("获取保存天数异常，检查配置文件", e);
		}
		logger.info("开始清除历史数据， 保存时间为：" + keepDay + " 天(o天代表不删除历史数据)");
		if (keepDay == 0)
			return;

		try {
			String classPath = File.separatorChar + "lib" + File.separatorChar;
			String sep = "";
			if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
				sep = ";";
			} else {
				sep = ":";
			}
			// String t_classPath = classPath;
			String getClassRootPath = ClassLoader.getSystemResource("lib")
					.getPath();
			String t_classPath = classPath;
			classPath = getClassRootPath;

			File dirOfJar = new File(classPath);

			if (dirOfJar.isDirectory()) {
				String[] filenames = dirOfJar.list();
				classPath = "." + sep;
				for (String filename : filenames) {
					if (filename.endsWith(".jar"))
						classPath += "." + t_classPath + filename + sep;
				}
				if (logger.isDebugEnabled())
					logger.debug("classPath=" + classPath);
			}
			// String classPath = "\\lib\\";
			// String t_classPath = classPath;
			// String getClassRootPath = ClassLoader.getSystemResource("")
			// .getPath();
			// // String nowClassPath = getClassRootPath + nowLibPath;
			// classPath = getClassRootPath + classPath;
			// File dirOfJar = new File(classPath);
			//
			// if (dirOfJar.isDirectory()) {
			// String[] filenames = dirOfJar.list();
			// classPath = ".;";
			// for (String filename : filenames) {
			// if (filename.endsWith(".jar"))
			// classPath += "." + t_classPath + filename + ";";
			// }
			// logger.info("classPath=" + classPath);
			// }
			/*String clearHisCMD = "java -classpath " + classPath
					+ " -Xmx1024m com.meiah.webCrawlers.ClearHisNews";
			logger.info(clearHisCMD);
			Runtime.getRuntime().exec(clearHisCMD);*/
		} catch (Exception e) {
			logger.error("清理超过指定日期的新闻任务异常", e);
		}
		// String sqlStr = "delete from n_realtime where islist=0 and
		// datediff(dd,datepublished,getdate())>"
		// + keepDay;
		// try {
		// logger.info("开始删除数据库历史数据：" + sqlStr);
		// long t = System.currentTimeMillis();
		// baseDao.save(sqlStr);
		// logger.info("删除数据库历史数据成功,耗时：" + (System.currentTimeMillis() - t)
		// + " ms");
		// } catch (Exception e) {
		// logger.error("清理超过指定日期的数据库数据任务异常", e);
		// }
	}

	public static void main(String[] args) {
		// ScanDao s = ScanDao.getInstance();
		// try {
		// System.out.println(Config.getUrl());
		// //s.createKWScores1(s.createKWScores1(0));
		// int idStart = s.createKWScores1(0);
		// for (int i = 0; i < 30; i++) {
		// idStart = s.createKWScores1(idStart);
		//
		// }
		// // s.cleanHistoryDay();
		// } catch (RuntimeException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		ScanDao.getInstance().createNewsTrs();
	}
}
