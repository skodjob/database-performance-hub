/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.parser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.skodjob.dmt.model.DatabaseEntry;

@ApplicationScoped
public class DmtSchemaJsonParser implements DataParser<DatabaseEntry, JsonObject> {

    private static final Logger LOG = Logger.getLogger(DmtSchemaJsonParser.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DatabaseEntry parse(JsonObject inputObject) throws JsonException {
        io.skodjob.dmt.schema.DatabaseEntry schemaEntry;
        try {
            schemaEntry = objectMapper.readValue(inputObject.toString(), io.skodjob.dmt.schema.DatabaseEntry.class);
        }
        catch (Exception ex) {
            LOG.error("Could not parse DatabaseEntity from Json object " + inputObject);
            LOG.error(ex.getMessage());
            throw new JsonException("Could not parse DatabaseEntity from Json object", ex.getCause());
        }
        return ParseUtils.fromSchema(schemaEntry);
    }
}
