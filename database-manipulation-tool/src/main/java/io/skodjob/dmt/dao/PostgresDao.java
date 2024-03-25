/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;


import io.skodjob.dmt.dataSource.PostgresDataSource;
import io.skodjob.dmt.exception.RuntimeSQLException;
import io.skodjob.dmt.queryCreator.PostgresQueryCreator;
import io.quarkus.arc.Unremovable;
import io.quarkus.arc.lookup.LookupIfProperty;
import org.eclipse.microprofile.faulttolerance.Retry;

@Dependent
@LookupIfProperty(name = "quarkus.datasource.postgresql.enabled", stringValue = "true")
@Unremovable
@Retry
public final class PostgresDao extends AbstractBasicDao {

    @Inject
    public PostgresDao(PostgresDataSource source, PostgresQueryCreator queryCreator) {
        super(source, queryCreator);
    }

    @Override
    public void resetDatabase() {
        try (Connection conn = source.getConnection();
                Statement stmt = conn.createStatement()) {
            String schema = "public";
            LOG.debug("Dropping schema " + schema);
            stmt.execute(queryCreator.dropDatabaseQuery(schema));
            LOG.debug("Creating schema " + schema);
            stmt.execute(queryCreator.createDatabaseQuery(schema));
            LOG.info("Successfully reset schema " + schema);
        }
        catch (SQLException ex) {
            LOG.error("Could not reset schema");
            LOG.error(ex.getMessage());
            throw new RuntimeSQLException(ex);
        }
    }
}
