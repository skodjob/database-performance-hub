/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.dataSource;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;

@Dependent
public class PostgresDataSource implements DataSourceWrapper {

    @Inject
    @DataSource("postgresql")
    AgroalDataSource source;

    @Override
    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }
}
