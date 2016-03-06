
package com.meiah.linkfilters;



public class FileExtensionFilter implements LinkFilter {

	
	private String[] fileExtentinons;

	
	public FileExtensionFilter(String fileExtentinon) {
		fileExtentinons = new String[] { fileExtentinon.toLowerCase() };
	}

	
	public FileExtensionFilter(String[] acceptList) {
		this.fileExtentinons = new String[acceptList.length];
		for (int i = 0; i < acceptList.length; i++) {
			this.fileExtentinons[i] = acceptList[i].toLowerCase();
		}
	}

	

	public boolean accept(String link) {
		if (link == null || link.equals("")) {
			return false;
		}

		String checkLink = link.toLowerCase();

		if (checkLink.endsWith(".html") || checkLink.endsWith(".htm")
				|| checkLink.endsWith(".shtml"))
			return false;
		for (int i = 0; i < fileExtentinons.length; i++) {
			if (checkLink.endsWith(fileExtentinons[i])) {
				return true;
			}
		}

		return false;
	}

}
