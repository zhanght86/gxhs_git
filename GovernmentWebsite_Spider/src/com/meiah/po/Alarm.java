package com.meiah.po;

import java.util.Date;

/**
 * 报警实体类。包含验证报警的方法。
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-08-10
 * 
 */
public class Alarm {
	private int alermid;
	private String alermname;
	private String alermexp;
	private int alarmType;
	private int userId;

	private String taskid;
	private String TopDomain;
	private String WebDomain;
	private String page_url;
	private String title;
	private int pageSize;
	private String userName;
	private Date datePublished;
	private Date Savetime;
	private String page_type;
	private String Filename;
	private String spot_code;
	private String source_type;
	private int islist;
	private String Conform;
	private String IP;
	private String IPArea;
	private String content;

	// private String alermbelong;

	public int getAlermid() {
		return alermid;
	}

	public void setAlermid(int alermid) {
		this.alermid = alermid;
	}

	public String getAlermname() {
		return alermname;
	}

	public void setAlermname(String alermname) {
		this.alermname = alermname;
	}

	public String getAlermexp() {
		return alermexp;
	}

	public void setAlermexp(String alermexp) {
		this.alermexp = alermexp;
	}

	// public String getAlermbelong() {
	// return alermbelong;
	// }
	// public void setAlermbelong(String alermbelong) {
	// this.alermbelong = alermbelong;
	// }
	/**
	 * 自身检查是否报警方法
	 * 
	 * @param content
	 * @return
	 */
	public boolean isAlerm(String content) {
		String exps = fixExp(alermexp);// 所有关键字
		if (content == null || content.trim().length() == 0)
			return false;
		return expAlarm(exps, content);
	}

	private String fixExp(String exp) {
		// 判断表达式是否正确，只能包含一重括号 只判断一次
		boolean fix = true;
		int iCount = 0;
		int length = exp.length();
		for (int i = 0; i < length; i++) {
			char cc = exp.charAt(i);
			if (cc == '(')
				iCount++;
			else if (cc == ')')
				iCount--;

			if (iCount < 0 || iCount > 1) {
				fix = false;
				break;
			}
		}

		// 表达式错误
		if (!fix || iCount != 0) {
			return "";
		}

		exp = exp.replaceAll(" ", "");// 半角空格
		exp = exp.replaceAll("　", "");// 全角空格

		exp = exp.replaceAll("[\\|\\+,\\-,\\^,']", "");// 去掉特殊字符

		exp = exp.replaceAll("(?i)and", "|'+");
		exp = exp.replaceAll("(?i)or", "|'-");
		exp = exp.replaceAll("(?i)not", "|'^");

		if (!exp.startsWith("|"))
			exp = "|'+" + exp;

		String exp1 = "";
		int i1 = 0;
		int i2 = 0;
		length = exp.length();

		for (int i = 0; i < length; i++) {
			char cc = exp.charAt(i);
			if (cc == '(')
				iCount++;

			if (iCount == 1) {
				i2 = exp.indexOf(")", i);
				String s1 = exp.substring(i1, i + 1);
				String s2 = exp.substring(i + 1, i2);
				s2 = s2.replaceAll("\\|'", "~");
				exp1 = exp1 + s1 + s2;
				i1 = i2;
				i = i2 + 1;
				iCount = 0;
			}
		}

		exp1 = exp1 + exp.substring(i2);
		exp = exp1;

		// 将括号里面的分隔符合括号外面分开,以便将括号当成一个整体 |'+(搜索~+技巧~^你好)|'+基本|'^合适

		String[] expL = exp.split("\\|");

		String NOT = "", AND = "", OR = "";

		for (int i = 0; i < expL.length; i++) {
			if (expL[i].startsWith("'+")) {
				AND = AND + expL[i];
			} else if (expL[i].startsWith("'^")) {
				NOT = NOT + expL[i];
			} else if (expL[i].startsWith("'-")) {
				OR = OR + expL[i];
			}
		}

		exp = NOT + OR + AND;

		return exp;

	}

	private boolean expAlarm(String exp, String text) {
		if (exp.equals(""))
			return false;
		exp = exp.substring(1);
		String[] expL = exp.split("'");

		for (int i = 0; i < expL.length; i++) {// 优先级，一不符合，就退出，最后不符合退出的条件，则为真
			if (expL[i].startsWith("^")) {// 匹配 NOT
				if (aExpAlarm(expL[i], text))
					return false;
			} else if (expL[i].startsWith("-")) {
				if (aExpAlarm(expL[i], text))
					return true;
			} else if (expL[i].startsWith("+")) {
				if (!aExpAlarm(expL[i], text))
					return false;
			} else {
				return false;
			}
		}
		return true;
	}

	// 判断单个 正则表达式 或者字符串
	private boolean aExpAlarm(String exp, String text) {
		boolean cat = false;

		exp = exp.substring(1);
		if (exp.indexOf("(") != -1 && exp.indexOf("*") != -1) {
			if (text.indexOf(exp) != -1)
				cat = true;
		} else if (exp.indexOf("(") != -1) {
			exp = exp.substring(1, exp.length() - 1);// 包含括号

			// /////////////////////重新正规化
			String[] expL = exp.split("~");

			String NOT = "", AND = "", OR = "";

			for (int i = 0; i < expL.length; i++) {
				if (expL[i].startsWith("+")) {
					AND = AND + "'" + expL[i];
				} else if (expL[i].startsWith("^")) {
					NOT = NOT + "'" + expL[i];
				} else if (expL[i].startsWith("-")) {
					OR = OR + "'" + expL[i];
				} else {
					AND = AND + "'+" + expL[i];
				}
			}

			exp = NOT + OR + AND;
			return (expAlarm(exp, text));

		} else {
			exp = exp.replaceAll("\\*", "\\.");
			if (text.matches("(?s).*" + exp + ".*"))
				// if (JavaUtil.isAllMatch(text,".*" + exp + ".*"))
				cat = true;
		}
		return cat;
	}

	public int getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getTopDomain() {
		return TopDomain;
	}

	public void setTopDomain(String topDomain) {
		TopDomain = topDomain;
	}

	public String getWebDomain() {
		return WebDomain;
	}

	public void setWebDomain(String webDomain) {
		WebDomain = webDomain;
	}

	public String getPage_url() {
		return page_url;
	}

	public void setPage_url(String page_url) {
		this.page_url = page_url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}

	public Date getSavetime() {
		return Savetime;
	}

	public void setSavetime(Date savetime) {
		Savetime = savetime;
	}

	public String getPage_type() {
		return page_type;
	}

	public void setPage_type(String page_type) {
		this.page_type = page_type;
	}

	public String getFilename() {
		return Filename;
	}

	public void setFilename(String filename) {
		Filename = filename;
	}

	public String getSpot_code() {
		return spot_code;
	}

	public void setSpot_code(String spot_code) {
		this.spot_code = spot_code;
	}

	public String getSource_type() {
		return source_type;
	}

	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}

	public int getIslist() {
		return islist;
	}

	public void setIslist(int islist) {
		this.islist = islist;
	}

	public String getConform() {
		return Conform;
	}

	public void setConform(String conform) {
		Conform = conform;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String ip) {
		IP = ip;
	}

	public String getIPArea() {
		return IPArea;
	}

	public void setIPArea(String area) {
		IPArea = area;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
