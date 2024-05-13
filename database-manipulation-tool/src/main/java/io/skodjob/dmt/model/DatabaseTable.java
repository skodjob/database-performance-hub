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
package io.skodjob.dmt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import io.skodjob.dmt.exception.InnerDatabaseException;

public class DatabaseTable {
    private Map<String, DatabaseEntry> rows;
    private DatabaseTableMetadata metadata;

    private static final Logger LOG = Logger.getLogger(DatabaseTable.class);

    public DatabaseTable() {
        rows = new HashMap<>();
        metadata = new DatabaseTableMetadata();
    }

    public DatabaseTable(DatabaseTableMetadata metadata) {
        rows = new HashMap<>();
        this.metadata = metadata;
    }

    public boolean putRow(DatabaseEntry row) {
        boolean updated = rowExists(row);
        String primary = getPrimary(row);
        rows.put(primary, row);
        return updated;
    }

    public void deleteRow(DatabaseEntry row) {
        String primary = getPrimary(row);
        if (rows.remove(primary) == null) {
            LOG.debug("Row did not exist -> not deleting entry " + row);
        }
        else {
            LOG.debug("Row successfully deleted " + row);
        }
    }

    public boolean rowExists(DatabaseEntry row) {
        return rows.containsKey(getPrimary(row));
    }

    private String getPrimary(DatabaseEntry row) {
        DatabaseColumnEntry primary = row.getPrimaryColumnEntry();
        if (primary != null) {
            return primary.value();
        }
        throw new InnerDatabaseException("Primary key is not set");
    }

    public DatabaseTableMetadata getMetadata() {
        return metadata;
    }

    public List<DatabaseEntry> getRows() {
        return new ArrayList<>(rows.values());
    }

    public Map<String, DatabaseEntry> getRowsAsMap() {
        return rows;
    }
}
