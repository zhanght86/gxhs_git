package com.meiah.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.meiah.po.News;
import com.meiah.util.BloomFilter;
import com.meiah.util.Converter;
import com.meiah.util.MD5Utils;
import com.meiah.util.MyDate;
import com.meiah.util.SysConstants;

/**
 * @author huhb
 * 
 */
public class NewsDao {

	private Logger logger = Logger.getLogger(NewsDao.class);
	private BaseDao baseDao = new BaseDao();

	private static NewsDao dao;

	private NewsDao() {
	}

	public static NewsDao getInstance() {
		if (dao == null)
			dao = new NewsDao();

		return dao;
	}

	// public boolean isUrlVisited(String taskid, String url) {
	// boolean flag = false;
	// String sqlStr = "select islist from n_realtime where taskid=" + taskid
	// + " and page_url='" + url
	// + "' and (islist=0 or islist=9 or islist=8)";
	// // Object[] parasValue = new Object[] { taskid, url };
	// // logger.debug("sql : select taskid from n_realtime where taskid=" +
	// // taskid + " and page_url='" + url
	// // + "' and islist=" + 0);
	// try {
	// List<Object[]> results = baseDao.query(sqlStr);
	// if (results.size() != 0) {
	// int islist = (Integer) results.get(0)[0];
	// flag = true;
	// switch (islist) {
	// case 0: {
	// logger.debug("url:" + url + " 已经运行过，而且是正文页");
	// break;
	// }
	// case 9: {
	// logger.debug("url:" + url + " 已经运行过，而且是404页面");
	// break;
	// }
	// case 8: {
	// logger.debug("url:" + url + " 已经运行过，而且超过48小时没有更新");
	// flag = false;// 暂时然下载以后再考虑是否屏蔽
	// break;
	// }
	// default: {
	// break;
	// }
	// }
	//
	// // logger.debug("url:" + url + " 已经运行过，而且是正文页或者404页面");
	// }
	// // result = Integer.parseInt(results.get(0)[0].toString());
	// } catch (SQLException e) {
	// logger.debug("查询url存在状况 " + taskid + "：" + url + " 状态异常， 异常提示："
	// + e.getMessage(), e);
	// }
	// return flag;
	// }

	// public boolean isUrlVisitedAll(String url) {
	// boolean flag = false;
	// String sqlStr = "select islist from n_realtime where page_url='" + url
	// + "'";
	//
	// try {
	// List<Object[]> results = baseDao.query(sqlStr);
	// if (results.size() != 0) {
	//
	// flag = true;
	//
	// }
	// // result = Integer.parseInt(results.get(0)[0].toString());
	// } catch (SQLException e) {
	// logger.debug("查询url存在状况状态异常", e);
	// }
	// return flag;
	// }
	public boolean isUrl404(String url) {

		String sqlStr = "select 1 from n_404Urls where url=? ";
		Object[] parasValue = new Object[] { url };

		try {
			List<Object[]> results = baseDao.query(sqlStr, parasValue);
			if (results.size() != 0) {
				return true;
			}

		} catch (SQLException e) {
			logger.error("查询url存在状况 ：" + url + " 状态异常", e);

		}
		return false;
	}

	public boolean isNewsExist(String taskid, String url, long conformTextCRC) {
		boolean flag = false;
		String sqlStr = "select page_url from n_realtime where taskid="
				+ taskid + " and islist=" + SysConstants.PAGETYPE_CONTENT
				+ " and conform='" + conformTextCRC + "'";
		try {
			List<Object[]> results = baseDao.query(sqlStr);
			if (results.size() != 0) {
				flag = true;
				String simiUrl = results.get(0)[0].toString();
				logger.info("url: " + url + " 存在内容相同的新闻:" + simiUrl);
			}
		} catch (SQLException e) {
			logger.info("查询相同新闻 " + url + " 状态异常， 异常提示：" + e.getMessage(), e);
		}
		return flag;

	}

