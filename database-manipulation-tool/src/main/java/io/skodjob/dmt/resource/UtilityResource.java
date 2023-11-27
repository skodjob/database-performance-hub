/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.service.UtilityService;
import io.skodjob.dmt.parser.DmtSchemaJsonParser;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Path("Utility")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@RegisterForReflection
public class UtilityResource {

    @Inject
    UtilityService utilityService;

    @Inject
    DmtSchemaJsonParser parser;

    private static final Logger LOG = Logger.getLogger(UtilityResource.class);

    @Path("GetAll")
    @GET
    public Response getAll() {
        try {
            LOG.debug("Received GET ALL request");
            JsonObject obj = utilityService.getAllEntries();
            return Response.ok().entity(obj.toString()).build();

        }
        catch (Exception ex) {
            LOG.error("Could not get all entries");
            LOG.error(ex.getMessage());
            return Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("TestSchema")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response parseTest(JsonObject inputJsonObj) {
        LOG.debug("Received PARSE TEST request");
        try {
            DatabaseEntry dbEntity = parser.parse(inputJsonObj);
            return Response.ok().entity("DMT schema is correct").build();
        }
        catch (Exception ex) {
            LOG.debug("Received DMT schema is incorrect");
            return Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROR - DMT schema is incorrect").build();
        }
    }
}
