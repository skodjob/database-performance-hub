package io.skodjob.dmt.schema;

import java.util.Objects;

public final class DatabaseColumnEntry {
    private String value;
    private String columnName;
    private String dataType;

    public DatabaseColumnEntry(String value, String columnName, String dataType) {
        this.value = value;
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public DatabaseColumnEntry() {
        this.value = "UNDEFINED";
        this.columnName = "UNDEFINED";
        this.dataType = "UNDEFINED";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "DatabaseColumnEntry{" +
                "value='" + value + '\'' +
                ", columnName='" + columnName + '\'' +
                ", dataType='" + dataType + '\'' +
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
        DatabaseColumnEntry entry = (DatabaseColumnEntry) o;
        return Objects.equals(value, entry.value) && Objects.equals(columnName, entry.columnName) && Objects.equals(dataType, entry.dataType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, columnName, dataType);
    }

    public String value() {
        return value;
    }

    public String columnName() {
        return columnName;
    }

    public String dataType() {
        return dataType;
    }

}
