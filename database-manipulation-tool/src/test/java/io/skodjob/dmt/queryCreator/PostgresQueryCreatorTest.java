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

import io.quarkus.test.junit.QuarkusTest;
import io.skodjob.dmt.model.DatabaseColumn;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.skodjob.dmt.queryCreator.QueryCreatorTestUtils.generateDatabaseEntry;
import static io.skodjob.dmt.queryCreator.QueryCreatorTestUtils.generateTableMetadata;

@QuarkusTest
class PostgresQueryCreatorTest {

    @Inject
    PostgresQueryCreator postgresQueryCreator;

    @Test
    void insertQuery() {
        String expectedQuery = "INSERT INTO TestTable (PrimaryColumn, Column1, Column2) VALUES ('100', 'test', '25.11')";
        String actualQuery = postgresQueryCreator.insertQuery(generateDatabaseEntry());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void createTableQuery() {
        String expectedQuery = "CREATE TABLE IF NOT EXISTS TestTable (PrimaryColumn INT, Column1 TEXT, Column2 DOUBLE PRECISION, PRIMARY KEY (PrimaryColumn))";
        String actualQuery = postgresQueryCreator.createTableQuery(generateTableMetadata());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void updateQuery() {
        String expectedQuery = "UPDATE TestTable SET PrimaryColumn = '100', Column1 = 'test', Column2 = '25.11' WHERE PrimaryColumn = '100'";
        String actualQuery = postgresQueryCreator.updateQuery(generateDatabaseEntry());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void addColumnsQuery() {
        String tableName = "TestTable";
        List<DatabaseColumn> columns = new ArrayList<>();
        columns.add(new DatabaseColumn("Column3", "TEXT", false));
        columns.add(new DatabaseColumn("Column4", "INT", false));
        String expectedQuery = "ALTER TABLE TestTable ADD COLUMN Column3 TEXT, ADD COLUMN Column4 INT";
        String actualQuery = postgresQueryCreator.addColumnsQuery(columns, tableName);
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void dropTableQuery() {
        String expectedQuery = "DROP TABLE IF EXISTS TestTable";
        String actualQuery = postgresQueryCreator.dropTableQuery(generateTableMetadata());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void dropDatabaseQuery() {
        String expectedQuery = "DROP SCHEMA IF EXISTS TestSchema CASCADE";
        String actualQuery = postgresQueryCreator.dropDatabaseQuery("TestSchema");
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void createDatabaseQuery() {
        String expectedQuery = "CREATE SCHEMA TestSchema";
        String actualQuery = postgresQueryCreator.createDatabaseQuery("TestSchema");
        Assertions.assertEquals(expectedQuery, actualQuery);
    }
}