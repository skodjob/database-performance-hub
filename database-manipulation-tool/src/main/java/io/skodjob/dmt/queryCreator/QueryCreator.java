/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.queryCreator;

import java.util.List;

import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTableMetadata;

public interface QueryCreator {
    String insertQuery(DatabaseEntry databaseEntry);

    String createTableQuery(DatabaseTableMetadata databaseTableMetadata);

    String updateQuery(DatabaseEntry databaseEntry);

    String addColumnsQuery(List<DatabaseColumn> columns, String databaseName);

    String dropTable(DatabaseTableMetadata databaseTableMetadata);

    String dropDatabase(String schema);

    String createDatabase(String schema);
}