	public boolean isChanged(String taskid, String url, String webContent) {
		boolean flag = false;
		String sqlStr = "select content,trstime from n_realtime where taskid="
				+ taskid + " and page_url='" + url + "'";

		try {
			List<Object[]> results = baseDao.query(sqlStr);
			if (results.size() != 0) {
				String content = results.get(0)[0].toString();

				if (webContent.equals(content)) {
					String sqlStrCount = "select isnull(noChangeCounts,0)noChangeCounts,savetime from n_realtime where taskid="
							+ taskid + " and page_url='" + url + "'";
					List<Object[]> resultCounts = baseDao.query(sqlStrCount);

					int noChangeCount = (Integer) resultCounts.get(0)[0];
					Date lastSavetime = (Date) resultCounts.get(0)[1];
					int minuteDiff = Converter.dateDiff(Calendar.MINUTE,
							lastSavetime, new Date());
					noChangeCount += minuteDiff;
					if (noChangeCount > 2880) {
						logger.debug("url: " + url + " 内容已经" + noChangeCount
								+ "超过分钟没有改变，设置成不再下载");
						String updateStr = "update n_realtime set islist=8 where taskid="
								+ taskid + " and page_url='" + url + "'";
						baseDao.save(updateStr);
					} else {
						logger.debug("url: " + url + " 内容已经" + noChangeCount
								+ "分钟没有改变");
						String updateStr = "update n_realtime set noChangeCounts="
								+ noChangeCount
								+ " where taskid="
								+ taskid
								+ " and page_url='" + url + "'";
						baseDao.save(updateStr);

					}

				} else {
					logger.debug("url: " + url + " 内容发生改变，时间重置");
					String updateStr1 = "update n_realtime set noChangeCounts=0 where taskid="
							+ taskid + " and page_url='" + url + "'";
					baseDao.save(updateStr1);

				}
				flag = true;

			}

		} catch (SQLException e) {
			logger.debug("出现异常 " + taskid + "：" + url + " 状态异常， 异常提示："
					+ e.getMessage(), e);
		}
		return flag;
	}

	// public void save(News ne) {
	// String sqlStr = "insert n_realtime
	// (taskid,topdomain,webdomain,page_url,title,pagesize,datePublished,savetime,filename,islist,conform,ip,iparea,content,refererurl,spot_code,isHomePageUrl,author,sourceSite)
	// "
	// + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	// Object[] parasValue = new Object[] { ne.getTaskid(), ne.getTopDomain(),
	// ne.getWebDomain(), ne.getUrl(), ne.getTitle(), ne.getLength(),
	// Converter.getSqlDateFromUtil(ne.getDateline()),
	// Converter.getSqlDateFromUtil(ne.getSavetime()),
	// ne.getFilename(), ne.getIslist(), ne.getConform(), ne.getIp(),
	// ne.getIpArea(), ne.getContent(), ne.getReferUrl(),
	// ne.getSpot_code(), ne.getIsHomePageNews(), ne.getAuthor(),
	// ne.getSourceSite() };
	//
	// try {
	// baseDao.save(sqlStr, parasValue);
	// } catch (SQLException e) {
	// if (e.getMessage().indexOf("IX_n_realtime") == -1) {
	// logger.error("保存新闻数据时出错！" + ne.getUrl(), e);
	// } else {
	// if (ne.getIslist() == 0 || ne.getIslist() == 9) {
	// logger.error("注意！重复插入新闻：" + ne.getUrl(), e);
	// }
	// }
	// } catch (Exception e) {
	// logger.error("保存新闻数据时出错！" + ne.getUrl(), e);
	// }
	// }

	public void save(Collection<News> newses) {
		String sqlStr = "insert n_realtime (taskid,topdomain,webdomain,"
				+ "page_url,title,pagesize,datePublished,savetime,"
				+ "filename,islist,conform,ip,iparea,content,"
				+ "refererurl,spot_code,isHomePageUrl,author,sourceSite,"
				+ "pageUrlMD5,wordType,imgURL,columnName,documentNo,newsType,webName,ipName) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		List<Object[]> parasValues = new ArrayList<Object[]>();
		for (Iterator<News> i = newses.iterator(); i.hasNext();) {
			News ne = i.next();
			Object[] parasValue = new Object[] { ne.getTaskid(),
					ne.getTopDomain(), ne.getWebDomain(), ne.getUrl(),
					ne.getTitle(), ne.getLength(),
					Converter.getSqlDateFromUtil(ne.getDateline()),
					Converter.getSqlDateFromUtil(ne.getSavetime()),
					ne.getFilename(), ne.getIslist(), ne.getConform(),
					ne.getIp(), ne.getIpArea(), ne.getContent(),
					ne.getReferUrl(), ne.getSpot_code(),
					ne.getIsHomePageNews(), ne.getAuthor(), ne.getSourceSite(),
					MD5Utils.getMD5(ne.getUrl().getBytes()) ,
					ne.getWordType() == null?null:ne.getWordType().getValue(),
					ne.getImgUrl(),ne.getColumnName(),ne.getDocumentNo(),
					ne.getNewsType(),ne.getWebName(),ne.getIpName()};
			parasValues.add(parasValue);
		}
		try {
			baseDao.save(sqlStr, parasValues);
//			logger.info("入库成功");
		} catch (SQLException e) {
			logger.error("保存新闻数据时出错！", e);
		} catch (Exception e) {
			logger.error("保存新闻数据时出错！", e);
		}
	}

