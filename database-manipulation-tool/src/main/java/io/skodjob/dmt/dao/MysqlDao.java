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


import io.skodjob.dmt.dataSource.MysqlDataSource;
import io.skodjob.dmt.exception.RuntimeSQLException;
import io.skodjob.dmt.queryCreator.MysqlQueryCreator;
import io.quarkus.arc.Unremovable;
import io.quarkus.arc.lookup.LookupIfProperty;
import org.eclipse.microprofile.faulttolerance.Retry;

@Dependent
@LookupIfProperty(name = "quarkus.datasource.mysql.enabled", stringValue = "true")
@Unremovable
@Retry
public final class MysqlDao extends AbstractBasicDao {

    @Inject
    public MysqlDao(MysqlDataSource source, MysqlQueryCreator queryCreator) {
        super(source, queryCreator);
    }

    @Override
    public void resetDatabase() {
        try (Connection conn = source.getConnection();
                Statement stmt = conn.createStatement()) {
            String schema = conn.getCatalog();
            LOG.debug("Dropping schema " + schema);
            stmt.execute(queryCreator.dropDatabaseQuery(schema));
            LOG.debug("Creating schema " + schema);
            stmt.execute(queryCreator.createDatabaseQuery(schema));
            LOG.debug("Use schema " + schema);
            stmt.execute(((MysqlQueryCreator) queryCreator).useDatabase(schema));
            LOG.info("Successfully reset schema " + schema);
        }
        catch (SQLException ex) {
            LOG.error("Could not reset schema");
            LOG.error(ex.getMessage());
            throw new RuntimeSQLException(ex);
        }
    }
}
