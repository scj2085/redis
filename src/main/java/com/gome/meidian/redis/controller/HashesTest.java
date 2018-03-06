package com.gome.meidian.redis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.util.Maps;
import org.assertj.core.util.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * Hashes Strings类型的field和value的映射表.它的添加、删除操作都是O(1)(平均)。hash特别适合用于存储对象 对于Hashes
 * key是名称、field是hashmap的key，value是hashmap的value
 */
public class HashesTest {

	

	private Logger logger = LoggerFactory.getLogger(getClass());
	Jedis jedis;
	JedisPool pool;
	
	public HashesTest() {
		
	}
	
	public static void main(String[] args) {
		HashesTest hashesTest = new HashesTest();
		
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379, 0);
		Jedis jedis = pool.getResource();
		hashesTest.testSet();
		jedis.close();
	}

	/**
	 * hset [key] [field] [value] 设置field的value hsetnx [key] [field] [value]
	 * 设置field的value，如果key不存在，则先创建，如果field已经存在返回0，nx是not exist hmset [key]
	 * [field1] [value2] [field2] [value3] 同时设置hash的多个field
	 */
	public void testSet() {
		// hset
		logger.info("hset myhash field1 hello : " + jedis.hset("myhash", "field1", "hello"));
		logger.info("hget myhash field1 : " + jedis.hget("myhash", "field1") + "\n");

		// hsetnx
		logger.info("hget myhash field1 : " + jedis.hget("myhash", "field1"));
		logger.info("hsetnx myhash field1 new : " + jedis.hsetnx("myhash", "field1", "new"));
		logger.info("hget myhash field1 : " + jedis.hget("myhash", "field1"));
		logger.info("hget myhash field2 : " + jedis.hget("myhash", "field2"));
		logger.info("hsetnx myhash field2 world : " + jedis.hsetnx("myhash", "field2", "world"));
		logger.info("hget myhash field2 : " + jedis.hget("myhash", "field2") + "\n");

		// hmset
		logger.info("test hmset");
		Map<String, String> kvs = new HashMap<String, String>();
		kvs.put("field1", "newvalue");
		kvs.put("field3", "value3");
		logger.info("hmset myhash field1 newvalue field3 value3", jedis.hmset("myhash", kvs));
		logger.info("hget myhash field1 : " + jedis.hget("myhash", "field1"));
		logger.info("hget myhash field3 : " + jedis.hget("myhash", "field3") + "\n");

		// del
		logger.info("del myhash : " + jedis.del("myhash"));
	}

	/**
	 * hget [key] [field] 获取指定key,field的value hmget [key] [fields...]
	 * 获取全部指定key,field的value的List集合 hincrby [key] [field] [value]
	 * 通过指定field，给value加上给定的long值，value必须是Integer类型，不可以是浮点数或字符 hincrbyfloat
	 * [key] [field] [value] 通过指定field，给value加上给定的float值，value必须是数字类型
	 */
	public void testGet() {
		// hget
		logger.info("test hget");
		logger.info("hset myhash field1 hello : " + jedis.hset("myhash", "field1", "hello"));
		logger.info("hget myhash field1 : " + jedis.hget("myhash", "field1") + "\n");

		// hmget
		logger.info("test hmget");
		logger.info("hset myhash field2 world : " + jedis.hset("myhash", "field2", "world"));
		logger.info("hmget myhash field1 field2 : " + jedis.hmget("myhash", "field1", "field2") + "\n");

		// hincrby
		logger.info("test hincrby");
		logger.info("hset myhash field3 5 : " + jedis.hset("myhash", "field3", "5"));
		logger.info("hget myhash field3 : " + jedis.hget("myhash", "field3"));
		try {
			logger.info("hincrby myhash field1 1 : " + jedis.hincrBy("myhash", "field1", 1));
		} catch (Exception e) {
			logger.warn("hincrby 必须作用于integer类型", e);
		}
		logger.info("hincrby myhash field3 10 : " + jedis.hincrBy("myhash", "field3", 10));
		logger.info("hget myhash field3 : " + jedis.hget("myhash", "field3"));
		logger.info("hincrby myhash field3 -20 : " + jedis.hincrBy("myhash", "field3", -20));
		logger.info("hget myhash field3 : " + jedis.hget("myhash", "field3") + "\n");

		// hincrbyfloat
		logger.info("test hincybyfloat");
		logger.info("hincrbyfloat myhash field3 0.25 : " + jedis.hincrByFloat("myhash", "field3", 0.25));
		logger.info("hget myhash field3 : " + jedis.hget("myhash", "field3"));

		// del
		logger.info("del myhash : " + jedis.del("myhash"));
	}

	/**
	 * hexists [key] [field] 测试指定field是否存在 返回值boolean hlen [key]
	 * 返回指定hash的field数量，相当于size hdel [key] [field] 删除指定key和field的value，相当于remove
	 * hkeys [key] 返回指定key的hash所有field的Set集合 hvals [key]
	 * 返回指定key的hash所有value的List集合 hgetall [key] 返回全部数据，返回值为Map<K,V>
	 */
	public void testFieldAndValue() {
		// hexists
		logger.info("test hexists");
		logger.info("hexists myhash field1 : " + jedis.hexists("myhash", "field1"));
		logger.info("hset myhash field1 hello : " + jedis.hset("myhash", "field1", "hello"));
		logger.info("hexists myhash field1 : " + jedis.hexists("myhash", "field1") + "\n");

		// hlen
		logger.info("test hlen");
		logger.info("hset myhash field2 world : " + jedis.hset("myhash", "field2", "world"));
		logger.info("hset myhash field3 !! : " + jedis.hset("myhash", "field3", "!!"));
		logger.info("hlen myhash :　" + jedis.hlen("myhash") + "\n");

		// hdel
		logger.info("hdel myhash field3 : " + jedis.hdel("myhash", "field3") + "\n");

		// hkeys
		logger.info("hkeys myhash : " + jedis.hkeys("myhash") + "\n");

		// hvals
		logger.info("hvals myhash : " + jedis.hvals("myhash") + "\n");

		// hgetall
		logger.info("hgetall myhash : " + jedis.hgetAll("myhash") + "\n");

		// del
		logger.info("del myhash : " + jedis.del("myhash") + "\n");
	}

	/**
	 * hscan 扫描
	 * 
	 * @see http://redis.io/commands/scan hscan key 0 MATCH *11* COUNT 1000
	 *      有scan对应String sscan对应Sets hscan对应Hashes zscan对应Sorted Sets
	 *      Lists有lrange返回指定下标的元素
	 */
	public void testHscan() {
		long[] value = new long[] { 1L, 3L, 5L, 7L };
		Jedis jedis = null;
		String key = "key";

		try {
			jedis = pool.getResource();
			jedis.del(key);
			Pipeline pipeline = jedis.pipelined();

			for (int i = 0; i < 10000; i++) {
				pipeline.hincrBy(key, "value_" + i, value[((int) (Math.random() * 4))]);
			}
			List<Object> results = pipeline.syncAndReturnAll();
			logger.info("写入长度 {}", results.size());
		} catch (Exception e) {
			logger.error("写入异常", e);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}

		// 游标初始值为0
		String cursor = "0";
		int i = 0;
		Set<String> values = Sets.newHashSet();
		ScanParams scanParams = new ScanParams();
		scanParams.match("value_*");
		scanParams.count(1000);
		try {
			jedis = pool.getResource();
			do {
				ScanResult<Map.Entry<String, String>> hscan = jedis.hscan(key, cursor, scanParams);
				cursor = hscan.getStringCursor();
				List<Map.Entry<String, String>> result = hscan.getResult();
				if (CollectionUtils.isEmpty(result)) {
					logger.error("本次迭代数量为0");
					continue;
				} else {
					logger.info("本次迭代数量为{}", result.size());
				}
				for (Map.Entry<String, String> entry : result) {
					values.add(entry.getKey());
					i++;
				}
			} while (!"0".equals(cursor));
			logger.info("值个数 {}", values.size());
			logger.info("i 长度 {}", i);
		} catch (Exception e) {
			logger.error("读取异常", e);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}
}
