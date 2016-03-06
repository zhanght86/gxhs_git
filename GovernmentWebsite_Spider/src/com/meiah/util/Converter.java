package com.meiah.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 * 基础转换类
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-08-06
 */
public class Converter {
	private static Logger logger = Logger.getLogger(Converter.class);

	/**
	 * java.sql.Date转换到java.util.Date
	 * 
	 * @param paraDate
	 * @return
	 */
	public static java.util.Date getUtilDateFromSql(java.sql.Date paraDate) {
		return new java.util.Date(paraDate.getTime());
	}

	/**
	 * 字符串转换为util的Date类型
	 * 
	 * @param dateStr
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static java.util.Date getUtilDateFromString(String dateStr,
			String dateFormat) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.parse(dateStr);
	}

	/**
	 * 字符串转换为util的Date类型，以默认类型 yyyy-MM-dd HH:mm:ss 转换
	 * 
	 * @param dateStr
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static java.util.Date getDefaultUtilDateFromString(String dateStr)
			throws ParseException {
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.parse(dateStr);
	}

	/**
	 * util的Date类型保存进数据库时需要的转换，若通过getTime方法会丢失时分秒部分
	 * 
	 * @param paraDate
	 * @return
	 * @throws ParseException
	 */
	public static java.sql.Timestamp getSqlDateFromUtil(java.util.Date paraDate) {
		if (paraDate == null)
			return null;
		String dateFormat = "yyyy-MM-dd HH:mm:ss";// 注意使用HH，24小时制
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return Timestamp.valueOf(sdf.format(paraDate));
	}

	/**
	 * util的Date类型保存进数据库时需要的转换，若通过getTime方法会丢失时分秒部分
	 * 
	 * @param paraDate
	 * @return
	 * @throws ParseException
	 */
	public static String getStringFromUtil(java.util.Date paraDate,
			String dateFormat) {
		if (paraDate == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(paraDate);
	}

	/**
	 * 增加一个时间增量后得到的时间
	 * 
	 * @param i
	 *            改变的时间部分，使用Calendar的字段表示
	 * @param k
	 *            改变的量
	 * @param d
	 *            基础时间
	 * @return
	 */
	public static java.util.Date addDate(int i, int k, java.util.Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(i, k);
		return c.getTime();
	}

	/**
	 * 增加一个时间增量后得到的时间，并保留为一个整点时间
	 * 
	 * @param i
	 *            改变的时间部分，使用Calendar的字段表示
	 * @param k
	 *            改变的量
	 * @param d
	 *            基础时间
	 * @return
	 */
	public static java.util.Date addDate_ForHour(int i, int k, java.util.Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(i, k);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/**
	 * 计算两个日期之间的差值
	 * 
	 * @param i
	 *            <ul>
	 *            <li>Calendar.SECOND</li>
	 *            <li>Calendar.MINUTE</li>
	 *            <li>Calendar.HOUR</li>
	 *            <li>Calendar.DATE</li>
	 *            </ul>
	 * @param k
	 * @param d
	 * @return
	 */
	public static int dateDiff(int i, java.util.Date k, java.util.Date d) {
		int diffnum = 0;
		int needdiff = 0;
		switch (i) {
		case Calendar.SECOND: {
			needdiff = 1000;
			break;
		}
		case Calendar.MINUTE: {
			needdiff = 60 * 1000;
			break;
		}
		case Calendar.HOUR: {
			needdiff = 60 * 60 * 1000;
			break;
		}
		case Calendar.DATE: {
			needdiff = 24 * 60 * 60 * 1000;
			break;
		}
		}
		if (needdiff != 0) {
			diffnum = (int) (d.getTime() / needdiff)
					- (int) (k.getTime() / needdiff);
			;
		}

		return diffnum;
	}

	/**
	 * 返回指定位数的double，并进行四舍五入
	 * 
	 * @param doubleValue
	 * @param scale
	 * @return
	 */
	public static Double round(Double doubleValue, int scale) {
		Double flag = null;
		String text = doubleValue.toString();
		BigDecimal bd = new BigDecimal(text).setScale(scale,
				BigDecimal.ROUND_HALF_UP);
		flag = bd.doubleValue();
		return flag;
	}

	/**
	 * 返回进行四舍五入后的整数
	 * 
	 * @param doubleValue
	 * @return
	 */
	public static int roundInt(Double doubleValue) {
		try {
			int scale = 0;
			int flag = 0;
			String text = doubleValue.toString();
			BigDecimal bd = new BigDecimal(text).setScale(scale,
					BigDecimal.ROUND_HALF_UP);
			flag = bd.intValue();
			return flag;
		} catch (RuntimeException e) {
			logger.error("format error " + doubleValue, e);
		}
		return 0;
	}

	public static void main(String[] args) {
		Calendar k1 = Calendar.getInstance();
		k1.setTime(new java.util.Date());
		k1.set(Calendar.DAY_OF_MONTH, 28);
		k1.set(Calendar.HOUR_OF_DAY, 11);
		k1.set(Calendar.MINUTE, 10);

		Calendar d1 = Calendar.getInstance();
		d1.setTime(new java.util.Date());
		java.util.Date d = new java.util.Date();
		java.util.Date k = k1.getTime();

//		System.out.println(Converter.dateDiff(Calendar.DATE, k, d));
	}
}
