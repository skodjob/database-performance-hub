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
package io.skodjob.dmt.service;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.JsonObject;

import org.jboss.logging.Logger;

import io.skodjob.dmt.dao.Dao;
import io.skodjob.dmt.dao.DaoManager;
import io.skodjob.dmt.exception.InnerDatabaseException;
import io.skodjob.dmt.database.Database;
import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTableMetadata;
import io.skodjob.dmt.utils.TimeJsonBuilder;

@RequestScoped
@Named("main")
public class MainService {
    @Inject
    DaoManager daoManager;
    @Inject
    Database database;

    private static final Logger LOG = Logger.getLogger(MainService.class);

    public void insert(DatabaseEntry databaseEntry) {
        try {
            database.insertEntry(databaseEntry);
        }
        catch (InnerDatabaseException ex) {
            LOG.error("Error when inserting entry into inner database");
            LOG.error(ex.getMessage());
            return; // TODO: Throw Exception out
        }
        executeToDaos(dao -> dao.insert(databaseEntry));
    }

    public void createTable(DatabaseEntry databaseEntry) {
        List<DatabaseColumn> changedColumns;
        try {
            changedColumns = database.createOrAlterTable(databaseEntry.getDatabaseTableMetadata());
        }
        catch (InnerDatabaseException ex) {
            LOG.error("Error when creating table in database");
            LOG.error(ex.getMessage());
            return;
        }
        if (changedColumns == null) {
            LOG.debug("Creating table in Dbs " + databaseEntry.getDatabaseTableMetadata());
            executeToDaos(dao -> dao.createTable(databaseEntry));
            return;
        }

        if (!changedColumns.isEmpty()) {
            LOG.debug("Altering table in Dbs " + databaseEntry.getDatabaseTableMetadata());
            alterTableToDao(changedColumns, databaseEntry.getDatabaseTableMetadata());
        }
    }

    public void upsert(DatabaseEntry databaseEntry) {
        boolean update = false;
        try {
            createTable(databaseEntry);
            update = database.upsertEntry(databaseEntry);
        }
        catch (InnerDatabaseException ex) {
            LOG.error("Error when upserting entry into inner database");
            LOG.error(ex.getMessage());
            throw ex;
        }
        catch (Exception ex) {
            LOG.error("Error when upserting entry into databases");
            LOG.error(ex.getMessage());
        }
        if (update) {
            executeToDaos(dao -> dao.update(databaseEntry));
        }
        else {
            executeToDaos(dao -> dao.insert(databaseEntry));
        }
    }

    public void dropTable(DatabaseEntry databaseEntry) {
        try {
            database.dropTable(databaseEntry.getDatabaseTableMetadata().getName());
        }
        catch (InnerDatabaseException ex) {
            LOG.error("Error when dropping table from inner database");
            LOG.error(ex.getMessage());
            return;
        }
        executeToDaos(dao -> dao.dropTable(databaseEntry));
    }

    public void resetDatabase() {
        database.resetDatabase();
        executeToDaos((Dao::resetDatabase));
    }

    public JsonObject timedInsert(DatabaseEntry databaseEntry) {
        try {
            database.insertEntry(databaseEntry);
        }
        catch (InnerDatabaseException ex) {
            LOG.error("Error when timed inserting entry into inner database");
            LOG.error(ex.getMessage());
            throw ex;
        }
        TimeJsonBuilder builder = new TimeJsonBuilder();
        for (Dao dao : daoManager.getEnabledDbs()) {
            Instant instant = dao.timedInsert(databaseEntry);
            builder.addDbTimestamp(daoManager.prettifyDaoName(dao), instant);
        }
        return builder.build();
    }

    private void alterTableToDao(List<DatabaseColumn> columns, DatabaseTableMetadata metadata) {
        RuntimeException exception = null;
        for (Dao dao : daoManager.getEnabledDbs()) {
            try {
                dao.alterTable(columns, metadata);
            }
            catch (RuntimeException ex) {
                exception = ex;
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    private void executeToDaos(Consumer<Dao> func) {
        RuntimeException exception = null;
        for (Dao dao : daoManager.getEnabledDbs()) {
            try {
                func.accept(dao);
            }
            catch (RuntimeException ex) {
                exception = ex;
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

}
