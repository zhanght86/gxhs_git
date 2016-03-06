//package com.meiah.test;
//
//import java.io.IOException;
//import java.security.NoSuchAlgorithmException;
//
//import com.meiah.util.BdbUriUniqFilter;
//
//public class testBDB extends Thread {
//	BdbUriUniqFilter dbd = null;
//
//	public testBDB() throws IOException {
//		long start1 = System.currentTimeMillis();
//		String envHome = BDB_PATH;
//		dbd = new BdbUriUniqFilter(envHome);
//		System.out.println("1: " + (System.currentTimeMillis() - start1));
//
//		// long start2 = System.currentTimeMillis();
//		// for (int i = 0; i < 2000000; i++) {
//		// dbd.addUrl(i + "");
//		// }
//		//		System.out.println("1: " + (System.currentTimeMillis() - start2));
//	}
//
//	@Override
//	public void run() {
//		while (true) {
//		}
//	}
//
//	private static final String BDB_PATH = ClassLoader.getSystemResource("")
//			.toString().replaceAll("file:/", "")
//			+ "BDB";// 配置文件
//
//	public static void main(String[] args) throws IOException,
//			InterruptedException, NoSuchAlgorithmException {
//
//		/*
//		 * EnvironmentConfig envConfig = new EnvironmentConfig(); // 如果不存在则创建一个
//		 * envConfig.setAllowCreate(true); envConfig.setTransactional(false);
//		 * envConfig.setLocking(false); File envHome = new File("F:/frontier");
//		 * if (!envHome.exists()) { envHome.mkdir(); }
//		 * 
//		 * Environment env = new Environment(envHome, envConfig);
//		 */
//
//		testBDB tb = new testBDB();
//		System.out.println(tb.dbd.isUrlExist(1 + ""));
//		tb.start();
//		// System.out.println("1: " + (System.currentTimeMillis() - start2));
//		// String url =
//		// "http://tieba.baidu.com/f?z=804940489&ct=335544320&lm=0&sc=0&rn=30&tn=baiduPostBrowser&word=%C4%A7%CA%DE%CA%C0%BD%E7&pn=539670";
//		// String url1 = "http://tieba.baidu.com/f?kz=1050217044";
//
//		/*
//		 * System.out.println(dbd.setAdd("2"));
//		 * System.out.println(dbd.setAdd(url));
//		 * System.out.println(dbd.setAdd(url1));
//		 */
//		//		
//		// System.out.println(dbd.setAdd(url));
//		// System.out.println(dbd.setAdd(url1));
//		/*
//		 * long start = System.currentTimeMillis();
//		 * System.out.println(dbd.setAdd(url)); System.out.println("2: " +
//		 * (System.currentTimeMillis() - start)); long start2 =
//		 * System.currentTimeMillis(); System.out.println(dbd.setAdd(url));
//		 * System.out.println("2: " + (System.currentTimeMillis() - start2));
//		 */
//		/*
//		 * for (int i = 0; i < 5000000; i++) { if (i % 100000 == 0) { long
//		 * start2 = System.currentTimeMillis(); dbd.serializ("F:\\dd");
//		 * System.out.println("1: " + (System.currentTimeMillis() - start2)); }
//		 * long start = System.currentTimeMillis(); //
//		 * MD5Builder.getMD5("1wwwwwwwwwwwwwwwwwwwwwwwwwaaaaaaaaaaa" + i)
//		 * System.out.println(dbd.setAdd("1wwwwwwwwwwwwwwwwwwwwwwwwwaaaaaaaaaaa" +
//		 * i)); // System.out.println("md5: " +
//		 * MD5Builder.getMD5("1wwwwwwwwwwwwwwwwwwwwwwwwwaaaaaaaaaaa" + i)); //
//		 * dbd.serializ("F:\\dd1"); System.out.println("2: " +
//		 * (System.currentTimeMillis() - start)); }
//		 */
//		// System.out.println(dbd.size());
//	}
//}
