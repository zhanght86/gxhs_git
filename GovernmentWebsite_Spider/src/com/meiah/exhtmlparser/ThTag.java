/**
 * htmlparse匹配标签 th
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-08-10
 * 
 */
package com.meiah.exhtmlparser;

import org.htmlparser.tags.CompositeTag;

public class ThTag extends CompositeTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String[] mIds = new String[] {"TH"};

	@Override
	public String[] getEnders() {
		return mIds;
	}

	@Override
	public String[] getIds() {
		return mIds;
	}
	

}
