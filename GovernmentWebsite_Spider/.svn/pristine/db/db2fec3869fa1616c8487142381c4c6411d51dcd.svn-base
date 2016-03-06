package com.meiah.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripper;

import com.heaton.bot.Attribute;
import com.heaton.bot.HTTP;
import com.heaton.bot.HTTPSocket;

/***
 * pdf解析工具类
 * 注: 此工具支持PDF1.4及以上版本
 * @author liubb
 *
 */
public class PDFPaserUtil {
	private static final Logger LOGGER = Logger.getLogger(PDFPaserUtil.class);
	
	/***
	 * 根据远程HTTP连接 解析PDF为文本
	 * @param url
	 * @return
	 */
	public static String pasePDF2TextByHttp(String url){
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
			return paserPDF2TextByStream(new ByteArrayInputStream(_buff1));
		}else{
			return "";
		}
	}
	/***
	 * 根据远程HTTP连接 解析PDF为文本,解不开则保存在本地
	 * @param url
	 * @return
	 */
	public static String pasePDF2TextByHttpToFile(String url){
		String pdfName = "";
		String fileName = "";
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
		FileOutputStream file_out = null;
		try {
			pdfName = MD5Utils.getMD5(url.getBytes());
			fileName = PDFConfig.getPdfPath();
			if(!fileName.endsWith("/")) {
				fileName = fileName + "/";
			}
			File file = new File(fileName);
			if(!file.exists() && !file.isDirectory()) {
				file.mkdir();
			} 
			file_out = new FileOutputStream(new File(fileName + pdfName + ".pdf"));
			file_out.write(_buff1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != file_out) {
				try {
					file_out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(_buff1!=null){
			return paserPDF2TextByStream(new ByteArrayInputStream(_buff1));
		}else{
			return "";
		}
	}
	
	/**
	 * 将pdf 文件转换成文本
	 * @param fileName
	 * @return
	 */
	public static String paserPDF2TextByName(String fileName){
		if(!StringUtils.isEmpty(fileName)){
			return paserPDF2TextByFile(new File(fileName));
		}else{
			return "";
		}
	}
	
	/**
	 * 将pdf 文件转换成文本
	 * @param file
	 * @return
	 */
	public static String paserPDF2TextByFile(File file){
		if(file==null || !file.exists()){
			return "";
		}
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOGGER.error("解析PDF过程中发生异常!"+e);
		} finally {
			if(null != input) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return paserPDF2TextByStream(input);
	}
	
	/**
	 * 将pdf 文件转换成文本
	 * @param input
	 * @return
	 */
	public static String paserPDF2TextByStream(InputStream input){
		PDFTextStripper stripper = null;
		PDDocument  document = null;
		try {
			document = PDDocument.load(input);
			stripper = new PDFTextStripper(); 
			return stripper.getText(document);
		} catch (IOException e) {
			LOGGER.error("解析PDF文件流过程中发生异常!"+e);
		}finally{
			try {
				if(document !=null)document.close();
			} catch (IOException e) {
				LOGGER.error("关闭PDF解析文件流过程中发生异常!"+e);
			}
		}
		return "";
	}
	
	/***
	 * 根据远程HTTP链接 解析PDF为HTML
	 * @param url
	 * @return
	 */
	public static String pasePDF2HTMLByHttp(String url){
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
			return paserPDF2HTMLByStream(new ByteArrayInputStream(_buff1));
		}else{
			return "";
		}
	}
	
	/**
	 * PDF文件流,转换成html
	 * @param fileName
	 * @return
	 */
	public static String paserPDF2HTMLByName(String fileName){
		return paserPDF2HTMLByFile(new File(fileName));
	}
	
	/**
	 * PDF文件流,转换成html
	 * @param file
	 * @return
	 */
	public static String paserPDF2HTMLByFile(File file){
		if(file==null || !file.exists()){
			return "";
		}
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOGGER.error("解析PDF过程中发生异常!"+e);
		} finally {
			if(null != input) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return paserPDF2HTMLByStream(input);
	}
	
	/**
	 * PDF文件流,转换成html
	 * @param input
	 * @return
	 */
	public static String paserPDF2HTMLByStream(InputStream input){
		PDFText2HTML stripper = null;
		PDDocument  document = null;
		try {
			document = PDDocument.load(input);
			stripper = new PDFText2HTML(""); 
			return decode(stripper.getText(document));
		} catch (IOException e) {
			LOGGER.error("解析PDF过程中发生异常!"+e);
		}finally{
			try {
				if(document !=null)document.close();
			} catch (IOException e) {
				LOGGER.error("关闭PDF解析过程中发生异常!"+e);
			}
		}
		return "";
	}
	
	/***
	 * 获取PDF文档信息
	 * @param fileName
	 * @return
	 */
	public static PDDocumentInformation getPDFInfoByName(String fileName){
		return getPDFInfoByFile(new File(fileName));
	}
	
	/***
	 * 获取PDF文档信息
	 * @param file
	 * @return
	 */
	public static PDDocumentInformation getPDFInfoByFile(File file){
		if(file==null || !file.exists()){
			return null;
		}
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOGGER.error("解析PDF过程中发生异常!"+e);
		} finally {
			if(null != input) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return getPDFInfoByStream(input);
	}
	
	/***
	 * 获取PDF文档信息
	 * @param input
	 * @return
	 */
	public static PDDocumentInformation getPDFInfoByStream(InputStream input){
		PDDocument  document = null;
		try {
			document = PDDocument.load(input);
			return document.getDocumentInformation();
		} catch (IOException e) {
			LOGGER.error("解析PDF过程中发生异常!"+e);
		}finally{
			try {
				if(document !=null)document.close();
			} catch (IOException e) {
				LOGGER.error("关闭PDF解析过程中发生异常!"+e);
			}
		}
		return null;
	}
	
	/***
	 * 获取PDF文件中的图片
	 * @param fileName
	 * @return
	 */
	public static List<PDXObjectImage> writePDFImage(String fileName,String path){
		return writePDFImage(new File(fileName),path);
	}
	
	/***
	 * 获取PDF文件中的图片
	 * @param file
	 * @return
	 */
	public static List<PDXObjectImage> writePDFImage(File file,String path){
		if(file==null || !file.exists()){
			return null;
		}
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOGGER.error("解析PDF过程中发生异常!"+e);
		}
		return writePDFImage(input,path);
	}
	
	
	/***
	 * 获取PDF文件中的图片
	 * @param input
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public static List<PDXObjectImage> writePDFImage(InputStream input,String path){
		List<PDXObjectImage> list = new ArrayList<PDXObjectImage>();
		PDDocument  document = null;
		try {
			document = PDDocument.load(input);
			
			/** 文档页面信息 **/    
	        PDDocumentCatalog cata = document.getDocumentCatalog();     
	        List<PDPage> pages = cata.getAllPages();
	        int count = 0;
	        for (PDPage page : pages) {
	        	if( null != page ){     
	                PDResources res = page.findResources();     
	                //获取页面图片信息     
	                Map imgs = res.getImages();     
	                if( null != imgs ){     
	                    Set keySet = imgs.keySet();     
	                    Iterator<PDXObjectImage> it = keySet.iterator();     
	                    while( it.hasNext() ){     
	                        Object obj =  it.next();     
	                        PDXObjectImage img = ( PDXObjectImage ) imgs.get( obj );
	                        img.write2file(path+"pdf_image_"+count);   
	                        count++;
	                    }     
	                }     
	            }    
			}
		} catch (IOException e) {
			LOGGER.error("解析PDF过程中发生异常!"+e);
		}finally{
			try {
				if(document !=null)document.close();
			} catch (IOException e) {
				LOGGER.error("关闭PDF解析过程中发生异常!"+e);
			}
		}
		return list;
	}
	
	/***
	 * 解码HTML
	 * @param str
	 * @return
	 */
	private static String decode(String str){
		String[] tmp = str.split(";&#|&#|;");
		StringBuffer sb = new StringBuffer("");
		for (int i=0; i<tmp.length; i++ ){
			if (tmp[i].matches("\\d{5}")){
				sb.append((char)Integer.parseInt(tmp[i]));
			} else {
				sb.append(tmp[i]);
				}
		}
		return sb.toString();
	 }
	
	public static void main(String[] args) {
		String tempUrl = "http://epaper.01ny.cn/http_rb/page/1/2015-12/22/A1/20151222A1_pdf.pdf";
//		String str = tempUrl.substring(tempUrl.lastIndexOf("/") + 1 , tempUrl.lastIndexOf(".pdf"));
//		str = URLEncoder.encode(str);
//		String prefix = tempUrl.substring(0,tempUrl.lastIndexOf("/") + 1);
//		tempUrl = prefix + str + ".pdf";
		String webContent = PDFPaserUtil.pasePDF2HTMLByHttp(tempUrl);
		String web = PDFPaserUtil.pasePDF2TextByHttp(tempUrl);
//		System.out.println(web);
//		System.out.println(webContent);
	}
}
