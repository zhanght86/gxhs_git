package com.meiah.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.meiah.po.TaskLink;

public class LinkQueue {
	private LinkedList<TaskLink> q;
	private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock rLock = rwl.readLock();
	private final Lock wLock = rwl.writeLock();

	/**
	 * @param args
	 */
	public LinkQueue() {
		q = new LinkedList();
	}

	public synchronized void put(TaskLink link) {
		wLock.lock();
		try {
			q.addLast(link);
			Collections.sort(q);
		} finally {
			wLock.unlock();
		}
	}

	public synchronized TaskLink pop() {
		rLock.lock();
		try {
			return q.removeFirst();
		} finally {
			rLock.unlock();
		}
	}

	public synchronized boolean isEmpty() {
		return q.isEmpty();
	}

	public synchronized TaskLink peek() {
		return q.peek();
	}

	public synchronized boolean exist(TaskLink link) {
		boolean flag = false;
		for (TaskLink s : q) {
			if (s.equals(link)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public synchronized int getQueueSize() {
		return q.size();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LinkQueue lq = new LinkQueue();
		// TaskLink link1 = new TaskLink("fdsafa", 1);
		// link1.setVisited(false);
		// lq.put(link1);
		// TaskLink link2 = new TaskLink("fds", 1);
		// lq.put(link2);
		// link2.setVisited(true);
		// TaskLink link3 = new TaskLink("fdsfdsafdsa", 1);
		// lq.put(link3);
		//		
		// TaskLink link4 = new TaskLink("fdsafa", 2);
		// lq.put(link4);
		// TaskLink link5 = new TaskLink("fds", 2);
		//		
		// lq.put(link5);
		//		
		// TaskLink link6 = new TaskLink("fds", 3);
		// lq.put(link6);
		//		
		// TaskLink link7 = new TaskLink("fdsafa", 3);
		// lq.put(link7);
		// while (!lq.isEmpty()) {
		// TaskLink link = lq.pop();
		// System.out.println(link.getLevel() + ": " + link.getUrl());
		// }
		// TaskLink link8 = new TaskLink("fdsafafdsa", 3);
		// lq.put(link8);
		// Collections.sort(lq.q);

	}

}
