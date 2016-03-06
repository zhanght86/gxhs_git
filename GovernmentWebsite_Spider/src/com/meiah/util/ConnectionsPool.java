package com.meiah.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
/**
 * 数据库连接池
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-04-22
 * 
 */
public class ConnectionsPool {
	private Logger logger = Logger.getLogger(ConnectionsPool.class);

	private String driver;
	private String url;
	private String user;
	private String pwd;
	private int initCon;// 连接池中初始创建连接数
	private int maxCon;// 连接池中允许创建的最大的连接数
	private int incCon;// 请求连接数大于活动连接数时，允许请求用户创建的连接数(必须大于0)
	private int currCon;// 当前活动连接数，包括已经从连接池中分配出去的连接和连接池中的可分配连接
	private int overtime = 60;// 超时时间，单位(s)。超过时间就回收连接
	private int sleeptime = 30;// 获取连接失败后的休眠时间。单位(s)
	//private int maxusetime = 8;//最多使用次数
	

	private Hashtable<String, Object[]> releaseconnectionPool;// 已分配连接池
	private LinkedList<Connection> connectionsPool; // 待分配连接池
	private Map<String,Long> connectionsUsetime; // 连接使用次数记录

	private static ConnectionsPool connectionsPoolInstance = null;// 返回实例

	/**
	 * 构造函数，初始化基本信息以及连接池
	 */
	public ConnectionsPool() {
		connectionsPool = new LinkedList<Connection>();
		releaseconnectionPool = new Hashtable<String, Object[]>();
		connectionsUsetime = new HashMap<String, Long>();
		initConfig();
		initPool();
	}

	/**
	 * 单例
	 * 
	 * @return
	 */
	public synchronized static ConnectionsPool getInstance() {
		if (connectionsPoolInstance == null) {
			connectionsPoolInstance = new ConnectionsPool();
		}

		return connectionsPoolInstance;
	}

	/**
	 * 获取基本配置
	 */
	private void initConfig() {
		this.driver = Config.getDriver();
		this.url = Config.getUrl();
		this.user = Config.getUser();
		this.pwd = Config.getPwd();

		this.initCon = Config.getInitCon();
		this.maxCon = Config.getMaxCon();
		this.incCon = Config.getIncCon();
		this.overtime = Config.getOvertime();
		this.sleeptime = Config.getSleeptime();
	}

	/**
	 * 按照设定数量初始化数据库连接池连接
	 */
	private void initPool() {
		try {
			Class.forName(driver);
			for (int i = 0; i < initCon; i++)
				try {
					connectionsPool.addLast(this.createConnection());
				} catch (Exception e) {
					logger.error("建立数据库连接异常 " + i);
				}
		} catch (Exception e) {
			logger.error("获取驱动异常：" + driver);
		}
		currCon = connectionsPool.size();
	}

	/**
	 * 归还连接，将连接从已分配连接池删除，并加入未分配连接池，计算总共使用次数，超过最多次数，则放弃该连接
	 * 
	 * @param con
	 */
	public synchronized void giveBackConnectioni(Connection con) {
		releaseconnectionPool.remove(con.toString());
		
		long nowUsetime=connectionsUsetime.get(con.toString());
		
		if(nowUsetime>new java.util.Date().getTime()){
			connectionsPool.addLast(con);
		}else{
			this.abandonConnection(con);
		}
	}

	/**
	 * 丢弃连接，将连接从已分配连接池删除，关闭，并减少当前连接数量，从计算连接次数中删除
	 * 
	 * @param con
	 */
	public synchronized void abandonConnection(Connection con) {
		releaseconnectionPool.remove(con.toString());
		connectionsUsetime.remove(con.toString());
		this.currCon--;
	}

	/**
	 * 获取连接对外接口
	 * @return
	 */
	public Connection getConnection(){
		Connection con =null;
		while(true){
			try {
				con = this.getInsideConnection();
				if(!con.isClosed()){
					break;// 若验证有效，且未关闭，则返回该连接
				}else{
					this.abandonConnection(con);//否则放弃连接
				}
			} catch (SQLException e) {
				logger.error("获取链接失败，"+this.sleeptime+"秒后重新连接......",e);
				try {
					Thread.sleep(this.sleeptime*1000);
				} catch (InterruptedException e1) {}
			}
		}
		return con;
	}

	private synchronized Connection getInsideConnection() throws SQLException {
		// logger.debug("now cp num: "+this.connectionsPool.size()+" rcp num:
		// "+this.releaseconnectionPool.size()+" currCon num:"+this.currCon);
		Connection retCon = null;
		if (this.connectionsPool.size() > 0) {
			// 若当前待分配连接池中还有连接，直接获取
			retCon = this.connectionsPool.removeFirst();
			releaseconnectionPool.put(retCon.toString(), new Object[] { retCon,
					new Date() });
			return retCon;
		}

		if (this.currCon < this.maxCon) {
			// 待分配连接池中无连接，增加新连接
			for (int i = 0; i < this.incCon && this.currCon < this.maxCon; i++, this.currCon++) {
				connectionsPool.addLast(this.createConnection());
			}
			return this.getInsideConnection();
		} else {
			// 当前连接数超过最大连接数，尝试释放已分配连接池中连接，若有释放，则从待分配连接池中获取，否则抛出异常
			boolean hasOvertime = false;
			String conName = null;
			Connection con = null;
			Enumeration<String> eConnections = releaseconnectionPool.keys();
			for (; eConnections.hasMoreElements();) {
				conName = eConnections.nextElement();
				con = (Connection) releaseconnectionPool.get(conName)[0];
				Date date = (Date) releaseconnectionPool.get(conName)[1];
				Date crrdate = new Date();
				long time = (crrdate.getTime() - date.getTime()) / 1000;
				if (time > overtime && con != null) {
					releaseconnectionPool.remove(conName);
					connectionsPool.addLast(con);
					hasOvertime = true;
				}
			}
			con = null;
			if (hasOvertime) {
				return this.getInsideConnection();
			} else {
				throw new SQLException("数据库连接池已经达到最大连接数，无法提供连接");
			}
		}
	}

	/**
	 * 创建连接
	 * @return
	 * @throws SQLException
	 */
	private Connection createConnection() throws SQLException {
//		System.out.println(">>"+url);
//		System.out.println(">>"+user);
//		System.out.println(">>"+pwd);
		Connection realConn = DriverManager.getConnection(url, user, pwd);
		realConn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); 
		connectionsUsetime.put(realConn.toString(), new java.util.Date().getTime()+(5*60*60*1000));// 最长使用5个小时
		return realConn;
	}
	
	public static void main(String[] args) {
			ConnectionsPool.getInstance();
	}

}
