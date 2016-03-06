package com.meiah.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//敏感词过滤-使用hashmap实现dfa算法
@SuppressWarnings({ "rawtypes", "unchecked" })
public class KeywordFilter {
	/** 直接禁止的 */
	private HashMap keysMap = new HashMap();
	private int matchType = 2; // 1:最小长度匹配 2：最大长度匹配
	//private KeywordFilter filter =null;
	// 单例  
	   private static KeywordFilter inst = null;  
	   
	   private KeywordFilter(){
			Set<String> keywords = new HashSet<String>();
			keywords.add("作者");
			keywords.add("記者");
			keywords.add("记者");
			keywords.add("編輯");
			keywords.add("编辑");
			keywords.add("发布");
			keywords.add("浏览次数");
			keywords.add("累计浏览");
			keywords.add("地理位置图");
			keywords.add("本帖最后由");
			keywords.add("相关");
			keywords.add("主持人");
			keywords.add("来源");
			keywords.add("时间");
			keywords.add("岗前培训班");
			keywords.add("关键字");
			keywords.add("关键词");
			keywords.add("我来说");
			keywords.add("进入复兴论坛");
			keywords.add("登录");
			keywords.add("取消");
			keywords.add("首页");
			keywords.add("更多");
			keywords.add("评论内容");
			keywords.add("收藏");
			keywords.add("国际收支");
			keywords.add("【搭车】");
			keywords.add("公厅关于");
			keywords.add("关于组织");
			keywords.add("免费注册");
			addKeywords(keywords);
		}
	    public static KeywordFilter getInstance() {  
	       if (null == inst) {  
	           inst = new KeywordFilter();  
	       }  
	     return inst;  
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
		KeywordFilter filter = new KeywordFilter();
		Set<String> keywords = new HashSet<String>();
		keywords.add("有限");
		keywords.add("责任");
		keywords.add("公司");
		filter.addKeywords(keywords);
		String txt = "陕西迪普物业有限公司,西安迪乐普环保科技有限责任公司";
		boolean boo = filter.isContentKeyWords(txt);
		/*System.out.println(boo);
		Set set = filter.getTxtKeyWords(txt);
		System.out.println(set);
		String hou = filter.replaceKeyWords(txt,"");  
		System.out.println("替换前的文字为：" + txt);  
        System.out.println("替换后的文字为：" + hou);  */
		// String rootPath =  KeywordFilter.class.getClass().getResource("/").getFile().toString();
		// System.out.println(rootPath);
		 
	}
}
