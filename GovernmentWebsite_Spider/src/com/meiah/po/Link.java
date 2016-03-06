package com.meiah.po;

public class Link {

	private String url;
	private String refererUrl;
	private String refererText;
	private String title;

	public Link() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRefererUrl() {
		return refererUrl;
	}

	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

	public boolean equals(Object obj) {

		if (((Link) obj).getUrl().equals(this.getUrl()))
			return true;
		else
			return false;

	}

	public String getRefererText() {
		return refererText;
	}

	public void setRefererText(String refererText) {
		this.refererText = refererText;
	}

}
