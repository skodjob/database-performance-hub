/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.dao;

import java.time.Instant;
import java.util.List;

import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTableMetadata;

public interface Dao {

    void insert(DatabaseEntry databaseEntry);

    void delete(DatabaseEntry databaseEntry);

    void update(DatabaseEntry databaseEntry);

    void createTable(DatabaseEntry databaseEntry);

    void alterTable(List<DatabaseColumn> columns, DatabaseTableMetadata metadata);

    void dropTable(DatabaseEntry databaseEntry);

    void resetDatabase();

    void executeStatement(String statement);

    void executePreparedStatement(String statement);

    void executeBatchStatement(List<String> statements);

    Instant timedInsert(DatabaseEntry databaseEntry);

}
