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
package io.skodjob.dmt.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTable;
import io.skodjob.dmt.model.DatabaseTableMetadata;
import jakarta.enterprise.context.ApplicationScoped;

import io.skodjob.dmt.exception.InnerDatabaseException;
import org.jboss.logging.Logger;

import io.quarkus.arc.Lock;

@Lock
@ApplicationScoped
public class Database {
    private final MeterRegistry meterRegistry;
    Map<String, DatabaseTable> tables;

    private static final Logger LOG = Logger.getLogger(Database.class);

    public Database(PrometheusMeterRegistry meterRegistry) {
        tables = new HashMap<>();
        this.meterRegistry = meterRegistry;
        meterRegistry.gaugeMapSize("table.count", Tags.empty(), tables);
    }

    @Lock(value = Lock.Type.READ)
    public boolean tableExists(String name) {
        return tables.containsKey(name);
    }

    @Lock(value = Lock.Type.READ)
    public DatabaseTable getTable(String name) {
        return tables.get(name);
    }

    @Lock(value = Lock.Type.READ)
    public boolean entryExists(DatabaseEntry entry) {
        String tableName = entry.getDatabaseTableMetadata().getName();
        return tableExists(tableName) && getTable(tableName).rowExists(entry);
    }

    /**
     * @param tableMetadata contains name that is checked
     * @return false if table exists, true if it was created.
     */
    public boolean createTableIfNotExists(DatabaseTableMetadata tableMetadata) {
        if (tableExists(tableMetadata.getName())) {
            return false;
        }
        LOG.debug("Creating table in inner database: " + tableMetadata.getName());
        createTable(tableMetadata);
        return true;
    }

    /**
     * @param tableMetadata contains columns that are required in the database table
     * @return List of columns added. Is empty table did not need alteration. Null the table was created.
     */
    public List<DatabaseColumn> createOrAlterTable(DatabaseTableMetadata tableMetadata) {
        if (createTableIfNotExists(tableMetadata)) {
            return null;
        }
        DatabaseTableMetadata current = tables.get(tableMetadata.getName()).getMetadata();
        List<DatabaseColumn> missingColumns = tableMetadata.getMissingColumns(current);
        missingColumns.forEach(current::addColumn);
        return missingColumns;
    }

    public void dropTable(String name) {
        tables.remove(name);
    }

    public void resetDatabase() {
        tables.clear();
    }

    /**
     * @param entry
     * @return true if updated, false if inserted
     */
    public boolean upsertEntry(DatabaseEntry entry) {
        String tableName = entry.getDatabaseTableMetadata().getName();
        if (!entry.getDatabaseTableMetadata().isSubsetOf(getTable(tableName).getMetadata())) {
            throw new InnerDatabaseException("Entry contains columns the database table does not");
        }
        return getTable(tableName).putRow(entry);
    }

    public void insertEntry(DatabaseEntry entry) {
        String tableName = entry.getDatabaseTableMetadata().getName();
        createTableIfNotExists(entry.getDatabaseTableMetadata());
        if (entryExists(entry)) {
            throw new InnerDatabaseException("Table already contains entry");
        }
        if (!entry.getDatabaseTableMetadata().isSubsetOf(getTable(tableName).getMetadata())) {
            throw new InnerDatabaseException("Entry contains columns the database does not");
        }
        getTable(tableName).putRow(entry);
    }

    public void deleteEntry(DatabaseEntry entry) {
        DatabaseTable table = tables.get(entry.getDatabaseTableMetadata().getName());
        if (table == null) {
            LOG.debug("Table does not exist -> not deleting entry " + entry);
            return;
        }
        table.deleteRow(entry);
    }

    @Lock(value = Lock.Type.READ)
    public List<DatabaseTable> getTables() {
        return new ArrayList<>(tables.values());
    }

    private void createTable(DatabaseTableMetadata tableMetadata) {
        tables.put(tableMetadata.getName(), new DatabaseTable(tableMetadata));
        meterRegistry.gaugeMapSize(tableMetadata.getName() + ".table.size", Tags.empty(), tables.get(tableMetadata.getName()).getRowsAsMap());
    }
}
