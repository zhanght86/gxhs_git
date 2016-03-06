package com.meiah.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 读取配置，填充至静态属性，服务器配置
 * 
 * @author chenc
 * 
 */
public class ServerConfig {

	private static Logger logger = Logger.getLogger(ServerConfig.class);

	private static String configFile = ClassLoader.getSystemResource("")
			.toString().replaceAll("file:/", "")
			+ "config/server.properties";// 配置文件

	// ====================系统相关配置=======================
	private static Integer socket_port; // 最多同时运行多少个进程
	private static Integer socket_maxthread; // 最多同时运行多少个进程
	private static Integer ingoremin; // 从多少分钟之后作为下一个整点

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

			socket_port = Integer.parseInt(dbField.getProperty("socket_port",
					"39299"));
			socket_maxthread = Integer.parseInt(dbField.getProperty(
					"socket_maxthread", "40"));

			ingoremin = Integer
					.parseInt(dbField.getProperty("ingoremin", "45"));

		} catch (FileNotFoundException e) {
			logger.error("com/meiah/util/server.properties 配置文件不存在");
		} catch (IOException e) {
			logger.error("读取 com/meiah/util/server.properties 配置文件错误");
		}
	}

	/**
	 * 重新获取属性
	 */
	public static void reload() {
		load();
	}

	public static Integer getSocket_port() {
		return socket_port;
	}

	public static Integer getSocket_maxthread() {
		return socket_maxthread;
	}

	public static Integer getIngoremin() {
		return ingoremin;
	}

}
