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
package io.skodjob.dmt.utils;

import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import io.skodjob.dmt.database.Database;
import io.skodjob.dmt.model.DatabaseColumnEntry;
import io.skodjob.dmt.model.DatabaseTable;
import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseEntry;

public class ResponseJsonBuilder {
    JsonObjectBuilder mainBuilder;
    JsonArrayBuilder tablesBuilder;

    public ResponseJsonBuilder() {
        this.mainBuilder = Json.createObjectBuilder();
        this.tablesBuilder = Json.createArrayBuilder();
    }

    public ResponseJsonBuilder(Database database) {
        this.mainBuilder = Json.createObjectBuilder();
        this.tablesBuilder = Json.createArrayBuilder();
        database.getTables().forEach(this::addTable);
    }

    public ResponseJsonBuilder addDatabases(List<String> databases) {
        JsonArrayBuilder databasesBuilder = Json.createArrayBuilder();
        databases.forEach(databasesBuilder::add);
        mainBuilder.add("databases", databasesBuilder);
        return this;
    }

    public ResponseJsonBuilder addTable(DatabaseTable table) {
        JsonObjectBuilder tableBuilder = Json.createObjectBuilder();
        tableBuilder.add("tableName", table.getMetadata().getName());
        tableBuilder.add("primary", table.getMetadata().getPrimary().getName());

        JsonArrayBuilder schemaBuilder = Json.createArrayBuilder();
        for (DatabaseColumn databaseColumn : table.getMetadata().getColumns()) {
            JsonObjectBuilder columnSchemaBuilder = Json.createObjectBuilder();
            columnSchemaBuilder.add("name", databaseColumn.getName());
            columnSchemaBuilder.add("dataType", databaseColumn.getDataType());
            schemaBuilder.add(columnSchemaBuilder);
        }
        tableBuilder.add("tableSchema", schemaBuilder);

        JsonArrayBuilder rowsBuilder = Json.createArrayBuilder();
        int rowNumber = 1;
        for (DatabaseEntry row : table.getRows()) {
            JsonObjectBuilder rowBuilder = Json.createObjectBuilder();
            rowBuilder.add("rowNumber", rowNumber++);
            JsonArrayBuilder columnsBuilder = Json.createArrayBuilder();
            for (DatabaseColumnEntry columnEntry : row.getColumnEntries()) {
                JsonObjectBuilder columnBuilder = Json.createObjectBuilder();
                columnBuilder.add("columnName", columnEntry.columnName());
                columnBuilder.add("columnValue", columnEntry.value());
                columnsBuilder.add(columnBuilder);
            }
            rowBuilder.add("columns", columnsBuilder);
            rowsBuilder.add(rowBuilder);
        }
        tableBuilder.add("tableRows", rowsBuilder);
        tablesBuilder.add(tableBuilder);
        return this;
    }

    public JsonObject build() {
        mainBuilder.add("tables", tablesBuilder);
        return mainBuilder.build();
    }
}
