package com.meiah.webCrawlers;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.meiah.dao.TaskDao;

public class DeleteSnapShot {
	private static Logger logger = Logger.getLogger(DeleteSnapShot.class);
	private static AtomicInteger deletedNum = new AtomicInteger(0);

	public static void main(String[] args) throws ParseException {
		String fileSavePath = TaskDao.getInstance().getSavePath();
		File snapShotFolder = new File(fileSavePath);
		logger.info("删除快照开始");
		DeleteSnapShot delete = new DeleteSnapShot();
		delete.deleteExpiredFile(snapShotFolder, 6);
		logger.info("删除快照文件结束，共删除文件数：" + deletedNum);
	}

	public void deleteExpiredFile(File snapShotFolder, int keepDay)
			throws ParseException {
		Calendar nowDate = Calendar.getInstance();
		nowDate.add(Calendar.DATE, 0 - keepDay);
		String Tyear = String.valueOf(nowDate.get(Calendar.YEAR));
		String Tmonth = String.valueOf(nowDate.get(Calendar.MONTH) + 1);
		if (Tmonth.length() < 2)
			Tmonth = "0" + Tmonth;
		String Tday = String.valueOf(nowDate.get(Calendar.DAY_OF_MONTH));
		if (Tday.length() < 2)
			Tday = "0" + Tday;
		String beforeDateS = Tyear + Tmonth + Tday;
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		Date beforeDate = sf.parse(beforeDateS);

		File[] taskFolders = snapShotFolder.listFiles();
		for (int i = 0; i < taskFolders.length; i++) {
			File taskFolder = taskFolders[i];
			File[] yearFolders = taskFolder.listFiles();
			for (int j = 0; j < yearFolders.length; j++) {
				File yearFolder = yearFolders[j];
				File[] monthFolders = yearFolder.listFiles();
				for (int k = 0; k < monthFolders.length; k++) {
					File monthFolder = monthFolders[k];
					File[] dayFolders = monthFolder.listFiles();
					for (int m = 0; m < dayFolders.length; m++) {
						File dayFolder = dayFolders[m];
						String dateInfo = yearFolder.getName()
								+ monthFolder.getName() + dayFolder.getName();
						Date folderDate = sf.parse(dateInfo);
						if (folderDate.before(beforeDate)) {
							deleteFile(dayFolder);
						}
					}
					if (monthFolder.listFiles().length == 0) {
						monthFolder.delete();
					}
				}
				if (yearFolder.listFiles().length == 0) {
					yearFolder.delete();
				}
			}
			if (taskFolder.listFiles().length == 0) {
				taskFolder.delete();
			}
		}

	}

	public void deleteFile(File f) {
		if (f.isFile()) {
			if (!f.delete())
				logger.error("无法删除文件" + f.getAbsolutePath());
			else
				deletedNum.addAndGet(1);

		} else {
			File files[] = f.listFiles();
			if (files.length == 0) {
				if (!f.delete())
					logger.error("无法删除文件夹" + f.getAbsolutePath());
			} else {
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
				if (!f.delete())
					logger.error("无法删除文件夹" + f.getAbsolutePath());

			}
		}

	}
}
