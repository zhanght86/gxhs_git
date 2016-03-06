package com.meiah.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.meiah.util.JavaUtil;

public class TestEncode {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		byte[] buff = new byte[10000];
		//OneSiteCrawler cc = new OneSiteCrawler("13");
		// 以ZipEntry为参数得到一个InputStream，
		String zzdhtml = "";
		File dir = new File("E:\\huhb\\1");
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
//				System.out.println("file name: " + files[i].getName());
				InputStream in;
				try {
					in = new BufferedInputStream(new FileInputStream(files[i]));
					try {
						in.read(buff);

					} catch (Exception e) {

					} finally {
						try {
							in.close();
						} catch (Exception _e1) {
						}
					}
					if (buff != null)
						try {
							zzdhtml = JavaUtil.readBytes(buff, 0);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//					System.out.println("file text: " + zzdhtml);

				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

	}
}
