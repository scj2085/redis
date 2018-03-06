package com.gome.meidian.redis.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
@Configuration
public class JedisConfig {
	@Resource
	private JedisProperties jedisProperties;

	private static Thread shutdown = null;
	/**
	 * jedis 连接池配置
	 * 
	 * @return
	 */
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		//最大连接数, 默认20个
		config.setMaxTotal(20);
		//最小空闲连接数, 默认0
		config.setMinIdle(0);
		//获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted), 如果超时就抛异常, 小于零:阻塞不确定的时间, 默认 - 1
		config.setMaxWaitMillis(1000L);
		//最大空闲连接数, 默认20个
		config.setMaxIdle(5);
		//在获取连接的时候检查有效性, 默认false
		config.setTestOnBorrow(Boolean.TRUE);
		config.setTestOnReturn(Boolean.FALSE);
		//在空闲时检查有效性, 默认false
		config.setTestWhileIdle(Boolean.TRUE);
		//逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
		config.setMinEvictableIdleTimeMillis(1800000);
		//每次逐出检查时 逐出的最大数目 如果为负数就是: idleObjects.size / abs(n), 默认3
		config.setNumTestsPerEvictionRun(3);
		//对象空闲多久后逐出, 当空闲时间 > 该值 且 空闲连接>最大空闲数 时直接逐出, 不再根据MinEvictableIdleTimeMillis判断 (默认逐出策略)
		config.setSoftMinEvictableIdleTimeMillis(1800000);
		//逐出扫描的时间间隔(毫秒) 如果为负数, 则不运行逐出线程, 默认 - 1
		config.setTimeBetweenEvictionRunsMillis(60000);

 		config.setBlockWhenExhausted(true);
		return config;
	}

	/**
	 * 集群配置
	 * @return
	 */
	@Bean
	public JedisCluster jedisCluster() {
		Set<HostAndPort> nodes = new HashSet<>();
 		List<String> servers = jedisProperties.getServers();
		servers.forEach(server -> {
			String[] hostport = server.split(":");
			nodes.add(new HostAndPort(hostport[0], Integer.valueOf(hostport[1])));
		});

		final JedisCluster cluster = new JedisCluster(nodes, jedisPoolConfig());

		if (shutdown == null) {
			shutdown = new Thread(new Runnable() {

				private Logger logger = LoggerFactory.getLogger(this.getClass());
				// Clean up at exit
				@Override
				public void run() {
					logger.info(JedisConfig.class.getSimpleName() + " shutdown");

					try {
						cluster.close();
					} catch (Exception e) {
						logger.error("shutdown jedis cluster", e);
					}
				}
			});

			Runtime.getRuntime().addShutdownHook(shutdown);
		}
		return cluster;
	}
	
	 
}
