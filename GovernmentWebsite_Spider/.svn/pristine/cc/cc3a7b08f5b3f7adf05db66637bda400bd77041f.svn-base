package com.meiah.urlFilter;

import com.meiah.urlFilter.ServerCenter;

public class FilterGenerator extends Thread {
	
	
	@Override
	public void run() {
		FilterGeneratorMutiThread fg = new FilterGeneratorMutiThread();
		ServerCenter.urlFilter=fg.getFilter();
		ServerCenter.isReady=true;
		
		
	}
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
	}
}
