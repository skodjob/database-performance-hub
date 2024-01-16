package io.skodjob.dmt.queryCreator;

import io.quarkus.test.junit.QuarkusTest;
import io.skodjob.dmt.model.DatabaseColumn;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static io.skodjob.dmt.queryCreator.QueryCreatorTestUtils.generateDatabaseEntry;
import static io.skodjob.dmt.queryCreator.QueryCreatorTestUtils.generateTableMetadata;

@QuarkusTest
class MysqlQueryCreatorTest {

    @Inject
    MysqlQueryCreator mysqlQueryCreator;

    @Test
    void insertQuery() {
        String expectedQuery = "INSERT INTO TestTable (PrimaryColumn, Column1, Column2) VALUES ('100', 'test', '25.11')";
        String actualQuery = mysqlQueryCreator.insertQuery(generateDatabaseEntry());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void createTableQuery() {
        String expectedQuery = "CREATE TABLE IF NOT EXISTS TestTable (PrimaryColumn INT, Column1 TEXT, Column2 DOUBLE, PRIMARY KEY (PrimaryColumn))";
        String actualQuery = mysqlQueryCreator.createTableQuery(generateTableMetadata());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void updateQuery() {
        String expectedQuery = "UPDATE TestTable SET PrimaryColumn = '100', Column1 = 'test', Column2 = '25.11' WHERE PrimaryColumn = '100'";
        String actualQuery = mysqlQueryCreator.updateQuery(generateDatabaseEntry());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void addColumnsQuery() {
        String tableName = "TestTable";
        List<DatabaseColumn> columns = new ArrayList<>();
        columns.add(new DatabaseColumn("Column3", "TEXT", false));
        columns.add(new DatabaseColumn("Column4", "INT", false));
        String expectedQuery = "ALTER TABLE TestTable ADD COLUMN Column3 TEXT, ADD COLUMN Column4 INT";
        String actualQuery = mysqlQueryCreator.addColumnsQuery(columns, tableName);
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void dropTableQuery() {
        String expectedQuery = "DROP TABLE IF EXISTS TestTable";
        String actualQuery = mysqlQueryCreator.dropTableQuery(generateTableMetadata());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void dropDatabaseQuery() {
        String expectedQuery = "DROP SCHEMA IF EXISTS TestSchema";
        String actualQuery = mysqlQueryCreator.dropDatabaseQuery("TestSchema");
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void createDatabaseQuery() {
        String expectedQuery = "CREATE SCHEMA TestSchema";
        String actualQuery = mysqlQueryCreator.createDatabaseQuery("TestSchema");
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void useDatabaseQuery() {
        String expectedQuery = "USE TestSchema";
        String actualQuery = mysqlQueryCreator.useDatabase("TestSchema");
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

}