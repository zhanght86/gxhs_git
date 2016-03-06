package com.meiah.util;
public class Simi {

	//找出txt中包含关键字的最长句，提取其中的汉字，并全部转为简体
	public static String getSimitxt(String txt) {
		// 标记出关键词
		txt = txt.replaceAll("<font color.*?>", "``").replaceAll("<.*?>", "").replaceAll(
				"[\\w\\s的　]", "");
		String get = "";
		// 常用标点符号
		String[] ssL = txt.split("[,，、.．。【】!！\\│\\?？\\(\\):：︰﹕;；|‖┊…]");
		int i_key = 0, i_key_len = 0;// 包含关键字最长的那组
		int i_group = 0, i_group_len = 0;// 最长的那组

		for (int ii = 0; ii < ssL.length; ii++) {
			if (ssL[ii].length() > i_group_len) {// 每组都进行处理，将最长的长度保存在
				// i_group_len
				i_group_len = ssL[ii].length();
				i_group = ii;
			}
			if (ssL[ii].indexOf("``") != -1) {// 只对包含关键词的组进行处理
				if (ssL[ii].length() > i_key_len) {
					i_key_len = ssL[ii].length();
					i_key = ii;
				}
			}
		}
		// 如果最长的组都不到12个字，则认为无规律
		if (i_group_len < 12) {
		} else {
			if (i_key_len > 12) {
				get = ssL[i_key];
			} else {
				if (i_key_len < i_group_len)
					get = ssL[i_group];
			}
		}
		get = GBIG.getA().big2gb(get.replaceAll("``", ""));
		return onlyCHN(get);
	}

	static String onlyCHN(String s) {
		StringBuffer sbf = new StringBuffer("");
		char[] ary_s = s.toCharArray();
		for (int i = 0; i < ary_s.length; i++) {
			if (isCHN(ary_s[i])) {
				sbf.append(ary_s[i]);
			} else {
			}
		}
		return sbf.toString();
	}

	static boolean isCHN(char c) {
		return (19968 <= (int) c) && ((int) c <= 40891);
	}
}