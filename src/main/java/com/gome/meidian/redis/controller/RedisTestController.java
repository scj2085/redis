package com.gome.meidian.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gome.meidian.redis.utils.JedisUtil;
import com.gome.meidian.redis.utils.JedisUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

@RestController
public class RedisTestController {

	@Autowired
	private JedisCluster jedisCluster;
	
	public static void main(String[] args) {  
//	    Jedis jedis = new Jedis("127.0.0.1", 6379, 1000);  
//	    jedis.set(cluster,"redis", "redis value");  
//	    String value = jedis.get(cluster,"redis");  
//	    System.err.println("redis=" + value);  
	}
	
	@RequestMapping(value = "/test", method=RequestMethod.GET)
	public void test(){
		// 添加
		JedisUtils.process(jedisCluster, jedis -> {
			jedis.set("key", "value");
			return null;
		});
		String aa = JedisUtils.process(jedisCluster, jedis -> {
			String a = jedis.get("key");
			return a;
		});
		System.err.println(aa);
	}
	
}
