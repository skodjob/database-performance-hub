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
