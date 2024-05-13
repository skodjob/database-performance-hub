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

package io.skodjob.dmt.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type Database entry.
 */
public class DatabaseEntry {

    private List<DatabaseColumnEntry> columnEntries;
    private String name;
    private String primary;

    /**
     * Instantiates a new Database entry.
     *
     * @param columnEntries the column entries
     * @param name          the name
     * @param primary       the primary
     */
    public DatabaseEntry(List<DatabaseColumnEntry> columnEntries, String name, String primary) {
        this.columnEntries = columnEntries;
        this.name = name;
        this.primary = primary;
    }

    /**
     * Instantiates a new Database entry.
     *
     * @param name    the name
     * @param primary the primary
     */
    public DatabaseEntry(String name, String primary) {
        this.name = name;
        this.primary = primary;
        columnEntries = new ArrayList<>();
    }

    /**
     * Instantiates a new Database entry.
     */
    public DatabaseEntry() {
        this.name = "UNDEFINED";
        this.primary = "UNDEFINED";
        columnEntries = new ArrayList<>();
    }

    /**
     * Gets column entries.
     *
     * @return the column entries
     */
    public List<DatabaseColumnEntry> getColumnEntries() {
        return columnEntries;
    }

    /**
     * Sets column entries.
     *
     * @param columnEntries the column entries
     */
    public void setColumnEntries(List<DatabaseColumnEntry> columnEntries) {
        this.columnEntries = columnEntries;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets primary.
     *
     * @return the primary
     */
    public String getPrimary() {
        return primary;
    }

    /**
     * Sets primary.
     *
     * @param primary the primary
     */
    public void setPrimary(String primary) {
        this.primary = primary;
    }

    /**
     * Add column entry.
     *
     * @param columnEntry the column entry
     */
    public void addColumnEntry(DatabaseColumnEntry columnEntry) {
        columnEntries.add(columnEntry);
    }


    /**
     * Gets primary column entry.
     *
     * @return DatabaseColumnEntry which is the primary in this DatabaseEntry
     */
    @JsonIgnore
    public DatabaseColumnEntry getPrimaryColumnEntry() {
        String primaryColumnName = getPrimary();
        for (DatabaseColumnEntry columnEntry : columnEntries) {
            if (columnEntry.columnName().equals(primaryColumnName)) {
                return columnEntry;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DatabaseEntry{" +
                "columnEntries=" + columnEntries +
                ", name='" + name + '\'' +
                ", primary='" + primary + '\'' +
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

    /**
     * To json string string.
     *
     * @return the string
     * @throws JsonProcessingException the json processing exception
     */
    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
