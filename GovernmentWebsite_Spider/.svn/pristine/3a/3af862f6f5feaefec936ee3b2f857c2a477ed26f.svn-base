package com.meiah.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.heaton.bot.Attribute;
import com.heaton.bot.HTTP;
import com.heaton.bot.HTTPSocket;

public class WordPaserUtil {
	
	private static final Logger LOGGER = Logger.getLogger(WordPaserUtil.class);
	
	/***
	 * 根据远程HTTP连接 解析word为文本
	 * @param url
	 * @return
	 */
	public static String paseDOCXTextByHttp(String url){
		String content = "";
		HTTP _http = new HTTPSocket();
		_http.setUseCookies(true, true);
		_http.setTimeout(60 * 1000);
		_http.getClientHeaders().add(new Attribute("Accept-Encoding", "gzip"));
		_http.getClientHeaders().add(new Attribute("Accept-Language", "*"));
		_http.getClientHeaders().add(new Attribute("Connection", "close"));
		_http.setAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.3; .NET CLR 2.0.50727)");
		_http.SetAutoRedirect(false);
		try {
			_http.send(url, null);
		} catch (Exception e) {
//			System.out.println("发送请求:"+url+"\t过程中发生异常:\n"+e);
		} 
		byte[] _buff1= (_http.getBodyBytes());
		if(_buff1!=null){
			if(url.endsWith("doc")) {
				content = paserDOCTextByStream(new ByteArrayInputStream(_buff1));
			} else if(url.endsWith("docx")) {
				content = paserDOCXTextByStream(new ByteArrayInputStream(_buff1));
			}
		}else{
			return "";
		}
		return content; 
	}
	/**
	 * 将docx 文件转换成文本
	 * @param input
	 * @return
	 */
	public static String paserDOCXTextByStream(InputStream input){
		StringBuffer wordContent = new StringBuffer();
		try {
			XWPFDocument doc = new XWPFDocument(input);
			List<XWPFParagraph> paras = doc.getParagraphs();  
		      for (XWPFParagraph para : paras) {  
		         //当前段落的属性  
		    	 wordContent = wordContent.append(para.getText());
		      } 
		      //获取文档中所有的表格  
		      List<XWPFTable> tables = doc.getTables();  
		      List<XWPFTableRow> rows;  
		      List<XWPFTableCell> cells;  
		      if(tables.size() > 0) {
		    	  for (XWPFTable table : tables) {  
		    		  //表格属性  
		    		  //获取表格对应的行  
		    		  rows = table.getRows();  
		    		  for (XWPFTableRow row : rows) {  
		    			  //获取行对应的单元格  
		    			  cells = row.getTableCells();  
		    			  for (XWPFTableCell cell : cells) {  
//		    				  wordContent.append("\r\n");
		    				  wordContent.append(cell.getText());
		    			  }  
		    		  }  
		    	  }  
		      }
			return wordContent.toString();
		} catch (IOException e) {
			LOGGER.error("解析word文件流过程中发生异常!"+e);
		}
		return "";
	}
	/**
	 * 将doc 文件转换成文本
	 * @param input
	 * @return
	 */
	public static String paserDOCTextByStream(InputStream input){
		try {
			WordExtractor word = new WordExtractor(input);
			return word.getText();
		} catch (IOException e) {
			LOGGER.error("解析word文件流过程中发生异常!"+e);
		}
		return "";
	}
	
	public static void main(String[] args) {
		String url = "http://www.jlass.org.cn/files/201509/250950320.docx";
		String content = paseDOCXTextByHttp(url);
//		System.out.println(content);
	}
}
