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
