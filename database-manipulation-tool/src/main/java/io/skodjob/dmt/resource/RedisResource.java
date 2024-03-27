package io.skodjob.dmt.resource;

import java.util.List;
import java.util.Map;

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
    @GET
    public Response pollMessages(@QueryParam("max") Integer max, List<String> channels) {
        List<Map.Entry<String, List<StreamEntry>>> res = redisService.get(max, channels);
        return Response.ok(res).build();
    }

}
