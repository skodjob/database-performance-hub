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

import java.util.List;

import org.jboss.logging.Logger;

import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseColumnEntry;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTableMetadata;

public abstract class AbstractBasicQueryCreator implements QueryCreator {

    protected final Logger LOG = Logger.getLogger(getClass());

    @Override
    public String insertQuery(DatabaseEntry databaseEntry) {
        StringBuilder builder = new StringBuilder("INSERT INTO ")
                .append(databaseEntry.getDatabaseTableMetadata().getName())
                .append(" (");

        for (DatabaseColumn column : databaseEntry.getColumns()) {
            builder.append(column.getName())
                    .append(", ");
        }
        builder.delete(builder.length() - 2, builder.length())
                .append(") VALUES (");

        for (DatabaseColumnEntry entry : databaseEntry.getColumnEntries()) {
            builder.append('\'')
                    .append(entry.value())
                    .append('\'')
                    .append(", ");
        }
        builder.delete(builder.length() - 2, builder.length())
                .append(")");
        return builder.toString();
    }

    @Override
    public String createTableQuery(DatabaseTableMetadata databaseTableMetadata) {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(databaseTableMetadata.getName())
                .append(" (");

        for (DatabaseColumn column : databaseTableMetadata.getColumns()) {
            builder.append(column.getName())
                    .append(" ")
                    .append(column.getDataType())
                    .append(", ");
        }
        builder.append("PRIMARY KEY (")
                .append(databaseTableMetadata.getPrimary().getName())
                .append("))");
        String query = builder.toString();
        LOG.debug("Created TABLE CREATE query: " + query);
        return query;
    }

    /**
     *
     * @param databaseEntry with primary attribute
     * @return Update query for databaseEntry
     */
    @Override
    public String updateQuery(DatabaseEntry databaseEntry) {
        StringBuilder builder = new StringBuilder("UPDATE ")
                .append(databaseEntry.getDatabaseTableMetadata().getName())
                .append(" SET ");
        for (DatabaseColumnEntry columnEntry : databaseEntry.getColumnEntries()) {
            builder.append(columnEntry.columnName())
                    .append(" = '")
                    .append(columnEntry.value())
                    .append("', ");
        }
        builder.delete(builder.length() - 2, builder.length())
                .append(" WHERE ")
                .append(databaseEntry.getPrimaryColumnEntry().columnName())
                .append(" = '")
                .append(databaseEntry.getPrimaryColumnEntry().value())
                .append("'");

        return builder.toString();
    }

    @Override
    public String addColumnsQuery(List<DatabaseColumn> columns, String databaseName) {
        StringBuilder builder = new StringBuilder("ALTER TABLE " + databaseName);
        for (DatabaseColumn column : columns) {
            builder.append(" ADD COLUMN ")
                    .append(column.getName())
                    .append(" ")
                    .append(column.getDataType())
                    .append(",");
        }
        builder.delete(builder.length() - 1, builder.length());
        String query = builder.toString();
        LOG.debug("Created ALTER TABLE query: " + query);
        return query;
    }

    @Override
    public String dropTableQuery(DatabaseTableMetadata databaseTableMetadata) {
        String query = "DROP TABLE IF EXISTS " + databaseTableMetadata.getName();
        LOG.debug("Created DROP TABLE query: " + query);
        return query;
    }
}
