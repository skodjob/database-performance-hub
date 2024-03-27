package io.skodjob.dmt.sinkSources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XAddParams;
import redis.clients.jedis.params.XReadParams;
import redis.clients.jedis.resps.StreamEntry;

@ApplicationScoped
public class RedisDataSource {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private JedisPool pool;

    @Inject
    public RedisDataSource(@ConfigProperty(name = "data.source.redis.host") String host,
                           @ConfigProperty(name = "data.source.redis.port") Integer port,
                           @ConfigProperty(name = "data.source.redis.pool.max") Integer maxPool) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxPool);
        this.pool = new JedisPool(config, host, port);
    }

    public StreamEntryID sendMessage(String channel, Map<String, String> message) {
        Jedis jedis = pool.getResource();
        return jedis.xadd(channel, message, XAddParams.xAddParams());
    }

    public List<Map.Entry<String, List<StreamEntry>>> pollMessages(int maxAmount, List<String> channels) {

        Jedis jedis = pool.getResource();
        HashMap<String, StreamEntryID> streams = new HashMap<>();
        channels.forEach(channel -> streams.put(channel, new StreamEntryID()));

        return jedis.xread(XReadParams.xReadParams().count(maxAmount), streams);
    }

}
