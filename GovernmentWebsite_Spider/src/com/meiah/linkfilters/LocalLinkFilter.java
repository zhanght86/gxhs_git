package com.meiah.linkfilters;



public class LocalLinkFilter implements LinkFilter {

	
	private String[] sitePrefixs;


	public LocalLinkFilter(String path) {
		sitePrefixs = new String[] { path.toLowerCase().trim() };
	}


	public LocalLinkFilter(String[] acceptList) {
		this.sitePrefixs = new String[acceptList.length];
		for (int i = 0; i < acceptList.length; i++) {
			this.sitePrefixs[i] = acceptList[i].toLowerCase().trim();
		}
	}

	
	public boolean accept(String link) {
		if (link == null || link.equals("")) {
			return false;
		}

		String checkLink = link.toLowerCase();
		for (int i = 0; i < sitePrefixs.length; i++) {
			if (checkLink.startsWith(sitePrefixs[i])) {
				return true;
			}
		}

		return false;
	}

}
