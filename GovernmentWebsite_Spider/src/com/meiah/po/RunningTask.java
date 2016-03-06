package com.meiah.po;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 运行任务实体类，继承自任务实体类。每隔5秒重新计算一次下载数据
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-08-10
 * 
 */
public class RunningTask extends Task {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(RunningTask.class);
	private Date starttime;
	private String nowurl;
	private int runstate;
	private int downloadPages;
	private int SavePages;
	private int listPages;
	private int contentPages;
	private int unknownPages;
	private int threadnums;
	private double maxspeed;
	private double minspeed;
	private double avgspeed;

	private int alltime;// 执行总计时间
	private int lastdpages;
	private int nondtimes;
	private int nowspeed;
	private String runip;

	public String getRunip() {
		return runip;
	}

	public void setRunip(String runip) {
		this.runip = runip;
	}

	public void resetCounts() {
		alltime = 0;
		downloadPages = 0;
		SavePages = 0;
		listPages = 0;
		contentPages = 0;
		unknownPages = 0;
		// sposts=0;
		nondtimes = 0;
	}

	public int getNondtimes() {
		return nondtimes;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public String getNowurl() {
		return nowurl;
	}

	public void setNowurl(String nowurl) {
		this.nowurl = nowurl;
	}

	public int getRunstate() {
		return runstate;
	}

	public void setRunstate(int runstate) {
		this.runstate = runstate;
	}

	public int getThreadnums() {
		return threadnums;
	}

	public void setThreadnums(int threadnums) {
		this.threadnums = threadnums;
	}

	public double getMaxspeed() {
		return maxspeed;
	}

	public void setMaxspeed(double maxspeed) {
		this.maxspeed = maxspeed;
	}

	public double getMinspeed() {
		return minspeed;
	}

	public void setMinspeed(double minspeed) {
		this.minspeed = minspeed;
	}

	public double getAvgspeed() {
		return avgspeed;
	}

	public void setAvgspeed(double avgspeed) {
		this.avgspeed = avgspeed;
	}

	public int getAlltime() {
		return alltime;
	}

	/**
	 * 每五秒执行一次，由dpages计算出各种速度等
	 */
	public void countSpeed() {
//		logger.info("2222222222222下载页数" + this.downloadPages + "1111最后页数" + this.lastdpages);
		if (this.downloadPages - this.lastdpages == 0 && runstate == Task.RUNNING)
			this.nondtimes += 5;
		else
			this.nondtimes = 0;

		alltime += 5;
		nowspeed = nowspeed + this.downloadPages - this.lastdpages;

		if (alltime % 60 == 0) {
			if (this.maxspeed < nowspeed)
				this.maxspeed = nowspeed;

			if (this.minspeed == 0)
				this.minspeed = nowspeed;

			if (nowspeed != 0 && this.minspeed > (nowspeed))
				this.minspeed = nowspeed;

			nowspeed = 0;
		}

		this.avgspeed = Double.valueOf(new DecimalFormat("#0.00")
				.format(((double) (this.downloadPages)) / ((double) alltime)
						* 60));
		this.lastdpages = this.downloadPages;
	}

	public synchronized int getSavePages() {
		return SavePages;
	}

	public synchronized void setSavePages(int savePages) {
		SavePages = savePages;
	}

	public synchronized int getDownloadPages() {
		return downloadPages;
	}

	public synchronized void setDownloadPages(int downloadPages) {
		this.downloadPages = downloadPages;
	}

	public synchronized int getListPages() {
		return listPages;
	}

	public synchronized void setListPages(int listPages) {
		this.listPages = listPages;
	}

	public synchronized int getContentPages() {
		return contentPages;
	}

	public synchronized void setContentPages(int contentPages) {
		this.contentPages = contentPages;
	}

	public synchronized int getUnknownPages() {
		return unknownPages;
	}

	public synchronized void setUnknownPages(int unknownPages) {
		this.unknownPages = unknownPages;
	}
}
