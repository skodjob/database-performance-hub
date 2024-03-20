package io.skodjob.dmt.queryCreator;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.skodjob.dmt.queryCreator.QueryCreatorTestUtils.generateDatabaseEntry;
import static io.skodjob.dmt.queryCreator.QueryCreatorTestUtils.generateTableMetadata;

@QuarkusTest
class OracleQueryCreatorTest {

    @Inject
    OracleQueryCreator oracleQueryCreator;

    @Test
    void insertQuery() {
        String expectedQuery = "INSERT INTO TestTable (PrimaryColumn, Column1, Column2) VALUES ('100', 'test', '25.11')";
        String actualQuery = oracleQueryCreator.insertQuery(generateDatabaseEntry());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void createTableQuery() {
        String expectedQuery = "CREATE TABLE TestTable (PrimaryColumn INT, Column1 TEXT, Column2 DOUBLE, PRIMARY KEY (PrimaryColumn))";
        String actualQuery = oracleQueryCreator.createTableQuery(generateTableMetadata());
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void updateQuery() {
    }

    @Test
    void addColumnsQuery() {
    }

    @Test
    void dropTable() {
    }

    @Test
    void dropDatabase() {
    }

    @Test
    void createDatabase() {
    }
}