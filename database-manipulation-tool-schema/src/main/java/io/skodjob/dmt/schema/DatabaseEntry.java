package io.skodjob.dmt.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseEntry {

    private List<DatabaseColumnEntry> columnEntries;
    private String name;
    private String primary;

    public DatabaseEntry(List<DatabaseColumnEntry> columnEntries, String name, String primary) {
        this.columnEntries = columnEntries;
        this.name = name;
        this.primary = primary;
    }

    public DatabaseEntry(String name, String primary) {
        this.name = name;
        this.primary = primary;
        columnEntries = new ArrayList<>();
    }

    public DatabaseEntry() {
        this.name = "UNDEFINED";
        this.primary = "UNDEFINED";
        columnEntries = new ArrayList<>();
    }

    public List<DatabaseColumnEntry> getColumnEntries() {
        return columnEntries;
    }

    public void setColumnEntries(List<DatabaseColumnEntry> columnEntries) {
        this.columnEntries = columnEntries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public void addColumnEntry(DatabaseColumnEntry columnEntry) {
        columnEntries.add(columnEntry);
    }


    /**
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

    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
