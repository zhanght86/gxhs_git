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
public class Config {

	private static Logger logger = Logger.getLogger(Config.class);
	private static String configFile = ClassLoader.getSystemResource(
			"config/config.ini").getPath();// 配置文件
	// ====================分局编号=======================
	private static String SpotID; // 全局唯一
	// ====================ipName=======================
	private static Integer cid; // ipName

	// ====================数据库相关配置=======================
	private static String dbname;
	// private static String dbname;
	private static String driver;
	private static String url;
	private static String user;
	private static String pwd;

	private static String localuser;
	private static String localpassword;
	private static String localurl;
	private static String localdriver;

	private static String mongoHost;
	private static Integer mongoPort;
	private static String mongoDbname;
	private static String mongoTablename;
	private static String proxyCenterPort;
	private static String proxyCenterUrl;
	private static String localIp;

	private static Integer initCon;
	private static Integer maxCon;
	private static Integer incCon;
	private static Integer overtime;
	private static Integer sleeptime;

	private static int serverPort;
	private static String serverIp;

	private static int localServerPort;
	private static String localServerIp;

	// ====================索引相关配置=======================
	public static String isDoubleTrs;

	public static String BASEPREFIX = "newsics";

	public static String TRS_NEWS = "newsics_realtime";
	public static String TRS_YK = "YK";

	// ====================socket相关配置=======================
	private static String serversocketip;// 服务器SOCKET的IP
	private static String uipserversocketip;// 用户IP服务器SOCKET的IP

	// ====================系统相关配置=======================
	private static Integer maxproc; // 最多同时运行多少个进程
	private static String deldays;// 数据保存时间
	private static Integer Save_BatchCount;
	private static Integer isProxyTaskServer;

	private static byte indexon;// 开关变量，是否生成本地index文件
	private static byte isSaveSnapShot;
	
	private static String fileSavePath;
	
	public static String getProxyCenterPort() {
		return proxyCenterPort;
	}

	public static String getProxyCenterUrl() {
		return proxyCenterUrl;
	}

	public static String getFileSavePath() {
		return fileSavePath;
	}

	public static void setFileSavePath(String fileSavePath) {
		Config.fileSavePath = fileSavePath;
	}

	public static byte getIsSaveSnapShot() {
		return isSaveSnapShot;
	}

	public static void setIsSaveSnapShot(byte inSaveSnapShot) {
		Config.isSaveSnapShot = inSaveSnapShot;
	}

	private static Integer isTaskDBMysql = 0;
	private static Integer isLocalDBMysql = 0;

	public static Integer getIsLocalDBMysql() {
		return isLocalDBMysql;
	}

