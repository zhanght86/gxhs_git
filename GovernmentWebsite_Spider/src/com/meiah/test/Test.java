package com.meiah.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.meiah.dao.BaseDao;
import com.meiah.util.JavaUtil;

public class Test {
	private static Logger logger = Logger.getLogger(Test.class);
	private BaseDao baseDao = new BaseDao();
	public static HashSet<String> hs = new HashSet<String>();
	public static int counter;

	public static boolean checkPidExist(String pid) {
		boolean ret = false;
		String line;
		int lineNum = 0;
		BufferedReader input = null;
		try {
			Process proc = Runtime.getRuntime().exec(
					"tasklist /fi \"pid eq " + pid + "\"");
			input = new BufferedReader(new InputStreamReader(proc
					.getInputStream()));
			while ((line = input.readLine()) != null) {
				lineNum++;
			}
			if (lineNum > 1)
				ret = true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	/**
	 * 正则匹配
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static String[] match(String s, String pattern) {
		Matcher m = Pattern.compile(pattern).matcher(s);

		while (m.find()) {
			int n = m.groupCount();
			String[] ss = new String[n + 1];
			for (int i = 0; i <= n; i++) {
				ss[i] = m.group(i);
			}
			return ss;
		}
		return null;
	}

	// private static Log logger1 = LogFactory.getLog("taskinfo");
	/**
	 * @param suffix
	 *            进程文件名后缀
	 * @return
	 */
	public static boolean checkProcessExist(final String suffix) {
		boolean ret = false;
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File ff) {
				if (ff.getName().endsWith(suffix)) {
					return true;
				}
				return false;
			}
		};
		// logger.info("root:"+ClassLoader.getSystemResource("").getPath());
		File[] ffL = new File(ClassLoader.getSystemResource("").getPath())
				.listFiles(fileFilter);
		// logger.info("ffL.length:"+ffL.length);
		int matchCount = 0;
		if (ffL.length > 0) {
			for (int i = 0; i < ffL.length; i++) {
				File pidFile = ffL[i];
				try {
					String pid = match(pidFile.getName(), "\\d+")[0];
					if (checkPidExist(pid)) {
						matchCount++;
						ret = true;
					} else {
						if (pidFile.delete())
							logger.error("无法删除文件！" + pidFile.getName());
					}
				} catch (Exception e) {
					logger.error("error pid file:" + pidFile.getAbsolutePath());
				}
			}

		}
		if (matchCount > 1)
			logger.error("more than a process exist!!");

		return ret;
	}

	/**
	 * @author 胡海斌
	 * @throws ParseException
	 * @throws IOException
	 * @date Jul 7, 2010 返回值：void
	 */
	public static void main(String[] args) throws ParseException, IOException {
		// Runtime runtime = Runtime.getRuntime();
		// String cmds[] = { "cmd", "/c", "tasklist /fi \"pid eq " + 34496 +
		// "\"" };
		// Process proc = runtime.exec(cmds);
		// InputStream inputstream = proc.getInputStream();
		// InputStreamReader inputstreamreader = new
		// InputStreamReader(inputstream);
		// BufferedReader bufferedreader = new
		// BufferedReader(inputstreamreader);
		// String line;
		// while ((line = bufferedreader.readLine()) != null) {
		// System.out.println(line);
		// }
//		TimeZone gmtTime = TimeZone.getTimeZone("GMT");
//		DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",
//				Locale.ENGLISH);
//		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssz",
//				Locale.ENGLISH);
//		df1.setTimeZone(gmtTime);
//		df.setTimeZone(gmtTime);
//		System.out.println(new Date());
//		System.out.println(df.format(new Date()));
//		System.out.println(df.parseObject("Tue, 17 Dec 2013 00:08:00 +0100"));
//		System.out.println(df1.parseObject("2014-02-12 11:39:50+0100"));
//		System.out
//				.println("http://dealbook.nytimes.com/2013/12/18/bitcoin-collides-with-government-concerns/?src=dlbksb"
//						.replaceFirst("(?is)\\?((src)|(ref))=.*", ""));

		String pattern = "/detail_\\d+{4}_\\d+{2}/\\d+{2} | /a/\\d+{8}/\\d+{8}_0.shtml";
		String url = "http://news.ifeng.com/a/20140918/42020052_0.shtml";
		System.out.println(JavaUtil.isAllMatch(url, pattern.trim()));
	}

