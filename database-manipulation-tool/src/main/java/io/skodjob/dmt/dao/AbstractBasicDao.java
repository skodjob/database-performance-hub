/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import javax.enterprise.context.Dependent;

import io.skodjob.dmt.exception.RuntimeSQLException;
import org.jboss.logging.Logger;

import io.skodjob.dmt.dataSource.DataSourceWrapper;
import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTableMetadata;
import io.skodjob.dmt.queryCreator.QueryCreator;

@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@Dependent
public abstract class AbstractBasicDao implements Dao {

    protected DataSourceWrapper source;
    protected QueryCreator queryCreator;

    protected final Logger LOG = Logger.getLogger(getClass());

    public AbstractBasicDao() {
    }

    public AbstractBasicDao(DataSourceWrapper source, QueryCreator queryCreator) {
        this.source = source;
        this.queryCreator = queryCreator;
    }

    @Override
    public void insert(DatabaseEntry databaseEntry) {
        try (Connection conn = source.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(queryCreator.insertQuery(databaseEntry));
        }
        catch (SQLException ex) {
            LOG.error("Could not insert into database " + databaseEntry);
            LOG.error(ex.getMessage());
        }
    }

    @Override
    public void update(DatabaseEntry databaseEntry) {
        try (Connection conn = source.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(queryCreator.updateQuery(databaseEntry));
        }
        catch (Exception ex) {
            LOG.error("Could not update database " + databaseEntry);
            LOG.error(ex.getMessage());
        }
    }

    @Override
    public void createTable(DatabaseEntry databaseEntry) {
        DatabaseTableMetadata metadata = databaseEntry.getDatabaseTableMetadata();
        try (Connection conn = source.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(queryCreator.createTableQuery(metadata));
        }
        catch (SQLException ex) {
            LOG.error("Could not create table " + metadata);
            LOG.error(ex.getMessage());
            throw new RuntimeSQLException(ex);
        }
    }

    @Override
    public void alterTable(List<DatabaseColumn> columns, DatabaseTableMetadata metadata) {
        try (Connection conn = source.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(queryCreator.addColumnsQuery(columns, metadata.getName()));
        }
        catch (SQLException ex) {
            LOG.error("Could not add columns " + columns + " to table " + metadata.getName());
            LOG.error(ex.getMessage());
            throw new RuntimeSQLException(ex);
        }
    }

    @Override
    public void dropTable(DatabaseEntry databaseEntry) {
        DatabaseTableMetadata metadata = databaseEntry.getDatabaseTableMetadata();
        try (Connection conn = source.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(queryCreator.dropTable(metadata));
        }
        catch (SQLException ex) {
            LOG.error("Could not drop table " + metadata.getName());
            LOG.error(ex.getMessage());
            throw new RuntimeSQLException(ex);
        }
    }

    @Override
    public void delete(DatabaseEntry databaseEntry) {

    }

    @Override
    public Instant timedInsert(DatabaseEntry databaseEntry) {
        try (Connection conn = source.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(queryCreator.insertQuery(databaseEntry));
            return Instant.now();
        }
        catch (SQLException ex) {
            LOG.error("Could not insert into database timed request" + databaseEntry);
            LOG.error(ex.getMessage());
            throw new RuntimeSQLException(ex);
        }
    }

    @Override
    public void executeStatement(String statement) {
        try (Connection conn = source.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(statement);
        }
        catch (SQLException ex) {
            LOG.error("Could not execute statement " + statement);
            LOG.error(ex.getMessage());
        }
    }

    @Override
    public void executePreparedStatement(String statement) {
        try (Connection conn = source.getConnection();
             PreparedStatement stmt = conn.prepareStatement(statement)) {
            stmt.executeUpdate();
        }
        catch (SQLException ex) {
            LOG.error("Could not execute statement " + statement);
            LOG.error(ex.getMessage());
        }
    }

    @Override
    public void executeBatchStatement(List<String> statements) {
        try (Connection conn = source.getConnection();
             Statement stmt = conn.createStatement()) {
            for (int i = 1; i < statements.size(); i++) {
                stmt.addBatch(statements.get(i));
            }
            stmt.executeBatch();
        }
        catch (SQLException ex) {
            LOG.error("Could not execute batch statement " + statements.get(1) + " ...");
            LOG.error(ex.getMessage());
        }
    }

    public DataSourceWrapper getSource() {
        return source;
    }

    public QueryCreator getQueryCreator() {
        return queryCreator;
    }
}
