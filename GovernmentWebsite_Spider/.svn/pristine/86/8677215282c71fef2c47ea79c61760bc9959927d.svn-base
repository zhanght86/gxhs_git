/**
 * 连接到sokcet服务器，获取IPName
 */
package com.meiah.webCrawlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.apache.log4j.Logger;
import com.meiah.util.Config;

public class SOCKClient {
	private static Logger logger = Logger.getLogger(SOCKClient.class);
	public Socket socket = null;
	public BufferedReader in;
	public BufferedWriter out;

	public String serverip = "";
	public int serverport = 0;

	public int localPort = 0;

	public static SOCKClient sockClient = null;

	public SOCKClient() {
	}

	private static SOCKClient getSOCKClient(String serverip, int serverport) {
		sockClient = new SOCKClient();
		sockClient.serverip = serverip;
		sockClient.serverport = serverport;

		try {
			sockClient.socket = new Socket(serverip, serverport);
			sockClient.localPort = sockClient.socket.getLocalPort();
			// System.out.println(new MyDate().getDateTime() + " " + "socket
			// accepted " + localPort);
			sockClient.in = new BufferedReader(new InputStreamReader(
					sockClient.socket.getInputStream()));
			sockClient.out = new BufferedWriter(new OutputStreamWriter(
					sockClient.socket.getOutputStream()));

		} catch (Exception e) {
			sockClient = null;
			logger.error("链接migeIp服务出现异常！请检查配置! " + e.getMessage());
		}
		return sockClient;
	}

	public synchronized String getIPName(String ip) {
		if (ip == null || ip.trim().length() == 0)
			return "";

		String ss = "";

		try {
			if (sockClient != null) {
				out.write(ip + "\n");
				out.flush();
				ss = in.readLine();
			}
		} catch (Exception e) {
			bye();
			sockClient = null;
			logger.error("ClientSock Err 2 " + e, e);
		}

		// System.out.println("ipName " + ss +" ok");

		return ss;

	}

	public synchronized static SOCKClient getInstance() {
		while (sockClient == null) {
			sockClient = getSOCKClient(Config.getUipserversocketip(), 9417);
			if (sockClient != null)
				break;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		return sockClient;
	}

	public static void bye() {
		try {
			if (sockClient != null) {
				SOCKClient.getInstance().out.write("bye\n");
				SOCKClient.getInstance().out.flush();
			}
		} catch (Exception e) {
			logger.error("ClientSock Err 2 " + e, e);
		} finally {
			sockClient = null;
		}
	}

	public static void main(String[] args) {
		// String k = SOCKClient.getInstance().getIPName("121.204.201.250");
		SOCKClient sc = SOCKClient.getInstance();
//		System.out.println(sc == null);
	}

}
