/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.parser;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import io.skodjob.dmt.model.DatabaseColumnEntry;
import org.jboss.logging.Logger;

import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTableMetadata;

@Deprecated
@ApplicationScoped
public class DatabaseEntryParser implements DataParser<DatabaseEntry, JsonObject> {

    private static final Logger LOG = Logger.getLogger(DatabaseEntryParser.class);

    @Override
    public DatabaseEntry parse(JsonObject inputJsonObject) throws JsonException {
        DatabaseEntry databaseEntry;
        try {
            List<DatabaseColumnEntry> entries = new ArrayList<>();
            DatabaseTableMetadata table = new DatabaseTableMetadata();

            table.setName(inputJsonObject.getString("table"));
            String primary = inputJsonObject.getString("primary");
            JsonArray payload = inputJsonObject.getJsonArray("payload");

            for (JsonValue rawEntry : payload) {
                JsonObject objectEntry = rawEntry.asJsonObject();
                DatabaseColumnEntry entry = new DatabaseColumnEntry(objectEntry.getString("value"), objectEntry.getString("name"), objectEntry.getString("dataType"));
                entries.add(entry);
                table.addColumn(new DatabaseColumn(entry.columnName(), entry.dataType(), primary.equals(entry.columnName())));
            }
            databaseEntry = new DatabaseEntry(entries, table);
        }
        catch (Exception ex) {
            LOG.error("Could not parse DatabaseEntity from Json object " + inputJsonObject);
            throw new JsonException("Could not parse DatabaseEntity from Json object", ex.getCause());
        }
        return databaseEntry;
    }
}
