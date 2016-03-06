package com.meiah.htmlParser;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class ContentExtractorV2 {
	private static Logger logger = Logger.getLogger(ContentExtractorV2.class);
	private static final String PUNCTUATIONS = "。.，,、？?﹖！!；;…“”\"";// 正常文章中出现的中英文标点符号
	private static final String SEPERATOR = "###";// 用于切分网页中句子的分隔符
	private static final int DISTURB_LENGTH = 6;// 连续文字中不允许出现 链接文字超过该长度的链接
	private static Set<String> guideLinkText = new HashSet<String>();// 部分连接虽然文字较短，但是也是正文提取的干扰因素，一般為引导性连接，其链接文字一般特征为某些词组开头
	private static final int DIVIDE_THRESHOLD = 8;
	private static String linkTextReplacement = "";// 用于将干扰的链接文字替换，以成为提取最长正文的分隔
	static {
		guideLinkText.add("详细");
		guideLinkText.add("詳細");
		guideLinkText.add("详全文");
		guideLinkText.add("詳全文");

		for (int i = 0; i < DIVIDE_THRESHOLD; i++) {
			linkTextReplacement += "a" + SEPERATOR;// 此处a没有意义，linkTextReplacement的构造只是为了以干扰链接为点切分最长句
		}
	}

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
						newsContent = newsContent.replaceAll(
								"(?i)(?s)</?p( .*?)?>", "　　").replaceAll(
								"(?i)(?s)</?br>", "　　").replaceAll(
								"(?s)(?i)<.*?>", "");

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
				newsContent = fixA(text);
				newsContent = getPlainText(newsContent);
				newsContent = extract(newsContent);
			}
		} catch (Exception e) {
			throw e;
		}

		return newsContent;

	}

	// 智能提取
	public static String extract(String txt) throws Exception {
		String longestContent = "";
		StringBuffer sbf = new StringBuffer("");
		Set<Character> punctuationSet = new HashSet<Character>();
		punctuationSet.add('，');
		punctuationSet.add('。');
		punctuationSet.add('；');
		punctuationSet.add(',');
		punctuationSet.add('﹖');
		punctuationSet.add('？');
		punctuationSet.add('?');
		punctuationSet.add('!');
		punctuationSet.add('！');

		// punctuationSet.add('（');
		// punctuationSet.add('）');

		char[] c = txt.toCharArray();
		for (int i = 0; i < c.length; i++) {
			// 将标点格式化成标点+空格
			if (punctuationSet.contains(c[i])) {
				sbf.append(c[i]).append(SEPERATOR);
			} else {
				sbf.append(c[i]);
			}
		}
		txt = sbf.toString();
		String[] sentencees = txt.split("#{3,}");// 将文章按照空格分割
		if (txt.equals("") || sentencees.length == 0)
			return txt;

		int[] punctuationRows = new int[sentencees.length];// 存在标点的句子所在的行号
		int punctuationRowCount = 0;// 有几个句子存在标点
		char[] inters = new char[sentencees.length];// 保存每个分割句最后一个符号

		for (int i = 0; i < sentencees.length; i++) {

			String temp = sentencees[i].replaceAll("\\s+", "").replaceAll(
					"[　]+", "");
			if (logger.isDebugEnabled())
				logger.debug("s:" + temp);
			if (temp.length() > 0)
				inters[i] = temp.charAt(temp.length() - 1);
		}

		for (int i = 0; i < inters.length; i++) {// 判断每个分割句最后一个符号是否为标点，并将行号保存
			if (PUNCTUATIONS.indexOf(inters[i]) != -1) {
				if (logger.isDebugEnabled())
					logger.debug("p" + i + ":" + sentencees[i]);
				punctuationRows[punctuationRowCount] = i;
				punctuationRowCount++;
			}
		}

		int start = 0, end = 0;
		for (int i = 1; i < punctuationRowCount; i++) {
			if (punctuationRows[i] - punctuationRows[i - 1] < DIVIDE_THRESHOLD) {

				if (start == 0) {
					start = punctuationRows[i - 1];
				}
				if (end == 0 && i == (punctuationRowCount - 1)) {// 表示正文一直持续到最后
					end = punctuationRows[i];
					sbf = new StringBuffer("");
					for (int z = start; z <= end; z++) {
						sbf.append(sentencees[z]);
					}
					String temp = sbf.toString();

					if (longestContent.length() < temp.length()) {// 从中取最长一段
						longestContent = temp;

					}
				}
			} else {// 首次出现相离句子
				if (start != 0 && end == 0) {
					end = punctuationRows[i - 1];
					sbf = new StringBuffer("");
					for (int z = start; z <= end; z++) {
						sbf.append(sentencees[z]);
					}
					String temp = sbf.toString();
					if (logger.isDebugEnabled()) {
						for (int j = punctuationRows[i - 1]; j <= punctuationRows[i]; j++)
							logger.debug("\r\nfoun：" + j + ":" + sentencees[j]);

					}
					logger.info("\r\nfound：" + temp);
					if (longestContent.length() < temp.length()) {// 从中取最长一段
						longestContent = temp;
					}
					start = 0;
					end = 0;
				}
			}
		}

		if (longestContent.length() == 0 && (start != 0 && end == 0)) {
			// 如果正文之后再没有标点符号，则取到最后一个
			sbf = new StringBuffer("");
			end = punctuationRows[punctuationRowCount - 1];
			for (int z = start; z <= end; z++) {
				sbf.append(sentencees[z]);
			}
			longestContent = sbf.toString();

		}
		if (punctuationRowCount == 1) {
			longestContent = sentencees[punctuationRows[0]];
		}

		return longestContent;
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
			} else {
				String _s1 = content.substring(i1, i2);// xxx

				_s1 = _s1.replaceAll("(?s)(?i)<.*?>", "");
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

						if (line.length() > DISTURB_LENGTH) {
							line = linkTextReplacement;
						} else if (guideLinkText.contains(line.replaceAll(
								"&gt;|\\pP|\\s|>", ""))) {
							logger.info("l:" + line);
							line = linkTextReplacement;
						} else {
							line = line.replaceAll("[" + PUNCTUATIONS + "]",
									" ");
						}

					} else {
						line = line.replaceAll("\\s+", " ");
					}
					sbf.append(line).append(SEPERATOR);
				}
			}
		} catch (Exception e) {
			throw e;

		}
		ss = sbf.toString();
		ss = ss.replaceAll("(?s)(?i)<[a-z].*?>", "").replaceAll("&nbsp;?", "")
				.replaceAll("&gt;?", ">").replaceAll("&lt;?", "<").replaceAll(
						"&#149;?", "").replaceAll("&quot;?", "\"").replaceAll(
						"&#183;?", "·").replaceAll("\\s+", " ");// 去掉空格

		return ss;
	}
}
