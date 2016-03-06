package com.meiah.urlFilter;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.meiah.dao.BaseDao;
import com.meiah.util.BloomFilter;
import com.meiah.util.SysConstants;

public class FilterGeneratorMutiThread extends Thread {
	private Logger logger = Logger.getLogger(FilterGeneratorMutiThread.class);
	private int lastr = 0;
	private static final int delnum = 20000;
	private static AtomicInteger all = new AtomicInteger(0);
	private static BloomFilter<String> filter = null;

	FilterGeneratorMutiThread(int lastr) {
		this.lastr = lastr;
	}

	FilterGeneratorMutiThread() {

	}

	public static void main(String[] args) {
		FilterGeneratorMutiThread fg = new FilterGeneratorMutiThread();
		fg.getFilter();
	}

	@Override
	public void run() {
		logger.debug("当前线程 ： " + lastr + " 线程数 " + all + " 获取完成!!");
		long t1 = System.currentTimeMillis();
		try {
			loadUrl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		all.decrementAndGet();

		logger.debug("当前线程 ： " + lastr + " 线程数 " + all + " 退回完成!!,耗费时间："
				+ (System.currentTimeMillis() - t1) + " ms");

	}

	@SuppressWarnings("unchecked")
	public BloomFilter<String> getFilter() {
		long t2 = System.currentTimeMillis();
		int lastr = 0;
		filter = new BloomFilter<String>(
				SysConstants.BLOOMFILTER_MAXURLSIZE * 32,
				SysConstants.BLOOMFILTER_MAXURLSIZE);
		int maxID = 1;
		try {
			Object rets = new BaseDao().query("select max(id) from n_listUrls")
					.get(0)[0];
			if (rets != null)
				maxID = Integer.valueOf(rets.toString());
			else {
				logger.info("加载排重过滤器共耗费 " + (System.currentTimeMillis() - t2)
						+ " ms,当前url数目为" + filter.count());
				float cacheSize = (float) (SysConstants.BLOOMFILTER_MAXURLSIZE * 32)
						/ (8 * 1024 * 1024);
				logger.info("缓存使用大小为：" + cacheSize + "MB");

				NumberFormat formater = NumberFormat.getPercentInstance();
				formater.setMaximumFractionDigits(10);
				logger.info("hash次数为：" + filter.getK());
				logger.info("排重过滤器对于"
						+ (SysConstants.BLOOMFILTER_MAXURLSIZE / (2 * 10000))
						+ "万数据的误判概率为："
						+ formater.format(filter
								.expectedFalsePositiveProbability()));
				logger
						.info("排重过滤器当前误判概率为："
								+ formater.format(filter
										.getFalsePositiveProbability()));
				return filter;
			}
			logger.info("maxid:" + maxID);
		} catch (Exception e1) {
			logger.error("获取最大id出现异常", e1);
		}

		while (true) {
			if (lastr > maxID && all.get() == 0)
				break;
			if (lastr < maxID && all.get() < 10) {
				all.addAndGet(1);
				FilterGeneratorMutiThread fg = new FilterGeneratorMutiThread(
						lastr);
				fg.start();
				lastr = lastr + delnum;
			}
		}
		logger.info("加载排重过滤器共耗费 " + (System.currentTimeMillis() - t2)
				+ " ms,当前url数目为" + filter.count());
		float cacheSize = (float) (SysConstants.BLOOMFILTER_MAXURLSIZE * 32)
				/ (8 * 1024 * 1024);
		logger.info("缓存使用大小为：" + cacheSize + "MB");

		NumberFormat formater = NumberFormat.getPercentInstance();
		formater.setMaximumFractionDigits(10);
		logger.info("hash次数为：" + filter.getK());
		logger.info("排重过滤器对于" + (SysConstants.BLOOMFILTER_MAXURLSIZE / (10000))
				+ "万数据的误判概率为："
				+ formater.format(filter.expectedFalsePositiveProbability()));
		logger.info("排重过滤器当前误判概率为："
				+ formater.format(filter.getFalsePositiveProbability()));

		return filter;

	}

	public void loadUrl() throws SQLException {
		BaseDao dao = new BaseDao();
		String sqlStr = "select url from n_listUrls where id>? and ID<=?  ";
		Object[] parasValue = new Object[] { lastr, lastr + delnum };
		List<Object[]> taskResults;
		try {

			taskResults = dao.query(sqlStr, parasValue);
			for (int i = 0; i < taskResults.size(); i++) {

				filter.add(taskResults.get(i)[0].toString());

			}

		} catch (SQLException e) {
			logger.error("加载所有的新闻url缓存获取出现异常", e);
		}

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}
	}
}
