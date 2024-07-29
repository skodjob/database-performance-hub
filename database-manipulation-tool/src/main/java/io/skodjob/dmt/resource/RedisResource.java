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

package io.skodjob.dmt.resource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.skodjob.dmt.service.RedisService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import redis.clients.jedis.resps.StreamEntry;

@Path("Redis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@RegisterForReflection
public class RedisResource {

    @Inject
    RedisService redisService;

    @Path("sendMessage")
    @POST
    public Response sendMessage(@QueryParam("channel") String channel, Map<String, String> message) {
        String newId = redisService.insert(channel, message);
        return Response.ok(newId).build();
    }

    @Path("pollMessages")
    @POST
    public Response pollMessages(@QueryParam("max") Integer max, List<String> channels) {
        List<Map.Entry<String, List<StreamEntry>>> res = redisService.get(max, channels);
        return Response.ok(res).build();
    }

    @Path("readHash")
    @GET
    public Response readHash(@QueryParam("hashKey") String hashKey) {
        Map<String, String> map = redisService.readHash(hashKey);
        if (Objects.isNull(map)) {
            return Response.serverError().build();
        }
        return Response.ok(map).build();
    }

    @Path("reset")
    @GET
    public Response resetRedis() {
        redisService.resetRedis();
        return Response.ok().build();
    }

}
