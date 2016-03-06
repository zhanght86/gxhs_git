package com.meiah.test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.meiah.dao.BaseDao;
import com.meiah.util.JavaUtil;
import com.meiah.util.WebPageDownloader;

public class CopyOfTest {
	private static Logger logger = Logger.getLogger(CopyOfTest.class);
	private BaseDao baseDao = new BaseDao();
	public static HashSet<String> hs = new HashSet<String>();
	public static int counter;

	// private static Log logger1 = LogFactory.getLog("taskinfo");

	/**
	 * @author 胡海斌
	 * @throws Exception
	 * @date Jul 7, 2010 返回值：void
	 */
	public static void main(String[] args) throws Exception {
		int[] iar = new int[] { 1, 2, 3, 5 };
		int x = 0;
		for (int n : iar) {
			switch (n) {
			case 1:
				x += n;
			case 2:
				x += n;
			case 5:
				x += n;
			default:
				x += n;
			case 3:
				x += n;
			
			}
		}
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