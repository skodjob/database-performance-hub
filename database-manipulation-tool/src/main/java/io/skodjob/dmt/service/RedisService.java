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

    public Map<String, String> readHash(String hashKey) {
        return redisDataSource.readHash(hashKey);
    }

    public void resetRedis() {
        redisDataSource.flushRedis();
    }
}
