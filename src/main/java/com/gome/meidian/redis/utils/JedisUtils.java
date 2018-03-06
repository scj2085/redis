package com.gome.meidian.redis.utils;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;


public class JedisUtils {
    private static final Logger logger = LoggerFactory.getLogger(JedisUtils.class);
    
    public static <T> T process(JedisPool pool, Function<Jedis, T> function) {
        Jedis jedis = pool.getResource();

        T retVal = null;
        try {
            // 执行逻辑
            retVal = function.apply(jedis);

        } catch (Exception e) {
            logger.error("jedis error", e);

        } finally {
            jedis.close();
        }

        return retVal;
    }

    public static <T> T process(JedisCluster cluster, Function<JedisCluster, T> function) {
        T retVal = null;
        try {
            // 执行逻辑
            retVal = function.apply(cluster);

        } catch (Exception e) {
            logger.error("jedis error", e);

        } finally {
        }

        return retVal;
    }
}
