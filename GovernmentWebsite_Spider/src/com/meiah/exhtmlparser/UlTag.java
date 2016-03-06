package com.meiah.exhtmlparser;

import org.htmlparser.tags.CompositeTag;

/**
 * htmlparse匹配标签 ul
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-08-10
 * 
 */
public class UlTag extends CompositeTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String[] mIds = new String[] {"UL"};

	@Override
	public String[] getEnders() {
		return mIds;
	}

	@Override
	public String[] getIds() {
		return mIds;
	}
	

}
