package io.skodjob.dmt.queryCreator;

import io.quarkus.test.junit.QuarkusTest;
import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseColumnEntry;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTableMetadata;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

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
    void dropTable() {
        String expectedQuery = "DROP TABLE IF EXISTS TestTable";
        String actualQuery = mysqlQueryCreator.dropTable(generateTableMetadata());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void dropDatabase() {
        String expectedQuery = "DROP SCHEMA IF EXISTS TestSchema";
        String actualQuery = mysqlQueryCreator.dropDatabase("TestSchema");
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void createDatabase() {
        String expectedQuery = "CREATE SCHEMA TestSchema";
        String actualQuery = mysqlQueryCreator.createDatabase("TestSchema");
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void useDatabase() {
        String expectedQuery = "USE TestSchema";
        String actualQuery = mysqlQueryCreator.useDatabase("TestSchema");
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    DatabaseTableMetadata generateTableMetadata() {
        DatabaseTableMetadata tableMetadata = new DatabaseTableMetadata();
        tableMetadata.setName("TestTable");
        DatabaseColumn primaryColumn = new DatabaseColumn("PrimaryColumn", "INT", true);
        DatabaseColumn column1 = new DatabaseColumn("Column1", "TEXT", false);
        DatabaseColumn column2 = new DatabaseColumn("Column2", "DOUBLE", false);
        tableMetadata.addColumn(primaryColumn);
        tableMetadata.addColumn(column1);
        tableMetadata.addColumn(column2);
        return tableMetadata;
    }

    DatabaseEntry generateDatabaseEntry() {
        List<DatabaseColumnEntry> columnEntries = new ArrayList<>();
        columnEntries.add(new DatabaseColumnEntry("100", "PrimaryColumn", "INT"));
        columnEntries.add(new DatabaseColumnEntry("test", "Column1", "TEXT"));
        columnEntries.add(new DatabaseColumnEntry("25.11", "Column2", "DOUBLE"));
        return new DatabaseEntry(columnEntries, generateTableMetadata());
    }

}