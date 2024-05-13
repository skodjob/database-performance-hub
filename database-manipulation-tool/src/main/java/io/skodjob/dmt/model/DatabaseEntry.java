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

import java.util.List;
import java.util.Objects;

public class DatabaseEntry {

    private List<DatabaseColumnEntry> columnEntries;
    private final DatabaseTableMetadata databaseTableMetadata;

    public DatabaseEntry(List<DatabaseColumnEntry> columnEntries, DatabaseTableMetadata databaseTableMetadata) {
        this.columnEntries = columnEntries;
        this.databaseTableMetadata = databaseTableMetadata;
    }

    public DatabaseEntry() {
        databaseTableMetadata = new DatabaseTableMetadata();
    }

    public List<DatabaseColumnEntry> getColumnEntries() {
        return columnEntries;
    }

    public void setColumnEntries(List<DatabaseColumnEntry> columnEntries) {
        this.columnEntries = columnEntries;
    }

    public void addColumnEntry(DatabaseColumnEntry columnEntry) {
        columnEntries.add(columnEntry);
    }

    public List<DatabaseColumn> getColumns() {
        return databaseTableMetadata.getColumns();
    }

    public DatabaseTableMetadata getDatabaseTableMetadata() {
        return databaseTableMetadata;
    }

    /**
     * @return DatabaseColumnEntry which is the primary in this DatabaseEntry
     */
    public DatabaseColumnEntry getPrimaryColumnEntry() {
        DatabaseColumn primaryColumn = databaseTableMetadata.getPrimary();
        for (DatabaseColumnEntry columnEntry : columnEntries) {
            if (columnEntry.columnName().equals(primaryColumn.getName())) {
                return columnEntry;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DatabaseEntity{" +
                "columnEntries=" + columnEntries +
                ", databaseTable=" + databaseTableMetadata +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DatabaseEntry that = (DatabaseEntry) o;
        return columnEntries.equals(that.columnEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnEntries);
    }
}
