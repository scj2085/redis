package com.gome.meidian.redis.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jedis")
public class JedisProperties {

	private List<String> servers = new ArrayList();

	public List<String> getServers() {
		return this.servers;
	}

}