	public void deleteExpiredSnapFile() {
		int offset = 30;
		String path = "";
		String sqlStrPath = "select filepath from n_para ";
		try {
			List<Object[]> results = baseDao.query(sqlStrPath);
			path = results.get(0)[0].toString();
		} catch (SQLException e) {
			logger.error("获取快照文件保存路径出现错误 ", e);
		}
		String sqlStr = "select taskid,isnull(filename,'')[filename] from n_realtime where datediff(dd,savetime,getdate())>"
				+ offset;
		List<Object[]> results;
		try {
			logger.info("删除文件開始 " + new Date());
			results = baseDao.query(sqlStr);
			logger.info("共 " + results.size() + "个文件");
			for (int i = 0; i < results.size(); i++) {
				try {
					String taskid = results.get(i)[0].toString();
					String fileName = results.get(i)[1].toString();
					if (fileName != null && !fileName.equals(""))
						deleteSnapShotFile(path, taskid, fileName);
				} catch (Exception e) {
					logger.error("删除快照文件出现错误 ", e);
				}

			}
			logger.info("删除文件结束 ");
		} catch (Exception e) {
			logger.error("删除快照文件出现错误 ", e);
		}

	}

	public void deleteSnapShotFile(String savePath, String taskId,
			String fileName) throws Exception {

		if (savePath.endsWith("\\") || savePath.endsWith("/"))
			savePath = savePath.substring(0, savePath.length()) + "/";
		else
			savePath = savePath + "/";
		savePath = savePath + taskId + "/";
		String yyyymmdd = fileName.split("\\.")[2];
		String hhmmsss = fileName.split("\\.")[3];
		String foldPath = yyyymmdd.substring(0, 4) + "/"
				+ yyyymmdd.substring(4, 6) + "/" + yyyymmdd.substring(6, 8)
				+ "/" + hhmmsss.substring(0, 2) + "/" + hhmmsss.substring(2, 3);
		String savaFolder = savePath + foldPath;
		// File savaFolder = new File(savePath + foldPath);
		File deleteFile = new File(savaFolder + "/" + fileName);
		try {
			if (deleteFile.exists())
				if (!deleteFile.delete()) {
					logger.info("无法删除文件：" + deleteFile.getAbsolutePath());
				} else {
					logger.info("删除文件：" + deleteFile.getAbsolutePath() + "成功");
				}
		} catch (Exception e) {
			throw e;
			// logger.error("删除文件：" + deleteFile.getAbsolutePath() + "出现错误 ",
			// e);
		}

	}

	public void deleteFile() {

		String sqlStrPath = "select filename from n_realtime where islist=0 ";
		try {
			List<Object[]> results = baseDao.query(sqlStrPath);
			for (int i = 0; i < results.size(); i++) {
				if (results.get(i)[0] != null) {
					String filenameContent = results.get(i)[0].toString();
					hs.add(filenameContent);
				}
			}
			logger.info("size: " + hs.size());
		} catch (SQLException e) {
			logger.error("获取正文快照集合出现错误 ", e);
		}
		String path = "";
		String sqlSavePath = "select filepath from n_para ";
		try {
			List<Object[]> results = baseDao.query(sqlSavePath);
			path = results.get(0)[0].toString();
		} catch (SQLException e) {
			logger.error("获取快照文件保存路径出现错误 ", e);
		}
		if (path.endsWith("\\") || path.endsWith("/"))
			path = path.substring(0, path.length()) + "/";
		else
			path = path + "/";
		File f = new File(path);
		deleteFile1(f);

	}

	public void deleteFile1(File f) {
		if (f.isFile()) {
			if (!hs.contains(f.getName()) && f.exists()) {
				if (!f.delete())
					logger.error("无法删除文件" + f.getAbsolutePath());
				else {
					counter++;
				}
			}
		} else {
			File files[] = f.listFiles();
			if (files.length == 0) {
				if (!f.delete())
					logger.error("无法删除文件夹" + f.getAbsolutePath());
			} else {
				for (int i = 0; i < files.length; i++)
					deleteFile1(files[i]);
			}
		}

	}
}