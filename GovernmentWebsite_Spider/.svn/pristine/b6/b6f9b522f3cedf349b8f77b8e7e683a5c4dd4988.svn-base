package com.meiah.urlFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.meiah.util.BloomFilter;
import com.meiah.util.Config;
import com.meiah.util.SysConstants;
import com.meiah.webCrawlers.TaskStartAuto;

public class ServerCenter {
	private static Logger logger = Logger.getLogger(ServerCenter.class);
	public static BloomFilter<String> urlFilter;
	public static String useDbIp = ""; // 当前使用数据库的IP

	// -----------------外部请求方法列表-----------------------

	public final static int _IS__LINK_EXIST = 2;// 检查网页是否下载过

	public final static int _ADD_LINK = 3;// 设置刚下载网页在缓存中的页面类别

	public final static int WRONG_CMD = 4;// socket命令错误

	public final static int _TEST__LINK_EXIST = 5;// 检查网页是否下载过

	public static boolean isReady = false;

	// ----------------------------------------

	/**
	 * 构造函数中 初始化服务器socket，线程池，加载缓存数据
	 * 
	 * @throws IOException
	 */
	public ServerCenter() throws IOException {

	}

	class RegetBloomFilterTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (ServerCenter.isReady) {
				logger.debug("当前过滤器存储url数目：" + urlFilter.count() / 2 + ",误判概率"
						+ urlFilter.getFalsePositiveProbability());
				if (urlFilter.getFalsePositiveProbability() > SysConstants.BLOOMFILTER_MAX_FALSEPOSITIVE) {
					ServerCenter.isReady = false;
					logger.info("过滤器误判概率过大，重启过滤器");
					FilterGenerator fg1 = new FilterGenerator();// 多线程加载url排重的布隆过滤器
					fg1.start();
				}
			}
		}
	};

	private void initBloomFilter() {
		FilterGenerator fg = new FilterGenerator();// 多线程加载url排重的布隆过滤器
		fg.start();
		Timer regetBloomFilterTimer = new Timer();
		regetBloomFilterTimer.schedule(new RegetBloomFilterTask(),
				60 * 60 * 1000l, 60 * 60 * 1000l);
	}

	public static void main(String[] args) throws IOException {
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			RandomAccessFile raf;
			try {
				raf = new RandomAccessFile(new File("~runing_ServerCente.now"), "rw");
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
			File pidFile = new File(pid + "." + TaskStartAuto.SERVER_SUFFIX);
			try {
				if (!pidFile.createNewFile())
					logger.error("无法列表排重过滤器进程文件！");
			} catch (IOException e2) {
				logger.error("创建列表排重过滤器进程文件异常！", e2);
			}
			logger.info("启动列表排重进程，pid：" + pid);
		}
		Properties props = new Properties();
		try {
			InputStream istream = ServerCenter.class
					.getResourceAsStream("/log4j.properties");
			props.load(istream);
			istream.close();
			String logPath = ClassLoader.getSystemResource("").getPath()
					+ File.separatorChar + "log" + File.separatorChar;

			props.setProperty("log4j.appender.logfile.File", logPath
					+ "serverSocket_debug.log");
			props.setProperty("log4j.appender.logfile1.File", logPath
					+ "serverSocket_info.log");
			// 重新配置后，日志会打到新的文件去。
			PropertyConfigurator.configure(props);// 装入log4j配置信息
		} catch (Exception e) {
		}

		try {
			ServerCenter center = new ServerCenter();
			center.initBloomFilter();
			ClientCmdProcessor worker = new ClientCmdProcessor();
			new Thread(worker).start();

			new Thread(new NioServer(InetAddress.getByName(Config
					.getLocalServerIp()), Config.getLocalServerPort(), worker))
					.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}