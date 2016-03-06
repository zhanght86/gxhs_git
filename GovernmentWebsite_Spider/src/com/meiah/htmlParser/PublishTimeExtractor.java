package com.meiah.htmlParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * @author huhb
 * @function 自动提取 网页中的发布时间
 * 
 * 
 */
public class PublishTimeExtractor {
	private static Logger logger = Logger.getLogger(PublishTimeExtractor.class);
	private static HashMap<String, String> MonthEng = new HashMap<String, String>();
	private static HashMap<String, String> MonthMa = new HashMap<String, String>();
	private static HashMap<String, String> MonthChin = new HashMap<String, String>();

	static {
		MonthEng.put("Jan".toLowerCase(), "01");
		MonthEng.put("January".toLowerCase(), "01");
		MonthEng.put("Feb".toLowerCase(), "02");
		MonthEng.put("February".toLowerCase(), "02");
		MonthEng.put("Mar".toLowerCase(), "03");
		MonthEng.put("March".toLowerCase(), "03");
		MonthEng.put("Apr".toLowerCase(), "04");
		MonthEng.put("April".toLowerCase(), "04");
		MonthEng.put("May".toLowerCase(), "05");
		MonthEng.put("June".toLowerCase(), "06");
		MonthEng.put("Jun".toLowerCase(), "06");
		MonthEng.put("July".toLowerCase(), "07");
		MonthEng.put("Jul".toLowerCase(), "07");
		MonthEng.put("Aug".toLowerCase(), "08");
		MonthEng.put("August".toLowerCase(), "08");
		MonthEng.put("Sept".toLowerCase(), "09");
		MonthEng.put("Sep".toLowerCase(), "09");
		MonthEng.put("September".toLowerCase(), "09");
		MonthEng.put("Oct".toLowerCase(), "10");
		MonthEng.put("October".toLowerCase(), "10");
		MonthEng.put("Nov".toLowerCase(), "11");
		MonthEng.put("November".toLowerCase(), "11");
		MonthEng.put("Dec".toLowerCase(), "12");
		MonthEng.put("December".toLowerCase(), "12");

		MonthMa.put("Januari".toLowerCase(), "01");
		MonthMa.put("Februari".toLowerCase(), "02");
		MonthMa.put("Mac".toLowerCase(), "03");
		MonthMa.put("April".toLowerCase(), "04");
		MonthMa.put("Mei".toLowerCase(), "05");
		MonthMa.put("Jun".toLowerCase(), "06");
		MonthMa.put("Julai".toLowerCase(), "07");
		MonthMa.put("Ogos".toLowerCase(), "08");
		MonthMa.put("September".toLowerCase(), "09");
		MonthMa.put("Okt".toLowerCase(), "10");
		MonthMa.put("Oktober".toLowerCase(), "10");
		MonthMa.put("November".toLowerCase(), "11");
		MonthMa.put("Disember".toLowerCase(), "12");

		MonthChin.put("一月".toLowerCase(), "01");
		MonthChin.put("二月".toLowerCase(), "02");
		MonthChin.put("三月".toLowerCase(), "03");
		MonthChin.put("四月".toLowerCase(), "04");
		MonthChin.put("五月".toLowerCase(), "05");
		MonthChin.put("六月".toLowerCase(), "06");
		MonthChin.put("七月".toLowerCase(), "07");
		MonthChin.put("八月".toLowerCase(), "08");
		MonthChin.put("九月".toLowerCase(), "09");
		MonthChin.put("十月".toLowerCase(), "10");
		MonthChin.put("十一月".toLowerCase(), "11");
		MonthChin.put("十二月".toLowerCase(), "12");

	}

