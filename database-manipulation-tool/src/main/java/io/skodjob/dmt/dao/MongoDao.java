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

import java.time.Instant;
import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import com.mongodb.client.model.WriteModel;
import io.skodjob.dmt.model.DatabaseColumnEntry;
import io.skodjob.dmt.queryCreator.MongoBsonCreator;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.logging.Logger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTableMetadata;
import io.quarkus.arc.Unremovable;
import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.mongodb.MongoClientName;

@Dependent
@LookupIfProperty(name = "quarkus.mongodb.main.enabled", stringValue = "true")
@Unremovable
@Retry(retryOn = Exception.class)
public final class MongoDao implements Dao {

    @ConfigProperty(name = "mongodb.database")
    String databaseName;

    @Inject
    @MongoClientName("main")
    MongoClient client;

    @Inject
    MongoBsonCreator bsonCreator;

    private static final Logger LOG = Logger.getLogger(MongoDao.class);

    @Override
    public void insert(DatabaseEntry databaseEntry) {
        LOG.debug("Inserting into Mongo " + databaseEntry);
        try {
            MongoDatabase db = getDatabase();
            Document insert = bsonCreator.insertDocument(databaseEntry);
            db.getCollection(databaseEntry.getDatabaseTableMetadata().getName()).insertOne(insert);
        }
        catch (Exception me) {
            LOG.error("Could not insert into database" + databaseEntry);
            LOG.error(me.getMessage());
            throw me;
        }
    }

    @Override
    public void delete(DatabaseEntry databaseEntry) {

    }

    @Override
    public void update(DatabaseEntry databaseEntry) {
        LOG.debug("Updating into Mongo " + databaseEntry);
        try {
            MongoDatabase db = getDatabase();
            DatabaseColumnEntry primary = databaseEntry.getPrimaryColumnEntry();
            if (primary == null) {
                throw new RuntimeException("Cannot update without primary key");
            }
            Bson filter = bsonCreator.getPrimaryFilter(databaseEntry);
            Bson update = bsonCreator.updateBson(databaseEntry);
            db.getCollection(databaseEntry.getDatabaseTableMetadata().getName()).updateOne(filter, update);
        }
        catch (Exception me) {
            LOG.error("Could not update database" + databaseEntry);
            LOG.error(me.getMessage());
            throw me;
        }
    }

    public void bulkWrite(List<WriteModel<Document>> bulkOperations, String collection) {
        try {
            MongoDatabase db = getDatabase();
            db.getCollection(collection).bulkWrite(bulkOperations);
        }
        catch (Exception me) {
            LOG.error("Could not do bulk operation to collection " + collection);
            LOG.error(me.getMessage());
            throw me;
        }
    }

    @Override
    public void createTable(DatabaseEntry databaseEntry) {
        DatabaseTableMetadata metadata = databaseEntry.getDatabaseTableMetadata();
        LOG.debug("Creating collection in mongo " + metadata.getName());
        try {
            MongoDatabase db = getDatabase();
            db.createCollection(metadata.getName());
        }
        catch (Exception me) {
            LOG.error("Could not create collection " + metadata.getName());
            LOG.error(me.getMessage());
            throw me;
        }
    }

    /**
     * Since Mongo does table alteration automatically this method does not need to do anything. Due to implementing the DAO interface the method must be here even though its empty.
     * @param columns does nothing
     * @param metadata does nothing
     */
    @Override
    public void alterTable(List<DatabaseColumn> columns, DatabaseTableMetadata metadata) {
    }

    @Override
    public void dropTable(DatabaseEntry databaseEntry) {
        DatabaseTableMetadata metadata = databaseEntry.getDatabaseTableMetadata();
        LOG.debug("Dropping collection in mongo " + metadata.getName());
        try {
            MongoDatabase db = getDatabase();
            db.getCollection(metadata.getName()).drop();
        }
        catch (Exception me) {
            LOG.error("Could not drop collection " + metadata);
            LOG.error(me.getMessage());
            throw me;
        }
    }

    @Override
    public void resetDatabase() {
        LOG.debug("Resetting mongo database ");
        try {
            MongoDatabase db = getDatabase();
            db.drop();
            getDatabase();
            LOG.info("Successfully reset database");
        }
        catch (Exception me) {
            LOG.error("Could not reset database");
            LOG.error(me.getMessage());
            throw me;
        }
    }

    @Override
    public void executeStatement(String statement) {
    }

    @Override
    public void executePreparedStatement(String statement) {
    }

    @Override
    public void executeBatchStatement(List<String> statements) {
    }

    @Override
    public Instant timedInsert(DatabaseEntry databaseEntry) {
        LOG.debug("Inserting timed insert into Mongo " + databaseEntry);
        try {
            MongoDatabase db = getDatabase();
            Document insert = bsonCreator.insertDocument(databaseEntry);
            db.getCollection(databaseEntry.getDatabaseTableMetadata().getName()).insertOne(insert);
            return Instant.now();
        }
        catch (Exception me) {
            LOG.error("Could not insert into database timed request" + databaseEntry);
            LOG.error(me.getMessage());
            throw me;
        }
    }

    private MongoDatabase getDatabase() {
        return client.getDatabase(databaseName);
    }
}
