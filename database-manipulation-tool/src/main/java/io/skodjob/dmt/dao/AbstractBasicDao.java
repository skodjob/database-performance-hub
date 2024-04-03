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
package io.skodjob.dmt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import jakarta.enterprise.context.Dependent;

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
        executeStatement(queryCreator.insertQuery(databaseEntry),
                "Could not insert into database " + databaseEntry);
    }

    @Override
    public void update(DatabaseEntry databaseEntry) {
        executeStatement(queryCreator.updateQuery(databaseEntry),
                "Could not update database " + databaseEntry);
    }

    @Override
    public void createTable(DatabaseEntry databaseEntry) {
        DatabaseTableMetadata metadata = databaseEntry.getDatabaseTableMetadata();
        executeStatement(queryCreator.createTableQuery(metadata), "Could not create table " + metadata);
    }

    @Override
    public void alterTable(List<DatabaseColumn> columns, DatabaseTableMetadata metadata) {
        executeStatement(queryCreator.addColumnsQuery(columns, metadata.getName()),
                "Could not add columns " + columns + " to table " + metadata.getName());
    }

    @Override
    public void dropTable(DatabaseEntry databaseEntry) {
        DatabaseTableMetadata metadata = databaseEntry.getDatabaseTableMetadata();
        executeStatement(queryCreator.dropTableQuery(metadata),
                "Could not drop table " + metadata.getName());
    }

    @Override
    public void delete(DatabaseEntry databaseEntry) {

    }

    @Override
    public Instant timedInsert(DatabaseEntry databaseEntry) {
        executeStatement(queryCreator.insertQuery(databaseEntry),
                "Could not insert into database timed request" + databaseEntry);
        return Instant.now();
    }

    @Override
    public void executeStatement(String statement) {
        executeStatement(statement, "Could not execute statement " + statement);
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
            for (int i = 0; i < statements.size(); i++) {
                stmt.addBatch(statements.get(i));
            }
            int[] results = stmt.executeBatch();
            int i = 0;
            for (int result: results) {
                if (result != 1) {
                    LOG.error(i + " --- Query ---" + statements.get(i));
                    LOG.error(i + " --- A failed result from query!!");
                }
                i++;
            }
        }
        catch (SQLException ex) {
            LOG.error("Could not execute batch statement " + statements.get(0) + " ...");
            LOG.error(ex.getMessage());
        }
    }

    public DataSourceWrapper getSource() {
        return source;
    }

    public QueryCreator getQueryCreator() {
        return queryCreator;
    }

    private void executeStatement(String statement, String errorMessage) {
        LOG.info("Executing " + statement);
        try (Connection conn = source.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(statement);
        }
        catch (SQLException ex) {
            LOG.error(errorMessage);
            LOG.error(ex.getMessage());
            throw new RuntimeSQLException(ex);
        }
    }
}
