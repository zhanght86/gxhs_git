package com.meiah.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meiah.po.KeyWord;

/**
 * 
 * @author hepl
 * 
 */
public class KeyWordDao {

	private Logger logger = Logger.getLogger(KeyWordDao.class);
	private BaseDao baseDao = new BaseDao();

	private static KeyWordDao dao;

	private KeyWordDao() {
	}

	public static KeyWordDao getInstance() {
		if (dao == null)
			dao = new KeyWordDao();

		return dao;
	}

	/**
	 * 加载数据库中的关键词
	 * 
	 * @param taskid
	 * @return
	 */
	public List<KeyWord> getKeyWord() {
		String sqlStr = "select a.id,a.name,a.sortid,a.keywords,a.keywordsmust,a.score from n_basekeyword a ,n_basekwsort b where a.sortid=b.id and 1=1"
				+ "order by b.weight asc";
		List<KeyWord> words = new ArrayList<KeyWord>();
		List<Object[]> forumResults;
		try {
			forumResults = baseDao.query(sqlStr);

			for (int i = 0; i < forumResults.size(); i++) {
				KeyWord sw = new KeyWord();
				sw.setId((Integer) forumResults.get(i)[0]);
				sw.setName(forumResults.get(i)[1].toString());
				sw.setSortid((Integer) forumResults.get(i)[2]);
				sw.setKeywords(forumResults.get(i)[3] == null ? ""
						: forumResults.get(i)[3].toString());
				sw.setKeywordsmust(forumResults.get(i)[4] == null ? ""
						: forumResults.get(i)[4].toString());
				sw.setScore(((Double) forumResults.get(i)[5]).floatValue());
				words.add(sw);
			}
			// logger.info("加载关键词完成----hepl"+words.size());
		} catch (Exception e) {
			logger.error("获取关键词列表异常", e);
		}
		return words;
	}

	public static void main(String[] args) {
//		System.out.println(KeyWordDao.getInstance().getKeyWord().size());
	}
}