	public static void setIsLocalDBMysql(Integer isLocalDBMysql) {
		Config.isLocalDBMysql = isLocalDBMysql;
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

			driver = dbField.getProperty("driver", "");
			url = dbField.getProperty("url", "");
			user = dbField.getProperty("user", "");
			pwd = dbField.getProperty("password", "");
			fileSavePath=dbField.getProperty("fileSavePath", "index");
			localdriver = dbField.getProperty("localdriver", "");
			localurl = dbField.getProperty("localurl", "");
			localuser = dbField.getProperty("localuser", "");
			localpassword = dbField.getProperty("localpassword", "");
			proxyCenterPort = dbField.getProperty("proxyCenterPort");
			proxyCenterUrl = dbField.getProperty("proxyCenterUrl");
			mongoHost = dbField.getProperty("mongoHost", "127.0.0.1");
			mongoPort = Integer.parseInt(dbField.getProperty("mongoPort",
					"27017"));
			mongoDbname = dbField.getProperty("mongoDbname", "meiya");
			mongoTablename = dbField.getProperty("mongoTablename", "pico");

			localIp = dbField.getProperty("localIp", "");

			serverIp = dbField.getProperty("serverIp", "");
			String serverPortS = dbField.getProperty("serverPort", "");
			try {
				serverPort = Integer.parseInt(serverPortS);
			} catch (Exception e) {
				logger.error("新闻排重中心，端口配置错误![serverPort=" + serverPortS + "]");
				System.exit(-1);
			}

			localServerIp = dbField.getProperty("localServerIp", "127.0.0.1");
			String localServerPortS = dbField.getProperty("localServerPort",
					"39291");
			try {
				localServerPort = Integer.parseInt(localServerPortS);
			} catch (Exception e) {
				logger.error("列表过滤器服务端口配置错误![localServerPort="
						+ localServerPortS + "]");
				System.exit(-1);
			}

			dbname = dbField.getProperty("dbname", "newsicsdis");

			cid = Integer.parseInt(dbField.getProperty("cid", "-1"));
			initCon = Integer.parseInt(dbField.getProperty("initcon", "3"));
			maxCon = Integer.parseInt(dbField.getProperty("maxcon", "10"));
			incCon = Integer.parseInt(dbField.getProperty("inccon", "3"));
			overtime = Integer.parseInt(dbField.getProperty("overtime", "60"));
			sleeptime = Integer
					.parseInt(dbField.getProperty("sleeptime", "30"));

			isDoubleTrs = dbField.getProperty("isDoubleTrs", "0");

			serversocketip = dbField.getProperty("serversocketip", "127.0.0.1");
			uipserversocketip = dbField.getProperty("uipserversocketip",
					"127.0.0.1");

			maxproc = Integer.parseInt(dbField.getProperty("maxproc", "15"));
			if (maxproc <= 0) {
				logger.error("参数maxproc设置错误，默认设为10");
				maxproc = 15;
			}
			SpotID = dbField.getProperty("SpotID", "0000000000006700");

			deldays = dbField.getProperty("deldays", "0");
			Save_BatchCount = Integer.parseInt(dbField.getProperty(
					"Save_BatchCount", "10"));
			if (Save_BatchCount <= 0) {
				logger.error("参数Save_BatchCount设置错误，默认设为10");
				Save_BatchCount = 10;
			}

			isProxyTaskServer = Integer.parseInt(dbField.getProperty(
					"isProxyTaskServer", "0"));
			if (isProxyTaskServer != 0 && isProxyTaskServer != 1) {
				logger.error("参数isForeignTaskServer设置错误，默认设为0");
				isProxyTaskServer = 0;
			}

			indexon = Byte.parseByte(dbField.getProperty("indexon", "0"));
			if (indexon != 0 && indexon != 1) {
				logger.error("参数indexon设置错误，默认设为0");
				indexon = 0;
			}
			isSaveSnapShot = Byte.parseByte(dbField.getProperty(
					"isSaveSnapShot", "0"));
			if (isSaveSnapShot != 0 && isSaveSnapShot != 1) {
				logger.error("参数isSaveSnapShot设置错误，默认设为0");
				isSaveSnapShot = 0;
			}
			if (url.indexOf("mysql") != -1) {
				isTaskDBMysql = 1;
			}
			if (localurl.indexOf("mysql") != -1) {
				isLocalDBMysql = 1;
			}
			// isTaskDBMysql = Integer.parseInt(dbField
			// .getProperty("isMysql", "1"));
			// if (isTaskDBMysql != 0 && isTaskDBMysql != 1) {
			// logger.error("参数isMysql设置错误，默认设为0");
			// isTaskDBMysql = 1;
			// }
		} catch (FileNotFoundException e) {
			logger.error("config/config.ini 配置文件不存在");
		} catch (IOException e) {
			logger.error("读取 config/config.ini 配置文件错误");
		} finally {
			try {
				fis.close();
				fis = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static Integer getIsTaskDBMysql() {
		return isTaskDBMysql;
	}

	public static void setIsMysql(Integer isMysql) {
		Config.isTaskDBMysql = isMysql;
	}

	/**
	 * 重新获取属性
	 */
	public static void reload() {
		load();
	}

	public static String getDriver() {
		return driver;
	}

	public static String getUrl() {
		return url;
	}

	public static String getUser() {
		return user;
	}

	public static String getPwd() {
		return pwd;
	}

	public static Integer getInitCon() {
		return initCon;
	}

	public static Integer getMaxCon() {
		return maxCon;
	}

	public static Integer getIncCon() {
		return incCon;
	}

	public static Integer getOvertime() {
		return overtime;
	}

	public static Integer getSleeptime() {
		return sleeptime;
	}

	public static String getServersocketip() {
		return serversocketip;
	}

	public static Integer getMaxproc() {
		return maxproc;
	}

	public static String getDbname() {
		return dbname;
	}

	public static String getUipserversocketip() {
		return uipserversocketip;
	}

	public static String getSpotID() {
		return SpotID;
	}

	public static String getDeldays() {
		return deldays;
	}

	public static void setDeldays(String deldays) {
		Config.deldays = deldays;
	}

	public static String getIsDoubleTrs() {
		return isDoubleTrs;
	}

	public static void setIsDoubleTrs(String isDoubleTrs) {
		Config.isDoubleTrs = isDoubleTrs;
	}

	public static Integer getCid() {
		return cid;
	}

	public static void setCid(Integer cid) {
		Config.cid = cid;
	}

	public static void setSave_BatchCount(Integer save_BatchCount) {
		Save_BatchCount = save_BatchCount;
	}

	public static void main(String[] args) {
		load();
	}

	public static Integer getSave_BatchCount() {
		return Save_BatchCount;
	}

	public static String getLocaluser() {
		return localuser;
	}

	public static String getLocalpassword() {
		return localpassword;
	}

	public static String getLocalurl() {
		return localurl;
	}

	public static String getLocaldriver() {
		return localdriver;
	}

	public static byte getIsCreateIndex() {
		return indexon;
	}

	public static void setIsCreateIndex(byte isCreateIndex) {
		Config.indexon = isCreateIndex;
	}

	public static String getMongoHost() {
		return mongoHost;
	}

	public static void setMongoHost(String mongoHost) {
		Config.mongoHost = mongoHost;
	}

	public static Integer getMongoPort() {
		return mongoPort;
	}

	public static void setMongoPort(Integer mongoPort) {
		Config.mongoPort = mongoPort;
	}

	public static String getMongoDbname() {
		return mongoDbname;
	}

	public static void setMongoDbname(String mongoDbname) {
		Config.mongoDbname = mongoDbname;
	}

	public static String getMongoTablename() {
		return mongoTablename;
	}

	public static void setMongoTablename(String mongoTablename) {
		Config.mongoTablename = mongoTablename;
	}

	public static int getServerPort() {
		return serverPort;
	}

	public static void setServerPort(int serverPort) {
		Config.serverPort = serverPort;
	}

	public static String getServerIp() {
		return serverIp;
	}

	public static void setServerIp(String serverIp) {
		Config.serverIp = serverIp;
	}

	public static String getLocalIp() {
		return localIp;
	}

	public static void setLocalIp(String localIp) {
		Config.localIp = localIp;
	}

	public static Integer getIsProxyTaskServer() {
		return isProxyTaskServer;
	}

	public static void setIsProxyTaskServer(Integer isProxyTaskServer) {
		Config.isProxyTaskServer = isProxyTaskServer;
	}

	public static int getLocalServerPort() {
		return localServerPort;
	}

	public static void setLocalServerPort(int localServerPort) {
		Config.localServerPort = localServerPort;
	}

	public static String getLocalServerIp() {
		return localServerIp;
	}

	public static void setLocalServerIp(String localServerIp) {
		Config.localServerIp = localServerIp;
	}

}
