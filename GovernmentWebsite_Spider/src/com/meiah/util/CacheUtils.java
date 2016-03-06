package com.meiah.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.MemcachedClientCallable;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * 缓存管理单元
 * @author liuyao
 *
 */
public class CacheUtils {
	
	private final static Log log = LogFactory.getLog(CacheUtils.class);

	private static MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("127.0.0.1:11211")); 
	private static MemcachedClient memcachedClient; 
	
	static {
		try {
			memcachedClient = builder.build();
		} catch (IOException e) {
			log.error("缓存初始化出现异常: ", e);
		}
	}
	
	/**
	 * 初始化缓存
	 */
	public static void init() {
		
	}
	
	/**
	 * 获取缓存
	 * @param key 关键字
	 * @return
	 */
	public static <T> T get(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		try {
			return memcachedClient.get(key);
		} catch (Exception e) {
			log.error("获取缓存" + key + "出现异常: ", e);
		}
		return null;
	}
	
    /**
     * 获取缓存中内容
     * @param <T>
     * @param key 关键字
     * @param callable 回调函数执行缓存SQL结果
     * @return
     */
	public static <T> T get(String key, Callable<T> callable) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
    	try {
    		T cache = memcachedClient.get(key);
    		if (cache == null) {
        		cache = callable.call();
        		if (cache != null) {
        			set(key, cache);
        		}
    		}
    		return cache;
		} catch (Exception e) {
			log.error("加载缓存项: " + key + " 出现错误, 异常: ", e);
		}
    	return null;
    }
	
    /**
     * 获取缓存中内容
     * @param <T>
     * @param key 关键字
     * @param namespace 命名空间
     * @param callable 回调函数执行缓存SQL结果
     * @return
     */
	public static <T> T get(final String key, CacheEnum namespace, Callable<T> callable) {
		if (StringUtils.isBlank(key) || namespace == null) {
			return null;
		}
    	try {
    		T cache = memcachedClient.withNamespace(namespace.getValue(), new MemcachedClientCallable<T>() {
				public T call(MemcachedClient client) throws MemcachedException, InterruptedException, TimeoutException {
					return client.get(key);
				}
			});
    		if (cache == null && callable != null) {
        		cache = callable.call();
        		if (cache != null) {
        			set(key, cache, namespace);
        		}
    		}
    		return cache;
		} catch (Exception e) {
			log.error("加载缓存项: " + key + " 出现错误, 异常: ", e);
		}
    	return null;
    }
	
    /**
     * 获取缓存中数据字典值
     * @param <T>
     * @param key 关键字
     * @return
     */
	public static Map<String, String> getByDict(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		return get(key, CacheEnum.CACHE_NAMESPACE_GLOBAL_IPNAME);
	}
	
    /**
     * 获取缓存中数据字典值
     * @param <T>
     * @param key 关键字
     * @param refCode code
     * @return
     */
	public static String getByDictByCode(String key, Object refCode) {
		if (StringUtils.isBlank(key) || refCode == null) {
			return null;
		}
		Map<String, String> transferMap = getByDict(key);
		if (transferMap != null && !transferMap.isEmpty()) {
			return transferMap.get(ObjectUtils.toString(refCode));
		}
		return null;
	}
	
    /**
     * 获取缓存中内容
     * @param <T>
     * @param key 关键字
     * @param namespace 命名空间
     * @return
     */
	public static <T> T get(String key, CacheEnum namespace) {
		return get(key, namespace, null);
	}
	
	/**
	 * 设置缓存
	 * @param key 关键字
	 * @param obj 实体对象
	 * @param expire 失效时间
	 * @return
	 */
	public static boolean set(String key, Object obj, int expire) {
		if (StringUtils.isBlank(key) || obj == null) {
			return false;
		}
		try {
			return memcachedClient.set(key, expire, obj);
		} catch (Exception e) {
			log.error("获取缓存" + key + "出现异常: ", e);
		}
		return false;
	}
	
	/**
	 * 设置缓存
	 * @param key 关键字
	 * @param obj 实体对象
	 * @param expire 失效时间
	 * @param namespace 命名空间
	 * @return
	 */
	public static boolean set(final String key, final Object obj, final int expire, CacheEnum namespace) {
		if (StringUtils.isBlank(key) || namespace == null || obj == null) {
			return false;
		}
		try {
			return memcachedClient.withNamespace(namespace.getValue(), new MemcachedClientCallable<Boolean>() {
				public Boolean call(MemcachedClient client) throws MemcachedException, InterruptedException, TimeoutException {
					return client.set(key, expire, obj);
				}
			});
		} catch (Exception e) {
			log.error("获取缓存" + key + "出现异常: ", e);
		}
		return false;
	}
	
	/**
	 * 设置缓存
	 * @param key 关键字
	 * @param obj 实体对象
	 * @param namespace 命名空间
	 * @return
	 */
	public static boolean set(final String key, final Object obj, CacheEnum namespace) {
		return set(key, obj, 0, namespace);
	}
	
	/**
	 * 设置缓存
	 * @param key 关键字
	 * @param obj 实体对象
	 * @return
	 */
	public static boolean set(String key, Object obj) {
		return set(key, obj, 0);
	}
	
	/**
	 * 删除指定缓存
	 * @param key 关键字
	 */
	public static boolean remove(String key) {
		if (StringUtils.isBlank(key)) {
			return false;
		}
		try {
			return memcachedClient.delete(key);
		} catch (Exception e) {
			log.error("缓存失效出现异常: ", e);
		}
		return false;
	}
	
	/**
	 * 删除指定缓存. 命名空间为空则删除全局中的key
	 * @param key 关键字
	 * @param namespace 命名空间
	 * @return
	 */
	public static boolean remove(final String key, String namespace) {
		if (StringUtils.isBlank(key)) {
			return false;
		}
		try {
			if (StringUtils.isBlank(namespace)) {
				return remove(key);
			} else {
				return memcachedClient.withNamespace(namespace, new MemcachedClientCallable<Boolean>() {
					public Boolean call(MemcachedClient client) throws MemcachedException, InterruptedException, TimeoutException {
						return client.delete(key);
					}
				});
			}
		} catch (Exception e) {
			log.error("获取缓存" + key + "出现异常: ", e);
		}
		return false;
	}
	
	/**
	 * 缓存失效
	 * @param namespace 命名空间
	 */
	public static boolean invalidate(CacheEnum namespace) {
		if (namespace == null) {
			return false;
		}
		try {
			memcachedClient.invalidateNamespace(namespace.getValue());
			return true;
		} catch (Exception e) {
			log.error("缓存失效出现异常: ", e);
		}
		return false;
	}
	
	/**
	 * 缓存失效
	 */
	public static boolean clear() {
		try {
			memcachedClient.flushAll();
			return true;
		} catch (Exception e) {
			log.error("缓存失效出现异常: ", e);
		}
		return false;
	}
	
	/**
	 * 缓存统计
	 */
	public static Map<InetSocketAddress, Map<String,String>> stats() {
		try {
			return memcachedClient.getStats();
		} catch (Exception e) {
			log.error("缓存统计出现异常: ", e);
		}
		return null;
	}
	
	static enum CacheEnum {
		CACHE_NAMESPACE_GLOBAL_IPNAME("CACHE_NAMESPACE_GLOBAL_IPNAME", "ipName");
		String key;
		String value;
		private CacheEnum(String key, String value) {
			this.key = key;
			this.value = value;
		}
		public String getValue() {
			return value;
		}
	}
	
	public static void main(String[] args) {
		
		Object o = get("a");
//		System.out.println(o);
		String a = "vvvv";
		set("a", a);
		o = get("a");
//		.println(o);
		
		Map<InetSocketAddress, Map<String, String>> c = stats();
//		System.out.println(c);
		
		
		Map<String, String> ipNameMapping = new HashMap<String, String>(){{
			put("111.111.111.111", "北京");
			put("111.111.111.112", "北京1");
		}};
//		set("ipNameMapping", ipNameMapping);
		
		Map<String, String> ipNameMapping1 = get("ipNameMapping");
		
		
//		.println(ipNameMapping1.get("111.111.111.111"));
//		System.out.println(ipNameMapping1.get("111.111.111.112"));
		
		
		
	}
	
//	public static void all() {
//		KeyIterator it = memcachedClient.getke.getKeyIterator(AddrUtil.getOneAddress("localhost:11211"));
//		while (it.hasNext())
//		{
//		   String key=it.next();
//		}
//	}
	
}
