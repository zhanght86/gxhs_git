package com.meiah.htmlParser;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class ContentExtractor {
	private static Logger logger = Logger.getLogger(ContentExtractor.class);
	public static String INTERPUNCTION = "，、。﹖？！…～．“”（）" + ",\\?!";

	/**
	 * @param beginCode
	 *            正文开始标记
	 * @param endCode
	 *            正文结束标记 正文开始、结束标记可以为空
	 * @param webContent
	 *            html源码
	 * @return html网页正文
	 * 
	 * @常见调用：String 
	 *              newsContent=ContentExtractor.extractMainContent("","",htmlSouce
	 *              );
	 * @throws Exception
	 */
	public static String extractMainContent(String beginCode, String endCode,
			String webContent) throws Exception {
		String newsContent = "";
		try {
			String textBeginCode = beginCode;
			String textEndCode = endCode;

			if (!textBeginCode.equals("") && !textEndCode.equals("")) {
				String text = webContent.toLowerCase();
				int iPos0 = text.indexOf(textBeginCode.toLowerCase());

				if (iPos0 != -1) {
					int len0 = textBeginCode.length();
					int iPos1 = text.indexOf(textEndCode.toLowerCase(), iPos0
							+ len0);
					if (iPos1 != -1) {
						newsContent = text.substring(iPos0 + len0, iPos1);
						newsContent = newsContent.replaceAll(
								"(?i)(?s)<marquee.*?</marquee>", "");
						newsContent = newsContent.replaceAll("(?i)(?s)</?p( .*?)?>", "　　").replaceAll("(?i)(?s)</?br>", "　　");
//								.replaceAll("(?s)(?i)<.*?>", "");
						newsContent = newsContent.replaceAll("&nbsp;?", " ")
								.replaceAll("&gt;?", ">").replaceAll("&lt;?",
										"<").replaceAll("&#149;?", "")
								.replaceAll("&quot;?", "\"").replaceAll("\\s+",
										" ");
					}
				}
			}
			if (newsContent.equals("")) {
				String text = webContent.toLowerCase();
				try {
					newsContent = fixA(text);
				} catch (Exception e1) {
					logger.error("fixa error!" + e1.getMessage());
				}
				newsContent = getPlainText(newsContent);
				newsContent = extract(newsContent);
			}
		} catch (Exception e) {
			throw e;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("提取正文长度：" + newsContent.length());
		}
		return newsContent;
	}
	// 智能提取
	public static String extract(String txt) throws Exception {
		// mLog.log(2, "原始文本 txt.length()=" + txt.length(), "_newsTest_");
		Map<Character, Character> map = new HashMap<Character, Character>();
		// HashMap<String, String> map = new HashMap()<String, String>;
		map.put('，', '1');
		map.put('。', '1');
		map.put('；', '1');

		map.put(',', '1');

		map.put('﹖', '1');
		map.put('？', '1');
		map.put('?', '1');
		map.put('!', '1');
		map.put('！', '1');
		// map.put('（','1');
		// map.put('）','1');

		StringBuffer sbf = new StringBuffer("");// 将标点格式化成标点+空格
		char[] c = txt.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (map.get(c[i]) != null) {
				sbf.append(c[i]).append(' ');
			} else {
				sbf.append(c[i]);
			}
		}
		txt = sbf.toString();
		// mLog.log(2, "格式化标点 txt.length()=" + txt.length(), "_newsTest_");

		String ss = "";
		String[] ass = txt.split("\\s+");// 将文章按照空格分割
		if (txt.equals("") || ass.length == 0)
			return txt;

		int[] turns = new int[ass.length];// 存在标点的句子所在的行号
		int turnsNum = 0;// 有几个句子存在标点
		char[] inters = new char[ass.length];// 保存每个分割句最后一个符号

		// mLog.log(2, "每分割最后的字符-------------------------------------"
		// + "-----------------------------------", "_newsTest_");
		for (int ii = 0; ii < ass.length; ii++) {
			inters[ii] = ass[ii].charAt(ass[ii].length() - 1);
			// mLog.log(2, "charAt " + ii + "=" + inters[ii] + "\t" + ass[ii],
			// "_newsTest_");

		}

		for (int ii = 0; ii < inters.length; ii++) {// 判断每个分割句最后一个符号是否为标点，并将行号保存
			if (INTERPUNCTION.indexOf(inters[ii]) != -1) {
				// logger.debug(ii + ":" + ass[ii]);
				turns[turnsNum] = ii;
				turnsNum++;
			}
		}

		// mLog.log(2, "包含标点的行号数组-------------------------------------"
		// + "-----------------------------------" + turnsNum
		// + "\r\n turns " + java.util.Arrays.toString(turns),
		// "_newsTest_");
		int start = 0, end = 0, START = 0, END = 0;
		for (int i = 0; i < turnsNum; i++) {
			// mLog.log(2, turns[i] + "\t" + ass[turns[i]], "_newsTest_");
		}

		for (int i = 1; i < turnsNum; i++) {
			if (turns[i] - turns[i - 1] < 8) {// 首次出现相隔句子

				if (start == 0) {
					start = turns[i - 1];
					// mLog.log(2, "首次 start" + start, "_newsTest_");
				} else if (end == 0 && i == (turnsNum - 1)) {// 表示正文一直持续到最后
					end = turns[i];
					sbf = new StringBuffer("");
					for (int z = start; z <= end; z++) {
						sbf.append(ass[z]);
					}
					String _s = sbf.toString();

					if (ss.length() < _s.length()) {// 从中取最长一段
						ss = _s;
						START = start;
						END = end;
						// mLog.log(2, "longest START=" + START + " END=" + END,
						// "_newsTest_");
					}
				}
			} else {// 首次出现相离句子
				if (start != 0 && end == 0) {
					end = turns[i - 1];
					// mLog.log(2, "首次 end" + end, "_newsTest_");
					// mLog
					// .log(2, "start=" + start + "\tend=" + end,
					// "_newsTest_");

					sbf = new StringBuffer("");
					for (int z = start; z <= end; z++) {
						sbf.append(ass[z]);
					}
					String _s = sbf.toString();
					// if (logger.isDebugEnabled()) {
					// for (int j = turns[i - 1]; j <= turns[i]; j++)
					// logger.debug("\r\nfoun：" + j + ":" + ass[j]);
					// logger.debug("\r\nfound：" + _s);
					// }
					if (ss.length() < _s.length()) {// 从中取最长一段
						ss = _s;
						START = start;
						END = end;
						// mLog.log(2, "longest START=" + START + " END=" + END,
						// "_newsTest_");
					}
					start = 0;
					end = 0;
				}
			}
		}
		if (ss.length() == 0 && (start != 0 && end == 0)) {// 如果正文之后再没有标点符号，则取到最后一个
			sbf = new StringBuffer("");
			end = turns[turnsNum - 1];
			for (int z = start; z <= end; z++) {
				sbf.append(ass[z]);
			}
			// mLog.log(2, "start=" + start + "\tend=" + end, "_newsTest_");
			ss = sbf.toString();
			START = start;
			END = end;
		}
		if (turnsNum == 1) {
			ss = ass[turns[0]];
		}
		return ss;
	}

	// 去除<a...>xxx</a> xxx之间的html代码，这些html标签会影响HTMLParser的判断
	public static String fixA(String content) throws Exception {
		StringBuffer sbf = new StringBuffer("");
		String _sc = content.toLowerCase();
		int i1 = 0, i2 = 0;
		i1 = _sc.indexOf("<a ");
		if (i1 != -1)
			sbf.append(content.substring(0, i1));
		else
			return _sc.replaceAll("(?i)</?span.*?>", "");
		while (i1 != -1) {
			i2 = _sc.indexOf(">", i1);
			i2 = i2 + 1;
			sbf.append(content.substring(i1, i2));// <a ....>
			i1 = i2;
			i2 = _sc.indexOf("</a", i1);
			if (i2 == -1) {
				i2 = i1;
				i1 = _sc.indexOf("<a ", i2);
				if (i1 != -1) {
					sbf.append(content.substring(i2, i1));// </a>........</a>
				}
				// return content.replaceAll("(?i)</?span.*?>", "");
			} else {
				String _s1 = content.substring(i1, i2);// xxx

//				_s1 = _s1.replaceAll("(?s)(?i)<.*?>", "");
				sbf.append(_s1);
				i1 = i2;
				i2 = _sc.indexOf(">", i1);
				i2 = i2 + 1;
				sbf.append(content.substring(i1, i2));// </a>
				i1 = _sc.indexOf("<a ", i2);
				if (i1 != -1) {
					sbf.append(content.substring(i2, i1));// </a>........</a>
				}
			}
		}
		if (_sc.length() > i2 && i2 > 0) {
			sbf.append(content.substring(i2));// </a>
		}

		return sbf.toString().replaceAll("(?i)</?span.*?>", "").replaceAll(
				"(?i)</?font.*?>", "").replaceAll("(?i)</?strong.*?>", "")
				.replaceAll("(?i)</?b.*?>", "");
	}

	/**
	 * 解析普通文本节点.
	 * 
	 * @param content
	 * @throws ParserException
	 */
	public static String getPlainText(String content) throws Exception {
		content = content.replaceAll("(?i)(?s)<marquee.*?</marquee>", "")
				.replaceAll("(?i)(?s)</?p( .*?)?>", "　　").replaceAll(
						"(?i)(?s)</?br>", "　　");

		String ss;
		StringBuffer sbf = new StringBuffer("");

		try {
			Parser myParser;
			Node[] nodes = null;
			NodeList nodeList = null;

			myParser = Parser.createParser(content, null);

			// 这里不能抛出异常
			nodeList = myParser.extractAllNodesThatMatch(new NodeClassFilter(
					TextNode.class));
			nodes = nodeList.toNodeArray();
			for (int i = 0; i < nodes.length; i++) {
				TextNode textnode = (TextNode) nodes[i];
				String line = textnode.toPlainTextString().trim();
				if (line.equals(""))
					continue;
				else {
					// 将链接中，包含标点符号的地方，替换为空格
					if (textnode.getParent() instanceof LinkTag) {
						line = line.replaceAll("[" + INTERPUNCTION + "]", " ");
					} else {
						line = line.replaceAll("\\s+", "　");
					}
					// mLog.log(2,"..["+textnode.getParent().getClass().getName()+"]
					// "+line,"_newsTest_");
					sbf.append(line).append("\r\n");
				}
			}
		} catch (Exception e) {
			throw e;

		}
		ss = sbf.toString();
		ss = ss.replaceAll("(?s)(?i)<[a-z].*?>", "").replaceAll("&nbsp;?", "")
				.replaceAll("&gt;?", ">").replaceAll("&lt;?", "<").replaceAll(
						"&#149;?", "").replaceAll("&quot;?", "\"").replaceAll(
						"\\s+", " ");// 去掉空格

		return ss;
	}

	/**
	 * 去除html页面源代码 无用信息
	 * 
	 * @param webSourcePage
	 * @return
	 */
	private String clearHtml(String webSourcePage) {
		String target = webSourcePage.replaceAll("(?i)(?s)<style.*?</style>",
				"").replaceAll("(?i)(?s)<(no)?script.*?</(no)?script>", "")
				.replaceAll("(?i)(?s)<select.*?</select>", "").replaceAll(
						"(?i)(?s)<!--.*?-->", "");
		target = target.replaceAll("&nbsp;?", " ").replaceAll("[ ]{2,}", " ")
				.replaceAll("\\s+", " ");
		return target;
	}

}
