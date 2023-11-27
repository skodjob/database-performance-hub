/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.skodjob.dmt.schema.LoadResult;
import io.skodjob.dmt.service.AsyncMainService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.parser.DmtSchemaJsonParser;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.resteasy.reactive.RestQuery;

@Path("Main")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@RegisterForReflection
public class MainResource {

    @Inject
    AsyncMainService mainService;

    @Inject
    DmtSchemaJsonParser parser;

    @ConfigProperty(name = "onstart.reset.database", defaultValue = "false")
    boolean resetDatabase;

    private static final Logger LOG = Logger.getLogger(MainResource.class);

    @Path("Insert")
    @POST
    public Response insert(JsonObject inputJsonObj) {
        LOG.debug("Received INSERT request");
        try {
            DatabaseEntry dbEntity = parser.parse(inputJsonObj);
            mainService.insert(dbEntity);
            return Response.ok().build();

        }
        catch (Exception ex) {
            return Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("CreateTable")
    @POST
    public Response createTable(JsonObject inputJsonObj) {
        LOG.debug("Received CREATE TABLE IF DOES NOT EXIST or ALTER TABLE IF EXISTS request");
        try {
            DatabaseEntry dbEntity = parser.parse(inputJsonObj);
            mainService.createTable(dbEntity);
            return Response.ok().build();

        }
        catch (Exception ex) {
            return Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("CreateTableAndUpsert")
    @POST
    public Response createTableAndUpsert(JsonObject inputJsonObj) {
        LOG.debug("Received CREATE TABLE If does not exist and UPSERT request");
        try {
            DatabaseEntry dbEntity = parser.parse(inputJsonObj);
            mainService.upsert(dbEntity);
            return Response.ok().build();
        }
        catch (Exception ex) {
            return Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("DropTable")
    @DELETE
    public Response dropTable(JsonObject inputJsonObj) {
        LOG.debug("Received DROP TABLE request");
        try {
            DatabaseEntry dbEntity = parser.parse(inputJsonObj);
            mainService.dropTable(dbEntity);
            return Response.ok().build();
        }
        catch (Exception ex) {
            return Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("ResetDatabase")
    @GET
    public Response resetDatabase() {
        LOG.debug("Received RESET DATABASE request");
        try {
            mainService.resetDatabase();
            return Response.ok().build();
        }
        catch (Exception ex) {
            return Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("TimedInsert")
    @POST
    public Response timedInsert(JsonObject inputJsonObj) {
        LOG.debug("Received TIMED INSERT request");
        try {
            DatabaseEntry dbEntity = parser.parse(inputJsonObj);
            JsonObject obj = mainService.timedInsert(dbEntity);
            LOG.debug("Responded to TIMED INSERT request");
            return Response.ok().entity(obj.toString()).build();
        }
        catch (Exception ex) {
            LOG.debug("Could not do timed insert");
            return Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("GenerateLoad")
    @Consumes()
    @POST
    public Response generateLoad(@RestQuery int count, @RestQuery int maxRows) {
        LOG.debug("Received generate load request");
        if (count == 0|| maxRows == 0) {
            return Response.noContent().status(Response.Status.BAD_REQUEST).build();
        }
        long start = System.currentTimeMillis();
        long[] time = mainService.createAndExecuteLoad(count, maxRows);
        long totalTime = System.currentTimeMillis() - start;
        return generateLoadJsonResponse(totalTime, time[0], time[1]);
    }

    @Path("GenerateBatchLoad")
    @Consumes()
    @POST
    public Response generateBatchLoad(@RestQuery int count, @RestQuery int maxRows) {
        LOG.debug("Received generate load and use batch request");
        if (count == 0|| maxRows == 0) {
            return Response.noContent().status(Response.Status.BAD_REQUEST).build();
        }
        long start = System.currentTimeMillis();
        long[] time = mainService.createAndExecuteBatchLoad(count, maxRows);
        long totalTime = System.currentTimeMillis() - start;
        return generateLoadJsonResponse(totalTime, time[0], time[1]);
    }

    @Path("GenerateMongoBulkSizedLoad")
    @Consumes()
    @POST
    public Response generateMongoBulkSizedLoad(@RestQuery int count, @RestQuery int maxRows, @RestQuery int messageSize) {
        LOG.debug("Received generate mongo bulk load with custom message size request");
        if (count == 0|| maxRows == 0 || messageSize == 0) {
            return Response.noContent().status(Response.Status.BAD_REQUEST).build();
        }
        long start = System.currentTimeMillis();
        long time = mainService.createAndExecuteSizedMongoLoad(count, maxRows, messageSize);
        long totalTime = System.currentTimeMillis() - start;
        return generateLoadJsonResponse(totalTime, 0, time);
    }
    @Path("GenerateMongoBulkSizedLoadParallel")
    @Consumes()
    @POST
    public Response generateMongoBulkSizedLoadParallel(@RestQuery int count, @RestQuery int maxRows, @RestQuery int messageSize) {
        LOG.debug("Received generate mongo bulk load parallel with custom message size request");
        if (count == 0|| maxRows == 0 || messageSize == 0) {
            return Response.noContent().status(Response.Status.BAD_REQUEST).build();
        }
        long start = System.currentTimeMillis();
        long time[] = mainService.createAndExecuteSizedMongoLoadParallel(count, maxRows, messageSize);
        long totalTime = System.currentTimeMillis() - start;
        return generateLoadJsonResponse(totalTime, time[0], time[1]);
    }

    void onStart(@Observes StartupEvent ev) {
        if (resetDatabase) {
            LOG.info("Restarting database on startup");
            mainService.resetDatabase();
        }
    }

    private Response generateLoadJsonResponse(long totalTime, long lastExecStart, long lastExecFinish) {
        LoadResult loadResult = new LoadResult(totalTime, lastExecStart, lastExecFinish);
        try {
            return Response.ok().type(MediaType.APPLICATION_JSON).entity(loadResult.toJsonString()).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
