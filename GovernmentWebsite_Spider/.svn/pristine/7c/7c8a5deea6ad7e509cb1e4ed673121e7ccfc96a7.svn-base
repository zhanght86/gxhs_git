package com.meiah.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//敏感词过滤-使用hashmap实现dfa算法
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BaijiaxingMFilter {
	/** 直接禁止的 */
	private HashMap keysMap = new HashMap();
	private int matchType = 2; // 1:最小长度匹配 2：最大长度匹配
	//private KeywordFilter filter =null;
	// 单例  
	   private static BaijiaxingMFilter bam = null;  
	   
	   private BaijiaxingMFilter(){
			Set<String> keywords = new HashSet<String>();
			String family = "万俟 司马 上官 欧阳 夏侯 诸葛 闻人 东方 赫连 皇甫 尉迟 公羊 澹台 公冶 宗政 濮阳 淳于 单于 太叔 申屠 公孙 仲孙 轩辕 令狐 锺离 宇文 长孙 慕容 鲜于 闾丘 司徒 司空 丌官 司寇 子车 颛孙 端木 巫马 公西 漆雕 乐正 壤驷 公良 拓拔 夹谷 宰父 谷梁 段干 百里 东郭 南门 呼延 羊舌 微生 梁丘 左丘 东门 西门 南宫 第五";
			String[] xing = family.split(" ");
			for (int i = 0; i < xing.length; i++) {
				keywords.add(xing[i]);
			}
			addKeywords(keywords);
		}
	    public static BaijiaxingMFilter getInstance() {  
	       if (null == bam) {  
	           bam = new BaijiaxingMFilter();  
	       }  
	     return bam;  
	   }  

	public void addKeywords(Set<String> keywords) {
		for (String words : keywords) {
			
			String key = words.trim();
			HashMap nowhash = null;
			nowhash = keysMap;
			for (int j = 0; j < key.length(); j++) {
				char word = key.charAt(j);
				Object wordMap = nowhash.get(word);
				if (wordMap != null) {
					nowhash = (HashMap) wordMap;
				} else {
					HashMap<String, String> newWordHash = new HashMap<String, String>();
					newWordHash.put("isEnd", "0");
					nowhash.put(word, newWordHash);
					nowhash = newWordHash;
				}
				if (j == key.length() - 1) {
					nowhash.put("isEnd", "1");
				}
			}
		}
	}

	/**
	 * 重置关键词
	 */
	public void clearKeywords() {
		keysMap.clear();
	}

	/**
	 * 检查一个字符串从begin位置起开始是否有keyword符合， 如果有符合的keyword值，返回值为匹配keyword的长度，否则返回零
	 * flag 1:最小长度匹配 2：最大长度匹配
	 */
	private int checkKeyWords(String txt, int begin, int flag) {
		HashMap nowhash = null;
		nowhash = keysMap;
		int maxMatchRes = 0;
		int res = 0;
		int l = txt.length();
		char word = 0;
		for (int i = begin; i < l; i++) {
			word = txt.charAt(i);
			Object wordMap = nowhash.get(word);
			if (wordMap != null) {
				res++;
				nowhash = (HashMap) wordMap;
				if (((String) nowhash.get("isEnd")).equals("1")) {
					if (flag == 1) {
						wordMap = null;
						nowhash = null;
						txt = null;
						return res;
					} else {
						maxMatchRes = res;
					}
				}
			} else {
				txt = null;
				nowhash = null;
				return maxMatchRes;
			}
		}
		txt = null;
		nowhash = null;
		return maxMatchRes;
	}

	/**
	 * 返回txt中关键字的列表
	 */
	public Set<String> getTxtKeyWords(String txt) {
		Set set = new HashSet();
		int l = txt.length();
		for (int i = 0; i < l;) {
			int len = checkKeyWords(txt, i, matchType);
			if (len > 0) {
				set.add(txt.substring(i, i + len));
				i += len;
			} else {
				i++;
			}
		}
		txt = null;
		return set;
	}

	/**
	 * 仅判断txt中是否有关键字
	 */
	public boolean isContentKeyWords(String txt) {
		for (int i = 0; i < txt.length(); i++) {
			int len = checkKeyWords(txt, i, 1);
			if (len > 0) {
				return true;
			}
		}
		txt = null;
		return false;
	}

	public int getMatchType() {
		return matchType;
	}

	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}

	
	   /** 
     * 获取替换字符串 
     *  
     * @param replaceChar 
     * @param length 
     * @return 
     */  
    private String getReplaceChars(String replaceChar, int length) {  
        String resultReplace = replaceChar;  
        for (int i = 1; i < length; i++) {  
            resultReplace += replaceChar;  
        }  
  
        return resultReplace;  
    }  
	
    /** 
     * 替换敏感字字符 
     *  
     * @param txt 
     * @param matchType 
     * @param replaceChar 
     * @return 
     */  
    public String replaceKeyWords(String txt,  String replaceChar) {  
  
        String resultTxt = txt;  
  
        // 获取所有的敏感词  
        Set<String> set = getTxtKeyWords(txt);  
        Iterator<String> iterator = set.iterator();  
        String word = null;  
        String replaceString = null;  
        while (iterator.hasNext()) {  
            word = iterator.next();  
            replaceString = getReplaceChars(replaceChar, word.length());  
            resultTxt = resultTxt.replaceAll(word, replaceString);  
        }  
  
        return resultTxt;  
    }  
	
	
	public static void main(String[] args) {
		BaijiaxingMFilter filter = new BaijiaxingMFilter();
		Set<String> keywords = new HashSet<String>();
		keywords.add("有限");
		keywords.add("责任");
		keywords.add("公司");
		filter.addKeywords(keywords);
		String txt = "陕西迪普物业有限公司,西安迪乐普环保科技有限责任公司";
		boolean boo = filter.isContentKeyWords(txt);
//		System.out.println(boo);
		Set set = filter.getTxtKeyWords(txt);
//		System.out.println(set);
		String hou = filter.replaceKeyWords(txt,"");  
//		System.out.println("替换前的文字为：" + txt);  
//        System.out.println("替换后的文字为：" + hou);  
		// String rootPath =  KeywordFilter.class.getClass().getResource("/").getFile().toString();
		// System.out.println(rootPath);
		 
	}
}
