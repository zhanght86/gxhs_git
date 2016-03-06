package com.meiah.webCrawlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

//import org.apache.log4j.Logger;

/**
 * 全局计时器，精确计时，保证所有任务在确切的时间内启动<br>
 * 利用 Observer 模式通知任务
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-08-06
 */
public class CountTimer extends Observable {
	private static Logger logger = Logger.getLogger(CountTimer.class);

	private Calendar currentTime;

	public static void main(String[] args) {

		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			RandomAccessFile raf;
			try {
				raf = new RandomAccessFile(new File("~runing_Count.now"), "rw");
				FileChannel fc = raf.getChannel();
				try {
					@SuppressWarnings("unused")
					FileLock fl = fc.tryLock();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} else {
			String pid = ManagementFactory.getRuntimeMXBean().getName();
			pid = pid.substring(0, pid.indexOf("@"));
			File pidFile = new File(pid + "." + TaskStartAuto.COUNT_SUFFIX);
			try {
				if (!pidFile.createNewFile())
					logger.error("无法coutimer排重过滤器进程文件！");
			} catch (IOException e2) {
				logger.error("创建coutimer过滤器进程文件异常！", e2);
			}
			logger.info("启动统计进程，pid：" + pid);
		}

		Properties props = new Properties();
		try {

			InputStream istream = SiteCrawler.class.getResourceAsStream("/log4j.properties");
			props.load(istream);
			istream.close();
			String logPath = ClassLoader.getSystemResource("").getPath() + File.separatorChar + "log" + File.separatorChar;
			props.setProperty("log4j.appender.logfile.File", logPath + "count_info.log");
			props.setProperty("log4j.appender.logfile1.File", logPath + "count_error.log");

			// 重新配置后，日志会打到新的文件去。
			PropertyConfigurator.configure(props);// 装入log4j配置信息
		} catch (Exception e) {
			logger.error("装入属性文件异常 Exception ", e);
		}
		CountTimer c = new CountTimer();// 计时被观察器
		c.addObserver(new ScanCount());// 添加统计通知

		while (true) {

			Calendar currentTime = Calendar.getInstance();
			currentTime.setTime(new Date());
			c.setCurrentTime(currentTime);

			if (currentTime.get(Calendar.SECOND) == 0) {
				logger.info("当前时间：" + new Date() + " , 通知统计方法");
				c.setChanged();
				c.notifyObservers();
			}

			try {
				Thread.sleep(1000);// 1秒检查一次
			} catch (InterruptedException e) {
			}
		}
	}

	public Calendar getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Calendar currentTime) {
		this.currentTime = currentTime;
	}

}
