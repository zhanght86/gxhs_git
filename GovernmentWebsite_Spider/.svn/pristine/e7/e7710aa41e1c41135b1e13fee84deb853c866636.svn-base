package com.meiah.urlFilter;

import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meiah.util.JavaUtil;

public class ClientCmdProcessor implements Runnable {
	private Logger logger = Logger.getLogger(ClientCmdProcessor.class);

	private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();

	public void processData(NioServer server, SocketChannel socket,
			byte[] data, int count) {
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		synchronized (queue) {
			queue.add(new ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}
	}

	public void run() {
		ServerDataEvent dataEvent;

		while (true) {
			// Wait for data to become available
			synchronized (queue) {
				while (queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = (ServerDataEvent) queue.remove(0);
			}
			try {
				byte[] response = processComand(dataEvent);// 若获取传入命令成功，则解析
				dataEvent.server.send(dataEvent.socket, response);
			} catch (Exception e) {
				e.printStackTrace();

			}

		}
	}

	public byte[] processComand(ServerDataEvent event) {
		String msg = new String(event.data);
		String result = getReturnCmd("4", ServerCenter.WRONG_CMD);
		try {
			String[] t = JavaUtil.match(msg,
					"\\[([0|1])\\]\\[(\\d+)\\]\\[(\\d+)\\](.*)");

			int cmdtype = Integer.valueOf(t[3]);
			String content = t[4];

			switch (cmdtype) {// 根据解析命令参数执行不同过程
			
			case ServerCenter._IS__LINK_EXIST: {
				result = this.isLinkExists(content);
				break;
			}
			case ServerCenter._ADD_LINK: {
				result = this.addLink(content);
				break;
			}
			case ServerCenter._TEST__LINK_EXIST: {
				result = this.testLinkExists(content);
				break;
			}
			case ServerCenter.WRONG_CMD: {
				result = getReturnCmd("4", ServerCenter.WRONG_CMD);
				break;
			}
			default: {
				break;
			}
			}
			return result.getBytes();

		} catch (Exception e) {
			logger.error("命令格式错误，传入命令：" + msg);

		}
		return result.getBytes();
	}

	

	/**
	 * 判断链接是否抓取过
	 * 
	 * @param content
	 * @return 0代表未抓取；1代表抓取过
	 */
	private String isLinkExists(String content) {
		try {

			String key = content;
			// logger.debug("test" + key);
			if (ServerCenter.isReady) {
				if (!ServerCenter.urlFilter.contains(key)) {
					// ServerCenter.urlFilter.add(key);
					return getReturnCmd("0", ServerCenter._IS__LINK_EXIST);
				} else {
					return getReturnCmd("1", ServerCenter._IS__LINK_EXIST);
				}
			} else {
				return getReturnCmd("2", ServerCenter._IS__LINK_EXIST);
			}

		} catch (Exception e) {
			logger.error("  _IS__LINK_EXIST 异常", e);
			return "0";
		}

	}

	/**
	 * 判断链接是否抓取过
	 * 
	 * @param content
	 * @return 0代表未抓取；1代表抓取过
	 */
	private String testLinkExists(String content) {
		try {
			String taskid = content.split("#&#")[0];
			String url = content.split("#&#")[1];
			// logger.debug("test");
			String key = taskid + url;
			// logger.debug("test" + key);
			if (ServerCenter.isReady) {
				if (!ServerCenter.urlFilter.contains(key)) {
					return getReturnCmd("0", ServerCenter._TEST__LINK_EXIST);
				} else {
					return getReturnCmd("1", ServerCenter._TEST__LINK_EXIST);
				}
			} else {
				return getReturnCmd("2", ServerCenter._TEST__LINK_EXIST);
			}

		} catch (Exception e) {
			logger.error("  _TEST__LINK_EXIST 异常", e);
			return "0";
		}

	}

	private String addLink(String content) {
		try {

			String key = content;
			if (ServerCenter.isReady) {
				if (!ServerCenter.urlFilter.contains(key)) {
					ServerCenter.urlFilter.add(key);
					return getReturnCmd("1", ServerCenter._ADD_LINK);
				} else {

					logger.error("addLink异常,重复添加！" + key);
					return getReturnCmd("0", ServerCenter._ADD_LINK);
				}
			} else {
				return getReturnCmd("2", ServerCenter._ADD_LINK);
			}

		} catch (Exception e) {
			logger.error("_SET_LINK_TYPE 异常", e);
			return "0";
		}

	}

	/**
	 * 服务器只回复命令
	 * 
	 * @param result
	 * @return
	 */
	private String getReturnCmd(String result, int cmd) {
		// return "[0]["+new Date().getTime()+"]["+cmd+"]"+result+"";
		return "[0][" + new Date().getTime() + "][" + cmd + "]" + result
				+ "\r\n";
	}

}

/**
 * }
 */
