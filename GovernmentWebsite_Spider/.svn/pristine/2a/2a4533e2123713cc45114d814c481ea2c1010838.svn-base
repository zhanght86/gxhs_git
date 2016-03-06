package com.meiah.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 读取配置，填充至静态属性
 * 
 * @author chenc
 * 
 */
public class PDFConfig {

	private static Logger logger = Logger.getLogger(PDFConfig.class);
	private static String configFile = ClassLoader.getSystemResource(
			"config/pdfConfig.ini").getPath();// 配置文件
	
	// ====================pdf文件保存路径=======================
	private static String pdfPath;

	public static String getPdfPath() {
		return pdfPath;
	}

	public static void setPdfPath(String pdfPath) {
		PDFConfig.pdfPath = pdfPath;
	}

	static {
		load();
	}

	/**
	 * 从文件中读取属性
	 */
	private static void load() {
		FileInputStream fis = null;
		try {

			fis = new FileInputStream(configFile);
			Properties dbField = new Properties();
			dbField.load(fis);

			pdfPath = dbField.getProperty("pdfPath", "");
			
		} catch (FileNotFoundException e) {
			logger.error("config/pdfConfig.ini 配置文件不存在");
		} catch (IOException e) {
			logger.error("读取 config/pdfConfig.ini 配置文件错误");
		} finally {
			try {
				fis.close();
				fis = null;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}


	/**
	 * 重新获取属性
	 */
	public static void reload() {
		load();
	}
	
}
