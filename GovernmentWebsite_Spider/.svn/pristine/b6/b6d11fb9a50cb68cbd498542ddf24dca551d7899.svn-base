package com.meiah.linkfilters;

import com.meiah.util.JavaUtil;
import com.meiah.util.SysConstants;

public class ExcludeStrFilter implements LinkFilter {

	private String[] excludeStrs;
	private String[] excludeStrRegexs;

	public static final String REGEX_TOKEN = "reg_pico:";

	public ExcludeStrFilter(String excludeStr) {
		int indexRex = excludeStr.indexOf(ExcludeStrFilter.REGEX_TOKEN);
		if (indexRex != -1) {
			excludeStrs = excludeStr.substring(0, indexRex).split(",");
			excludeStrRegexs = excludeStr.substring(indexRex).replaceAll(
					SysConstants.REGEX_TOKEN, "").split(";");
		} else {

			excludeStrs = excludeStr.split(",");
		}

	}

	public ExcludeStrFilter(String[] acceptList) {
		this.excludeStrs = new String[acceptList.length];
		for (int i = 0; i < acceptList.length; i++) {
			this.excludeStrs[i] = acceptList[i].toLowerCase().trim();
		}
	}

	public boolean accept(String link) {
		if (link == null || link.equals("")) {
			return false;
		}

		String checkLink = link.toLowerCase();
		if (excludeStrs != null)
			for (int i = 0; i < excludeStrs.length; i++) {
				String excludeStr = excludeStrs[i].trim().toLowerCase();
				if (!excludeStr.equals("")
						&& checkLink.indexOf(excludeStr) != -1)
					return true;

			}
		if (excludeStrRegexs != null)
			for (int i = 0; i < excludeStrRegexs.length; i++) {
				String excludeStr = excludeStrRegexs[i].trim();
				if (!excludeStr.equals("")
						&& JavaUtil.isAllMatch(checkLink, excludeStr))
					return true;

			}

		return false;
	}

	public static void main(String[] args) {

	}
}
