/*
 *
 * MIT License
 *
 * Copyright (c) [2024] [Ondrej Babec <ond.babec@gmail.com>, Jiri Novotny <novotnyjirkajn@gmail.com>]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE
 * ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    public Map<String, String> readHash(String hashKey) {
        Jedis jedis = pool.getResource();
        return jedis.hgetAll(hashKey);
    }

    public void flushRedis() {
        Jedis jedis = pool.getResource();
        jedis.flushAll();
    }

}
