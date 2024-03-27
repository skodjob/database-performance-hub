package io.skodjob.dmt.service;

import java.util.List;
import java.util.Map;

import io.skodjob.dmt.sinkSources.RedisDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import redis.clients.jedis.resps.StreamEntry;

@ApplicationScoped
public class RedisService {
    @Inject
    RedisDataSource redisDataSource;

    public String insert(String channel, Map<String, String> message) {
        return redisDataSource.sendMessage(channel, message).toString();
    }

    public List<Map.Entry<String, List<StreamEntry>>> get(int amount, List<String> channels) {
        return redisDataSource.pollMessages(amount, channels);
    }
}
