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
@Retry(retryOn = RuntimeSQLException.class)
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
