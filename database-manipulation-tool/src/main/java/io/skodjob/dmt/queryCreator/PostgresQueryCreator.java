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
package io.skodjob.dmt.queryCreator;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseTableMetadata;

@ApplicationScoped
public class PostgresQueryCreator extends AbstractBasicQueryCreator {

    private static final Logger LOG = Logger.getLogger(PostgresQueryCreator.class);

    public PostgresQueryCreator() {}

    @Override
    public String createTableQuery(DatabaseTableMetadata databaseTableMetadata) {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(databaseTableMetadata.getName())
                .append(" (");

        for (DatabaseColumn column : databaseTableMetadata.getColumns()) {
            builder.append(column.getName())
                    .append(" ")
                    .append(convertDouble(column.getDataType()))
                    .append(", ");
        }
        builder.append("PRIMARY KEY (")
                .append(databaseTableMetadata.getPrimary().getName())
                .append("))");

        String query = builder.toString();
        LOG.debug("Created TABLE CREATE query: " + query);
        return query;
    }

    @Override
    public String dropDatabaseQuery(String schema) {
        String query = "DROP SCHEMA IF EXISTS " + schema + " CASCADE";
        LOG.debug("Created DROP SCHEMA query: " + query);
        return query;
    }

    @Override
    public String createDatabaseQuery(String schema) {
        String query = "CREATE SCHEMA " + schema;
        LOG.debug("Created CREATE SCHEMA query: " + query);
        return query;
    }

    private String convertDouble(String dataType) {
        if (dataType.equalsIgnoreCase("double")) {
            return "DOUBLE PRECISION";
        }
        return dataType;
    }
}
