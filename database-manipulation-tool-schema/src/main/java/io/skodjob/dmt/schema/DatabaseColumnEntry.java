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

import java.util.Objects;

/**
 * The type Database column entry.
 */
public final class DatabaseColumnEntry {
    private String value;
    private String columnName;
    private String dataType;

    /**
     * Instantiates a new Database column entry.
     *
     * @param value      the value
     * @param columnName the column name
     * @param dataType   the data type
     */
    public DatabaseColumnEntry(String value, String columnName, String dataType) {
        this.value = value;
        this.columnName = columnName;
        this.dataType = dataType;
    }

    /**
     * Instantiates a new Database column entry.
     */
    public DatabaseColumnEntry() {
        this.value = "UNDEFINED";
        this.columnName = "UNDEFINED";
        this.dataType = "UNDEFINED";
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets column name.
     *
     * @return the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets column name.
     *
     * @param columnName the column name
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Gets data type.
     *
     * @return the data type
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets data type.
     *
     * @param dataType the data type
     */
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

    /**
     * Value string.
     *
     * @return the string
     */
    public String value() {
        return value;
    }

    /**
     * Column name string.
     *
     * @return the string
     */
    public String columnName() {
        return columnName;
    }

    /**
     * Data type string.
     *
     * @return the string
     */
    public String dataType() {
        return dataType;
    }

}
