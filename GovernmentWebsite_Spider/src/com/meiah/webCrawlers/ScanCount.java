package com.meiah.webCrawlers;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.meiah.dao.ScanDao;
import com.meiah.util.Config;
import com.meiah.util.MyDate;

public class ScanCount extends Thread implements Observer {
	private Logger logger = Logger.getLogger(ScanCount.class);
	private int ctype;

	public ScanCount(int ctype) {
		this.ctype = ctype;
	}

	public ScanCount() {
		this.ctype = -1;
	}

	@Override
	public void run() {
		mainPro();
	}

	private void mainPro() {
		switch (this.ctype) {
		case 1: {
			ScanDao.getInstance().createKWScores();
			logger.info("生成News的KWScores数据结束！");
			break;
		}
		case 8: {
			ScanDao.getInstance().createNewsTrs();
			logger.info("生成News的TRS数据结束！");
			break;
		}
		case 13: {
			ScanDao.getInstance().clearOlddatas();
			logger.info("清除历史数据结束！");
			break;
		}

		default: {
			break;
		}
		}
	}

	public void update(Observable o, Object arg) {

		if (((CountTimer) o).getCurrentTime().get(Calendar.SECOND) == 0) {
			// new ScanCount(1).start();// 每隔1分钟，则生成News的KWScore数据
			if (Config.getIsCreateIndex() == 1)
				new ScanCount(8).start();// 每隔1分钟，则生成News的TRS数据
			if (((CountTimer) o).getCurrentTime().get(Calendar.HOUR_OF_DAY) == 9
					&& ((CountTimer) o).getCurrentTime().get(Calendar.MINUTE) == 41) {
				new ScanCount(13).start();
			}
		}
	}

	public static void main(String[] args) {
		String t1 = new MyDate().get_yyyymmddhh24miss();
//		System.out.println(t1);
		t1 = t1.replaceAll("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})", "$1-$2-$3 $4:$5:$6");
//		System.out.println(t1);
//		t2 = t2.replaceAll("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})", "$1-$2-$3 $4:$5:$6");
//		new ScanCount(8).start();
	}
}