	/**
	 * @param url
	 *            html网页url 可以为空
	 * @param webContent
	 *            html源码
	 * @param dateFormat
	 *            日期格式
	 * @param datePos1
	 *            日期位置 （日期格式、日期位置可以为空）
	 * @return 新闻发布日期，如果没有（小时、分钟，例：2011-01-28 00:00:00）消息,一般意味着抓不到日期
	 * 
	 * @常见调用 Date newsPublishedDate=PublishTimeExtractor.getNewsPublishedDate();
	 */
	public Date getNewsPublishedDate(String url, String webContent,
			String dateFormat, int datePos1) {
		String date = "";
		Date dateDublished = null;
		if (dateFormat != null && !dateFormat.equals("")) {
			date = getDateByFormat(webContent, dateFormat, datePos1);
			if (date.equals(""))
				logger.error(url + "日期格式设定错误" + dateFormat);
		} else {

			date = getDateByRegrex(webContent);
			
			if (date.equals("")) {
				date = getSpecialDate(webContent);
			} else {
				try {
					dateDublished = new SimpleDateFormat("yyyy-MM-dd HH:mm")
							.parse(date);
					Date tomorrow = new Date(
							System.currentTimeMillis() + 24 * 3600 * 1000);
					
					if (dateDublished.compareTo(tomorrow) > 0) {
						logger.debug(url
								+ " ,提取新闻日期出现错误:比系统当前时间超过一天，尝试特殊日期获取");
						date = getSpecialDate(webContent);
					}

				} catch (ParseException e) {
					
				}
			}
		}
		if (date.equals("")) {
			logger.warn(url + " ,无法获取日期，缺省设为当前日期的零点零时，请尝试设定日期格式");
			date = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
					+ " 00:00";
			try {
				dateDublished = new SimpleDateFormat("yyyy-MM-dd HH:mm")
						.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return dateDublished;
		}

		try {
			dateDublished = new SimpleDateFormat("yyyy-MM-dd HH:mm")
					.parse(date);
			Date tomorrow = new Date(
					System.currentTimeMillis() + 24 * 3600 * 1000);
			if (dateDublished.compareTo(tomorrow) > 0) {
				// 提取后的日期比系统当前日期大，意味提取错误的日期
				logger.error(url
						+ " ,提取新闻日期出现错误:比系统当前时间超过一天，缺省设为当前日期的零点零时，请尝试设定日期格式");
				date = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
						+ " 00:00";
				dateDublished = new SimpleDateFormat("yyyy-MM-dd HH:mm")
						.parse(date);
			}
			Calendar c = Calendar.getInstance();
			c.setTime(dateDublished);
			if ((c.get(Calendar.YEAR) - 2000) > 30) {
				// 提取后的日期太早，可以檢查
				logger.warn(url + " ,当前抓取日期与2000年相比，相差超过大，检查！");
			}

		} catch (ParseException e) {
			logger.error(url + " ,提取新闻日期出现错误，缺省设为当前日期的零点零时，请尝试设定日期格式");
		}
		return dateDublished;
	}

	/**
	 * 提取新闻的发布时间，如果有设置模板，则用模板进行匹配
	 * 
	 * @param webContent
	 * @param dateFormat
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getDateByFormat(String webContent, String dateFormat,
			int datePos1) {
		String datePublished = "";

		try {
			String datePattern = dateFormat; // ���ڸ�ʽ

			int datePos = datePos1;
			if (datePos <= 0)
				datePos = 1;
			// int dateSeq = 0;

			int indexFind = 0;
			int indexOfYYYY = -1, indexOfYY = -1, indexOfMM = -1, indexOfDD = -1, indexOfHH = -1, indexOfMI = -1, indexOfSS = -1;
			indexOfYYYY = datePattern.indexOf("yyyy");
			indexOfYY = datePattern.indexOf("yy");
			indexOfMM = datePattern.indexOf("mm");
			indexOfDD = datePattern.indexOf("dd");
			indexOfHH = datePattern.indexOf("hh");
			indexOfMI = datePattern.indexOf("mi");
			indexOfSS = datePattern.indexOf("ss");

			Hashtable charHash = new Hashtable();

			if (indexOfYYYY != -1) {
				datePattern = datePattern.replaceAll("yyyy", "[0-9]{4}");
				charHash.put(new Integer(indexOfYYYY), "yyyy");
			} else if (indexOfYY != -1) {
				datePattern = datePattern.replaceAll("yy", "[0-9]{2}");
				charHash.put(new Integer(indexOfYY), "yy");
			}
			if (indexOfMM != -1) {
				datePattern = datePattern.replaceAll("mm", "[0-9]{1,2}+");
				charHash.put(new Integer(indexOfMM), "mm");
			}
			if (indexOfDD != -1) {
				datePattern = datePattern.replaceAll("dd", "[0-9]{1,2}+");
				charHash.put(new Integer(indexOfDD), "dd");
			}
			if (indexOfHH != -1) {
				datePattern = datePattern.replaceAll("hh", "[0-9]{1,2}+");
				charHash.put(new Integer(indexOfHH), "hh");
			}
			if (indexOfMI != -1) {
				datePattern = datePattern.replaceAll("mi", "[0-9]{1,2}+");
				charHash.put(new Integer(indexOfMI), "mi");
			}
			if (indexOfSS != -1) {
				datePattern = datePattern.replaceAll("ss", "[0-9]{1,2}+");
				charHash.put(new Integer(indexOfSS), "ss");
			}
			// datePattern = datePattern.replaceAll("#", "\\D{1}");

			webContent = this.getPlainText(webContent);
			Matcher m = Pattern.compile(datePattern).matcher(webContent);
			// logger.debug(webContent);
			while (m.find()) {
				indexFind++;
				if (indexFind == datePos) {// ��dateTimes�γ��ָ�����
					int indexFrom = m.start();
					int indexEnd = m.end();
					String d = webContent.substring(indexFrom, indexEnd);
					// System.out.println(d);
					// Calendar currentTime = Calendar.getInstance();
					// currentTime.setTime(new Date());

					int yyyy = 0, mm = 0, dd = 0, hh = 8, mi = 0, ss = 0;
					int indexOfSub = 0;
					while (true) {
						int minKey = 100000;
						for (Enumeration e = charHash.keys(); e
								.hasMoreElements();) {
							Integer ikey = (Integer) e.nextElement();
							minKey = Math.min(minKey, ikey.intValue());
						}
						if (minKey == 100000)
							break;
						String typeKey = (String) charHash.get(new Integer(
								minKey));
						charHash.remove(new Integer(minKey));
						// System.out.println(typeKey);
						minKey = minKey - indexOfSub;
						if (typeKey.equals("yyyy"))
							yyyy = Integer.parseInt(d.substring(minKey,
									minKey + 4));
						else if (typeKey.equals("yy"))
							yyyy = Integer.parseInt("20"
									+ d.substring(minKey, minKey + 2));
						else {
							int unknowValue = 0;
							try {
								unknowValue = Integer.parseInt(d.substring(
										minKey, minKey + 2));
							} catch (Throwable e) {
								unknowValue = Integer.parseInt(d.substring(
										minKey, minKey + 1));
								indexOfSub++;
							}
							if (typeKey.equals("mm"))
								mm = unknowValue;
							else if (typeKey.equals("dd"))
								dd = unknowValue;
							else if (typeKey.equals("hh"))
								hh = unknowValue;
							else if (typeKey.equals("mi"))
								mi = unknowValue;
							else if (typeKey.equals("ss"))
								ss = unknowValue;
						}
					} // while����
					if (yyyy == 0) {
						Calendar c = Calendar.getInstance();
						yyyy = c.get(Calendar.YEAR); // 
						if (mm > (c.get(Calendar.MONTH) + 1)) {
							yyyy--;
						} else if (mm == (c.get(Calendar.MONTH) + 1)
								&& dd > c.get(Calendar.DAY_OF_MONTH)) {
							yyyy--;
						}

					}
					if (mm >= 1 && mm <= 12 && dd >= 1 && dd <= 31
							&& yyyy < 2100) {
						datePublished = yyyy + "-"
								+ (mm < 10 ? "0" + mm : "" + mm) + "-"
								+ (dd < 10 ? "0" + dd : "" + dd) + " "
								+ (hh < 10 ? "0" + hh : "" + hh) + ":"
								+ (mi < 10 ? "0" + mi : "" + mi) + ":"
								+ (ss < 10 ? "0" + ss : "" + ss);

					}

					break;
				} // if
			} // while
			m = null;
			charHash = null;
		} catch (Throwable e) {
			logger.error("提取日期格式出现异常", e);
		}
		// logger.info(dateFormat + ": " + datePublished);
		return datePublished;
	}

	/**
	 * 获取html页面源代码 去除标记后的文本
	 * 
	 * @param webSourcePage
	 * @return
	 */
	private String getPlainText(String webSourcePage) {
		String target = webSourcePage.replaceAll("(?i)(?s)<style.*?</style>",
				"").replaceAll("(?i)(?s)<(no)?script.*?</(no)?script>", "")
				.replaceAll("(?i)(?s)<select.*?</select>", "").replaceAll(
						"(?i)(?s)<!--.*?-->", "");
		target = webSourcePage.replaceAll("(?s)(?i)<.*?>", " ");
		target = target.replaceAll("&nbsp;?", " ").replaceAll("[ ]{2,}", " ")
				.replaceAll("\\s+", " ");
		// target = target.replaceAll("\\s+", " ");
		return target;
	}

	private String[] getHHMM(String str) {
		String[] hhmm = null;
		Matcher m = Pattern.compile("\\D(\\d{1,2})[:时時](\\d{1,2})\\D").matcher(str);
		if (m.find()) {
			String hh = m.group(1);
			int h = 0;
			try {
				h = Integer.parseInt(hh);
				if (str.indexOf("pm") != -1 && h < 12) {
					h += 12;
				}
				if (h < 10) {
					hh = "0" + h;
				}
				hh = "" + h;
			} catch (Exception e) {

			}

			String ss = m.group(2);
			ss = ss.length() == 1 ? "0" + ss : ss;
			hhmm = new String[] { hh, ss };
		}

		return hhmm;
	}

	/**
	 * 获取html页面源代码 去除标记后的文本
	 * 
	 * @param webSourcePage
	 * @return
	 */
	public String getSpecialDate(String webContent) {
		String date = "", year = "", month = "", day = "", hh = "08", mm = "00";

		webContent = getPlainText(webContent);
		webContent = webContent.toLowerCase();
		int lenth = webContent.length();
		Pattern pYear = Pattern.compile("[\\D](20\\d{2})[\\D]");
		Matcher mYear = pYear.matcher(webContent);
		String monthRegex = getMonthPattern();
		Pattern pMonth = Pattern.compile(monthRegex);
		int k = 1;
		while (mYear.find()) {
			year = mYear.group(1);
			int YearStart = mYear.start();
			int preYearStart = YearStart - 15 < 0 ? 0 : YearStart - 15;
			int YearEnd = mYear.end();
			int afterYearEnd = YearEnd + 15 > lenth ? lenth : YearEnd + 15;
			String preYearStr = webContent.substring(preYearStart,
					YearStart + 1);

			String afterYearStr = webContent.substring(YearEnd - 1,
					afterYearEnd);

			Matcher preMonthMatcher = pMonth.matcher(preYearStr);
			Matcher afterMonthMatcher = pMonth.matcher(afterYearStr);
			if (preMonthMatcher.find()) {
				month = preMonthMatcher.group().replaceAll("[于 \\(　,\\.]", "");
				month = MonthEng.get(month) != null ? MonthEng.get(month)
						: MonthChin.get(month) != null ? MonthChin.get(month)
								: MonthMa.get(month);
				int MonthStart = preMonthMatcher.start();
				int preMonthStart = MonthStart - 5 < 0 ? 0 : MonthStart - 5;
				int MonthEnd = preMonthMatcher.end();
				int afterMonthEnd = MonthEnd + 5 > preYearStr.length() ? preYearStr
						.length()
						: MonthEnd + 5;
				String preMonthStr = preYearStr.substring(preMonthStart,
						MonthStart + 1);

				String afterMonthStr = preYearStr.substring(MonthEnd - 1,
						afterMonthEnd);

				if (preMonthStr.matches(".*?([012]?\\d)\\D{1,3}")) {
					Matcher m1 = Pattern.compile(".*?([012]?\\d)\\D").matcher(
							preMonthStr);
					m1.find();
					day = m1.group(1);
					day = day.length() == 1 ? "0" + day : day;
				} else if (afterMonthStr.matches("\\D{1,2}([012]?\\d).*")) {
					Matcher m1 = Pattern.compile("\\D{1,2}([012]?\\d).*").matcher(
							afterMonthStr);
					m1.find();
					day = m1.group(1);
					day = day.length() == 1 ? "0" + day : day;

				} else {
					continue;
				}

			} else if (afterMonthMatcher.find()) {
				month = afterMonthMatcher.group().replaceAll("[于 \\(　]", "");
				month = MonthEng.get(month) != null ? MonthEng.get(month)
						: MonthChin.get(month) != null ? MonthChin.get(month)
								: MonthMa.get(month);
				int MonthStart = afterMonthMatcher.start();
				int preMonthStart = MonthStart - 5 < 0 ? 0 : MonthStart - 5;
				int MonthEnd = afterMonthMatcher.end();
				int afterMonthEnd = MonthEnd + 5 > afterYearStr.length() ? afterYearStr
						.length()
						: MonthEnd + 5;
				String preMonthStr = afterYearStr.substring(preMonthStart,
						MonthStart + 1);

				String afterMonthStr = afterYearStr.substring(MonthEnd - 1,
						afterMonthEnd);

				if (preMonthStr.matches(".*?([012]?\\d)\\D")) {
					Matcher m1 = Pattern.compile(".*?([012]?\\d)\\D").matcher(
							preMonthStr);
					m1.find();
					day = m1.group(1);
					day = day.length() == 1 ? "0" + day : day;

				} else if (afterMonthStr.matches("\\D([012]?\\d).*?")) {
					Matcher m1 = Pattern.compile("\\D([012]?\\d).*?").matcher(
							afterMonthStr);
					m1.find();
					day = m1.group(1);
					day = day.length() == 1 ? "0" + day : day;

				} else {
					continue;
				}

			} else {
				continue;
			}
			String[] hhmm = getHHMM(preYearStr);
			if (hhmm != null) {
				hh = hhmm[0];
				mm = hhmm[1];
				date = year + "-" + month + "-" + day + " " + hh + ":" + mm;

				return date;
			} else {
				hhmm = getHHMM(afterYearStr);
				if (hhmm != null) {
					hh = hhmm[0];
					mm = hhmm[1];
					date = year + "-" + month + "-" + day + " " + hh + ":" + mm;

					return date;
				}
			}
			if (!month.equals("") && !day.equals("") && k == 1)
				date = year + "-" + month + "-" + day + " " + hh + ":" + mm;
			k++;

		}

		return date;
	}

	/**
	 * 获取html页面源代码 去除标记后的文本
	 * 
	 * @param webSourcePage
	 * @return
	 */
	public String getDateByRegrex(String webContent) {
		String date = "", year = "", month = "", day = "", hh = "08", mm = "00";
		webContent = getPlainText(webContent);

		webContent = webContent.toLowerCase();
		int lenth = webContent.length();
		Pattern pYear = Pattern.compile("[\\D](20\\d{2})[\\D]");
		Matcher mYear = pYear.matcher(webContent);
		Pattern pMonthDayPost = Pattern
				.compile("\\D?([0,1]?\\d)([\\D&&[^:]]{1,2})([0,1,2,3]?\\d)((日(?=(\\d{1,2}[:时時]\\d{1,2})))|(日[\\s　]|日消息|讯|日讯|\\]|\\)|）)|[\\s　,])");
		Pattern pMonthDayPre = Pattern
				.compile("\\D?([0,1]?\\d)[\\D&&[^:]]{1,2}([0,1,2,3]?\\d)\\D");

		int k = 1;
		while (mYear.find()) {
			year = mYear.group(1);
			int dateEnd = 0;
			int YearStart = mYear.start();
			int preYearStart = YearStart - 7 < 0 ? 0 : YearStart - 7;
			int YearEnd = mYear.end();
			int afterYearEnd = YearEnd + 10 > lenth ? lenth : YearEnd + 10;
			String preYearStr = webContent.substring(preYearStart,
					YearStart + 1);

			String afterYearStr = webContent.substring(YearEnd - 1,
					afterYearEnd);

			Matcher preMonthMatcher = pMonthDayPre.matcher(preYearStr);
			Matcher afterMonthMatcher = pMonthDayPost.matcher(afterYearStr);
			if (afterMonthMatcher.find()) {
				// String temp = afterMonthMatcher.group();
				String seprator = afterMonthMatcher.group(2);
				if (seprator.replaceAll("[ 　]", "").length() == 0)
					continue;
				month = afterMonthMatcher.group(1);
				month = month.length() == 1 ? "0" + month : month;
				day = afterMonthMatcher.group(3);
				day = day.length() == 1 ? "0" + day : day;
				dateEnd = YearEnd + afterMonthMatcher.end() - 1;
			} else if (preMonthMatcher.find()) {
				month = preMonthMatcher.group(1);
				month = month.length() == 1 ? "0" + month : month;
				day = preMonthMatcher.group(2);
				day = day.length() == 1 ? "0" + day : day;
				dateEnd = YearEnd;
				if (Integer.parseInt(month) > 12) {
					String temp = month;
					month = day;
					day = temp;
				}
			} else {
				continue;
			}
			int afterDateEnd = dateEnd + 12 > webContent.length() ? webContent
					.length() : dateEnd + 12;
			String afterDateStr = webContent.substring(dateEnd - 1,
					afterDateEnd);

			String[] hhmm = getHHMM(afterDateStr);
			if (hhmm != null) {
				hh = hhmm[0];
				mm = hhmm[1];
				date = year + "-" + month + "-" + day + " " + hh + ":" + mm;

				return date;

			}
			if (!month.equals("") && !day.equals("") && k == 1)
				date = year + "-" + month + "-" + day + " " + hh + ":" + mm;
			k++;

		}

		return date;
	}

	private String getMonthPattern() {
		String pattern = "";
		Iterator<String> it = MonthEng.keySet().iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			String key = it.next();
			sb.append("[于 \\(　]" + key + "[ \\)　,\\.]|");
		}
		Iterator<String> it１ = MonthChin.keySet().iterator();
		while (it１.hasNext()) {
			String key = it１.next();
			sb.append("[于 \\(　]" + key + "[ \\)　,]|");
		}
		Iterator<String> it2 = MonthMa.keySet().iterator();
		while (it2.hasNext()) {
			String key = it2.next();
			sb.append("[于 \\(　]" + key + "[ \\)　,]|");
		}
		pattern = sb.toString();
		pattern = pattern.substring(0, pattern.length() - 1);
		return pattern;

	}
	
	//转化英文月份
	public String getMonth(String month){
		month = MonthEng.get(month) != null ? MonthEng.get(month) : MonthChin.get(month) != null ? MonthChin.get(month) : MonthMa.get(month);
		return month;
	}

	//
	// static class NewsDate implements Comparable<NewsDate> {
	//
	// public int compareTo(NewsDate o) {
	//
	// if (this.getLength() > o.getLength())
	// return 1;
	// else if (this.getLength() < o.getLength())
	// return 0;
	// else
	// return -1;
	// }
	//
	// private String date;
	// private int length;
	//
	// public String getDate() {
	// return date;
	// }
	//
	// public void setDate(String date) {
	// this.date = date;
	// }
	//
	// public int getLength() {
	// return length;
	// }
	//
	// public void setLength(int length) {
	// this.length = length;
	// }

	// }

}
