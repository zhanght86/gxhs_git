package com.meiah.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateUtil2 {
	// ---------------------获取日期--------------------------------
	public static String formatDate() {
		return (formatDate(now(), "yyyy-MM-dd"));
	}

	public static String formatDate(java.util.Date date) {
		return (formatDate(date, "yyyy-MM-dd"));
	}

	public static String formatDate(java.util.Date date, String pattern) {
		if (date == null)
			date = now();
		if (pattern == null)
			pattern = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return (sdf.format(date));
	}

	// -----------------------------------------------------

	// ---------------------获取日期时间--------------------------------
	public static String formatDateTime() {
		return (formatDate(now(), "yyyy-MM-dd HH:mm:ss"));
	}

	public static String formatDateTime(java.util.Date date) {
		return (formatDate(date, "yyyy-MM-dd HH:mm:ss"));
	}

	// -----------------------------------------------------

	// ---------------------获取时间--------------------------------
	public static String formatTime() {
		return (formatDate(now(), "HH:mm:ss"));
	}

	public static String formatTime(java.util.Date date) {
		return (formatDate(date, "HH:mm:ss"));
	}

	// -----------------------------------------------------

	// ---------------------当前时间--------------------------------
	public static String now(String pattern) {
		return (formatDate(now(), pattern));
	}
	public static java.util.Date now() {
		return (new java.util.Date());
	}

	public static java.util.Date nowDate() {
		return parseDate(formatDate());
	}

	public static java.util.Date nowDateTime() {
		return parseDateTime(formatDateTime());
	}

	// -----------------------------------------------------

	// -----------------------从字符获取util日期-------------------------------
	public static java.util.Date parseDateTime(String datetime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if ((datetime == null) || (datetime.equals(""))) {
			return null;
		} else {
			try {
				return formatter.parse(datetime);
			} catch (ParseException e) {
				return parseDate(datetime);
			}
		}
	}

	public static java.util.Date parseDate(String date) {
		return parseDate(date, "yyyy-MM-dd");
	}

	public static java.util.Date parseDate(String date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);

		if ((date == null) || (date.equals(""))) {
			return null;
		} else {
			try {
				return formatter.parse(date);
			} catch (ParseException e) {
				return null;
			}
		}
	}

	// -----------------------------------------------------

	// -----------------------从util日期获取util日期-------------------------------
	public static java.util.Date parseDate(java.util.Date datetime) {
		return parseDate(datetime, "yyyy-MM-dd");
	}

	public static java.util.Date parseDate(java.util.Date datetime,
			String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);

		if (datetime == null) {
			return null;
		} else {
			try {
				return formatter.parse(formatter.format(datetime));
			} catch (ParseException e) {
				return null;
			}
		}
	}

	// -----------------------------------------------------

	// -----------------------从未指定类型中猜测并返回日期字符串-------------------------------
	public static String formatDate(Object o) {
		if (o == null)
			return "";
		if (o.getClass() == String.class)
			return formatDate((String) o);
		else if (o.getClass() == java.util.Date.class)
			return formatDate((java.util.Date) o);
		else if (o.getClass() == Timestamp.class) {
			return formatDate(new java.util.Date(((Timestamp) o).getTime()));
		} else
			return o.toString();
	}

	public static String formatDateTime(Object o) {
		if (o.getClass() == String.class)
			return formatDateTime((String) o);
		else if (o.getClass() == java.util.Date.class)
			return formatDateTime((java.util.Date) o);
		else if (o.getClass() == Timestamp.class) {
			return formatDateTime(new java.util.Date(((Timestamp) o).getTime()));
		} else
			return o.toString();
	}

	// -----------------------------------------------------

	// -----------------------日期计算------------------------------
	public static java.util.Date add(java.util.Date date, int field, int amount) {
		if (date == null) {
			date = new java.util.Date();
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);

		return cal.getTime();
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
	public static int diff(int i, java.util.Date k, java.util.Date d) {
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

	public static java.util.Date addMilliSecond(java.util.Date date, int amount) {
		return add(date, Calendar.MILLISECOND, amount);
	}

	public static java.util.Date addSecond(java.util.Date date, int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	public static java.util.Date addMiunte(java.util.Date date, int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	public static java.util.Date addHour(java.util.Date date, int amount) {
		return add(date, Calendar.HOUR, amount);
	}

	public static java.util.Date addDay(java.util.Date date, int amount) {
		return add(date, Calendar.DATE, amount);
	}

	public static java.util.Date addMonth(java.util.Date date, int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	public static java.util.Date addYear(java.util.Date date, int amount) {
		return add(date, Calendar.YEAR, amount);
	}

	// -----------------------------------------------------

	// -----------------------获取指定日期------------------------------
	public static java.util.Date getLastDateByMonth() {
		return getLastDateByMonth(new java.util.Date());
	}

	public static java.util.Date getLastDateByMonth(java.util.Date d) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.MONTH, now.get(Calendar.MONTH) + 1);
		now.set(Calendar.DATE, 1);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);
		now.set(Calendar.HOUR, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		return now.getTime();
	}

	public static java.util.Date getFirstDateByMonth() {
		return getFirstDateByMonth(new java.util.Date());
	}

	public static java.util.Date getFirstDateByMonth(java.util.Date d) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, 1);
		now.set(Calendar.HOUR, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		return now.getTime();
	}

	public static java.util.Date getWeekDay(java.util.Date date, int k) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, k);
		return c.getTime();
	}
	// -----------------------------------------------------

	// -----------------------获取指定日期或时间区间------------------------------
	public static List<String> getDateZone(String fromdate, String todate,
			String format) {
		List<String> dateZone = new ArrayList<String>();

		long timediff = 1000 * 60 * 60 * 24;
		dateZone.add(fromdate);
		java.util.Date nowdate = null;
		java.util.Date fd = parseDate(fromdate, format);
		java.util.Date td = parseDate(todate, format);
		nowdate = new java.util.Date(fd.getTime() + timediff);

		while (nowdate.getTime() < td.getTime()) {
			dateZone.add(formatDate(nowdate, format));
			nowdate = new java.util.Date(nowdate.getTime() + timediff);
		}
		dateZone.add(todate);

		return dateZone;
	}
	
	public static List<String> getHouseZone(String fromdate, String todate,
			String format) {
		List<String> dateZone = new ArrayList<String>();

		long timediff = 1000 * 60 * 60 * 4;
		dateZone.add(fromdate);
		SimpleDateFormat sf = new SimpleDateFormat(format);
		java.util.Date nowdate = null;
		try {
			java.util.Date fd = sf.parse(fromdate);
			java.util.Date td = sf.parse(todate);
			nowdate = new java.util.Date(fd.getTime() + timediff);

			while (nowdate.getTime() < td.getTime()) {
				dateZone.add(sf.format(nowdate));
				nowdate = new java.util.Date(nowdate.getTime() + timediff);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		dateZone.add(todate);

		return dateZone;
	}
	// -----------------------------------------------------

	// ----------------------日期类型间的转换------------------------------
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
	// -----------------------------------------------------
	public static void main(String[] args) {
		// List k=getDateZone("2009-08-01","2009-08-10","yyyy-MM-dd");
		// for(int i=0;i<k.size();i++)
		// System.out.println(k.get(i));
	}
}
