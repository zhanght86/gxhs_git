package com.meiah.po;

public class SiteConfig {
	private String crawlerClassname;
	private String resolverClassname;
	private String contentUrlRegex;
	private String titleLocation;
	private String contentLocation;
	private String publishTimeLocation;
	private String sourceSiteLocation;
	private String authorLocation;
	private String imageUrlLocation;
	private String columnNameLocation;
	private String documentLocation;

	public String getColumnNameLocation() {
		return columnNameLocation;
	}

	public void setColumnNameLocation(String columnNameLocation) {
		this.columnNameLocation = columnNameLocation;
	}

	public String getDocumentLocation() {
		return documentLocation;
	}

	public void setDocumentLocation(String documentLocation) {
		this.documentLocation = documentLocation;
	}

	public static final String SPLITER = "###";

	public String getContentUrlRegex() {
		return contentUrlRegex;
	}

	public void setContentUrlRegex(String contentUrlRegex) {
		this.contentUrlRegex = contentUrlRegex;
	}

	public String getTitleLocation() {
		return titleLocation;
	}

	public void setTitleLocation(String titleLocation) {
		this.titleLocation = titleLocation;
	}

	public String getContentLocation() {
		return contentLocation;
	}

	public void setContentLocation(String contentLocation) {
		this.contentLocation = contentLocation;
	}

	

	public String getPublishTimeLocation() {
		return publishTimeLocation;
	}

	public void setPublishTimeLocation(String publishTimeLocation) {
		this.publishTimeLocation = publishTimeLocation;
	}

	public String getSourceSiteLocation() {
		return sourceSiteLocation;
	}

	public void setSourceSiteLocation(String sourceSiteLocation) {
		this.sourceSiteLocation = sourceSiteLocation;
	}

	public String getAuthorLocation() {
		return authorLocation;
	}

	public void setAuthorLocation(String authorLocation) {
		this.authorLocation = authorLocation;
	}

	public String getCrawlerClassname() {
		return crawlerClassname;
	}

	public void setCrawlerClassname(String crawlerClassname) {
		this.crawlerClassname = crawlerClassname;
	}

	public String getResolverClassname() {
		return resolverClassname;
	}

	public void setResolverClassname(String resolverClassname) {
		this.resolverClassname = resolverClassname;
	}

	public String getImageUrlLocation() {
		return imageUrlLocation;
	}

	public void setImageUrlLocation(String imageUrlLocation) {
		this.imageUrlLocation = imageUrlLocation;
	}

}