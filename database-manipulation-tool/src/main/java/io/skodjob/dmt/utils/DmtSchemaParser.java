/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.utils;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;

import io.skodjob.dmt.model.DatabaseColumnEntry;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTableMetadata;

@ApplicationScoped
public class DmtSchemaParser implements DataParser<DatabaseEntry, JsonObject> {

    private static final Logger LOG = Logger.getLogger(DmtSchemaParser.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DatabaseEntry parse(JsonObject inputObject) throws JsonException {
        DatabaseEntry databaseEntry;
        try {
            var schemaEntry = objectMapper.readValue(inputObject.toString(), io.skodjob.dmt.schema.DatabaseEntry.class);

            List<DatabaseColumnEntry> entries = new ArrayList<>();
            DatabaseTableMetadata table = new DatabaseTableMetadata();

            table.setName(schemaEntry.getName());
            String primary = schemaEntry.getPrimary();

            for (var databaseColumnEntry : schemaEntry.getColumnEntries()) {
                DatabaseColumnEntry entry = new DatabaseColumnEntry(databaseColumnEntry.value(), databaseColumnEntry.columnName(), databaseColumnEntry.dataType());
                entries.add(entry);
                table.addColumn(new DatabaseColumn(entry.columnName(), entry.dataType(), primary.equals(entry.columnName())));
            }
            databaseEntry = new DatabaseEntry(entries, table);
        }
        catch (Exception ex) {
            LOG.error("Could not parse DatabaseEntity from Json object " + inputObject);
            LOG.error(ex.getMessage());
            throw new JsonException("Could not parse DatabaseEntity from Json object", ex.getCause());
        }
        return databaseEntry;
    }
}
