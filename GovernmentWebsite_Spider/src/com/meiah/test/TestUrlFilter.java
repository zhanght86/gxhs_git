package com.meiah.test;

import java.sql.SQLException;
import java.util.List;

import com.meiah.dao.BaseDao;
import com.meiah.webCrawlers.ClientCenter;

public class TestUrlFilter {

	/**
	 * @param
	 */
	public static void main(String[] args) {

		String sqlStr = "select page_url from n_realtime where DATEDIFF(HH,Savetime,GETDATE())=0 ";

		List<Object[]> taskResults;
		int exist = 0, notExist = 0;
		try {
			taskResults = new BaseDao().query(sqlStr);

			for (int i = 0; i < taskResults.size(); i++) {
				String url = taskResults.get(i)[0].toString();
				if (ClientCenter.isNewsExist(url)) {
//					System.out.println(url + "     exist");
					exist++;
				} else {
//					System.out.println(url + "    not exist");
					notExist++;
				}
			}
//			System.out.println("exist:" + exist + ";  notExist:" + notExist);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