	public void save404pages(String url) {
		String sqlStr = "insert n_404Urls (url) values (?)";
		Object[] parasValue = new Object[] { url };
		try {
			baseDao.save(sqlStr, parasValue);
		} catch (SQLException e) {
			logger.error("保存404数据时出错！" + url, e);

		}

	}

//	public void saveFirstLevelListPage(String url) {
//		String sqlStr = "insert n_listUrls (url) values (?)";
//		Object[] parasValue = new Object[] { url };
//		try {
//			baseDao.save(sqlStr, parasValue);
//		} catch (SQLException e) {
//			logger.error("保存列表数据出错:" + url, e);
//
//		}
//
//	}

	public HashMap<Long, String> getConformMap(String taskid) {
		HashMap<Long, String> map = new HashMap<Long, String>();

		String sqlStr = "select conform,page_url from n_realtime where taskid="
				+ taskid + " and islist=0";
		logger.info(sqlStr);
		try {
			List<Object[]> result = baseDao.query(sqlStr);
			// logger.debug(result.size());
			for (int i = 0; i < result.size(); i++) {
				if (result.get(i)[0] != null) {
					String conform = result.get(i)[0].toString();
					String page_url = result.get(i)[1].toString();
					if (conform.length() != 0) {
						long crc = Long.parseLong(conform);
						map.put(crc, page_url);
					}
				}
			}

		} catch (Exception e) {
			logger.error("获取长句集合出现异常", e);
		}
		return map;
	}

	public static void main(String[] args) {
		long t = System.currentTimeMillis();
//		System.out
//				.println(NewsDao
//						.getInstance()
//						.isUrl404(
//								"http://admd.yam.com/AD_C/?ADID=04B4_7*45D-.--_58F_..4*B*-._*EA5&AID=6097_0*D7A-.--_7F1_..7*2*-._*4D2&LID=414F_9*E8B-.--_66F_..B*1*-._*F68&ADURL=SET&ADURLSET=http%3A%2F%2Fstore.yam.com%2Fpingfood%2Findex.php%3Faction%3Dproduct_detail%26prod_no%3DP0013000041754"));
		long t1 = System.currentTimeMillis();
//		System.out.println(t1 - t + " ms");
//		System.out
//				.println(NewsDao
//						.getInstance()
//						.isUrl404(
//								"http://admd.yam.com/AD_C/?ADID=04B4_7*45D-.--_58F_..4*B*-._*EA5&AID=6097_0*D7A-.--_7F1_..7*2*-._*4D2&LID=414F_9*E8B-.--_66F_..B*1*-._*F68&ADURL=SET&ADURLSET=http%3A%2F%2Fstore.yam.com%2Fpingfood%2Findex.php%3Faction%3Dproduct_detail%26prod_no%3DP0013000041754"));
		long t2 = System.currentTimeMillis();
//		System.out.println(t2 - t1 + " ms");
		boolean flag = NewsDao.getInstance().isUrl404("http://test1");
//		System.out.println(flag);
		long t3 = System.currentTimeMillis();
//		System.out.println(t3 - t2 + " ms");
		if (!flag) {
			NewsDao.getInstance().save404pages("http://test1");
		}
//		System.out.println(System.currentTimeMillis() - t3 + " ms");

	}

	public void saveFilter(BloomFilter<String> bf) {
		String base = ClassLoader.getSystemResource("").toString().replaceAll(
				"file:/", "")
				+ "config";
		String fileName = "";
		File f = new File(base);
		for (File o : f.listFiles()) {
			if (o.getName().matches("\\d{14}.filter")) {
				FileOutputStream ostream;
				if (o.delete()) {
					try {
						String t = new MyDate().get_yyyymmddhh24miss();
						fileName = base + "/" + t + ".filter";
						ostream = new FileOutputStream(fileName);
						ObjectOutputStream p = new ObjectOutputStream(ostream);
						p.writeObject(bf);
						p.flush();
						ostream.close();
					} catch (FileNotFoundException e) {

						logger.error("保存排重过滤器失败：", e);
					} catch (IOException e) {

						logger.error("保存排重过滤器失败：", e);
					}
					break;
				} else {
					logger.warn("序列化排重文件时，删除文件：" + o.getName() + "失败");
				}
			}
		}
		if (fileName.equals("")) {
			FileOutputStream ostream;
			try {

				String t = new MyDate().get_yyyymmddhh24miss();
				fileName = base + "/" + t + ".filter";
				ostream = new FileOutputStream(fileName);
				ObjectOutputStream p = new ObjectOutputStream(ostream);
				p.writeObject(bf);
				p.flush();
				ostream.close();

			} catch (FileNotFoundException e) {

				logger.error("保存排重过滤器失败：", e);
			} catch (IOException e) {
				logger.error("保存排重过滤器失败：", e);
			}

		}

	}

}
