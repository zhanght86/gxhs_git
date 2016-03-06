package com.meiah.po;

import java.util.Date;

import com.meiah.util.SysConstants;
import com.meiah.util.WordTypeEnum;

public class TaskLink extends Link implements Comparable<TaskLink> {
	private String taskid;
	private int level;
	private int linkType = SysConstants.PAGETYPE_UNKNOWN;
	private boolean isVisited = false;
	private Date publishTime = null;
	private String source;
	private String author;
	private String pageContent;
	private WordTypeEnum wordType;
	private String columnName;
	private String documentName;
	private String imgUrl; 
	
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getPageContent() {
		return pageContent;
	}

	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public TaskLink() {
	}

	public TaskLink(Link link) {
		this.setUrl(link.getUrl());
		this.setTitle(link.getTitle());
		this.setRefererUrl(link.getRefererUrl());
		this.setRefererText(link.getRefererText());
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public int getLinkType() {
		return linkType;
	}

	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean equals(Object obj) {
		if (((TaskLink) obj).getUrl().equals(this.getUrl()))
			return true;
		else
			return false;
	}

	public int compareTo(TaskLink o) {
		TaskLink l = o;
		int flag = 0;

//		boolean isVisitedThis = this.isVisited;
//		boolean isVisitedSpec = l.isVisited;

		int linkTypeThis = this.linkType;
		int linkTypeSpec = l.linkType;
//		if (isVisitedThis == true && isVisitedSpec == false) {
//			flag = 1;
//		} else if (isVisitedThis == false && isVisitedSpec == true) {
//			flag = -1;
//		} else {
//			flag = 0;
//		}
		if (linkTypeThis != SysConstants.PAGETYPE_CONTENT
				&& linkTypeSpec == SysConstants.PAGETYPE_CONTENT) {
			flag = 1;
		} else if (linkTypeThis == SysConstants.PAGETYPE_CONTENT
				&& linkTypeSpec != SysConstants.PAGETYPE_CONTENT) {
			flag = -1;
		} else {
			if (l.level < this.level) {
				flag = 1;
			} else if (l.level > this.level) {
				flag = -1;
			} 
		}
		
		// if (flag == 0) {
		// if (l.url.length() > this.url.length()) {
		// flag = 1;
		// } else if (l.url.length() < this.url.length()) {
		// flag = -1;
		// } else {
		// flag = 0;
		// }
		// }

		return flag;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public WordTypeEnum getWordType() {
		return wordType;
	}

	public void setWordType(WordTypeEnum wordType) {
		this.wordType = wordType;
	}


	
}
