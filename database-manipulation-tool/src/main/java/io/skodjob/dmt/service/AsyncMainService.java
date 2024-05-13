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

import com.mongodb.client.model.WriteModel;
import io.skodjob.dmt.queryCreator.MongoBsonCreator;
import io.skodjob.dmt.queryCreator.MysqlQueryCreator;
import io.skodjob.dmt.async.ExecutorPool;
import io.skodjob.dmt.dao.MongoDao;
import io.skodjob.dmt.generator.Generator;
import io.skodjob.dmt.model.DatabaseEntry;
import io.quarkus.runtime.Startup;
import org.bson.Document;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Startup
public class AsyncMainService extends MainService {
    private static final Logger LOG = Logger.getLogger(AsyncMainService.class);
    @Inject
    Generator generator;
    @Inject
    ExecutorPool executorPool;
    @Inject
    MysqlQueryCreator mysqlQueryCreator;
    @Inject
    MongoBsonCreator mongoBsonCreator;

    public long[] createAndExecuteLoad(int count, int maxRows) {
        List<String> statements = generateAviationSqlQueries(count,0, maxRows);
        executorPool.setCountDownLatch(statements.size());
        long start = System.currentTimeMillis();
        for (String statement: statements) {
            executorPool.executeFunction(dao -> dao.executePreparedStatement(statement));
        }
        return waitForLastTask(start);
    }

    public long[] createAndExecuteBatchLoad(int count, int maxRows) {
        int poolSize = executorPool.getPoolSize();
        int batchSize = count / poolSize;
        int rowBatchSize = maxRows / poolSize;
        List<List<String>> batches = new ArrayList<>();
        int minId = 0;
        int maxId = rowBatchSize;
        for (int i = 0; i < count; i += batchSize) {
            List<String> queries = generateAviationSqlQueries(batchSize, minId, maxId);
            batches.add(queries);
            minId = maxId;
            maxId = maxId + rowBatchSize;
        }
        LOG.info("Finished generating. Beginning execution.");
        executorPool.setCountDownLatch(batches.size());
        long start = System.currentTimeMillis();
        for (List<String> batch: batches) {
            executorPool.executeFunction(dao -> dao.executeBatchStatement(batch));
        }
        return waitForLastTask(start);
    }

    public long[] createAndExecuteSizedBatchLoad(int count, int maxRows, int messageSize) {
        int poolSize = executorPool.getPoolSize();
        int batchSize = count / poolSize;
        int rowBatchSize = maxRows / poolSize;
        List<List<String>> batches = new ArrayList<>();
        int minId = 0;
        int maxId = rowBatchSize;
        for (int i = 0; i < count; i += batchSize) {
            List<String> queries = generateSizedCustomRowBatch(batchSize, minId, maxId, messageSize);
            batches.add(queries);
            minId = maxId;
            maxId = maxId + rowBatchSize;
        }
        LOG.info("Finished generating. Beginning execution.");
        executorPool.setCountDownLatch(batches.size());
        long start = System.currentTimeMillis();
        for (List<String> batch: batches) {
            executorPool.executeFunction(dao -> dao.executeBatchStatement(batch));
        }
        return waitForLastTask(start);
    }

    public long createAndExecuteSizedMongoLoad(int count, int maxRows, int messageSize) {
        MongoDao mongo = daoManager.getMongoDao();
        if (mongo == null) {
            throw new RuntimeException("Missing Mongo database.");
        }
        String collection = generator.generateByteBatch(1,1,1).get(0).getDatabaseTableMetadata().getName();
        List<WriteModel<Document>> bulkOperations = generateSizedMongoBulk(count, maxRows, messageSize);
        LOG.info("Finished generating loaf generation. Beginning execution.");
        long start = System.currentTimeMillis();
        mongo.bulkWrite(bulkOperations, collection);
        long end = System.currentTimeMillis();
        return end - start;
    }

    public long[] createAndExecuteSizedMongoLoadParallel(int count, int maxRows, int messageSize) {
        String collection = generator.generateByteBatch(1,1,1).get(0).getDatabaseTableMetadata().getName();
        List<WriteModel<Document>> bulkOperations = generateSizedMongoBulk(count, maxRows, messageSize);
        int poolSize = executorPool.getPoolSize();
        int batchSize = bulkOperations.size() / poolSize;
        List<List<WriteModel<Document>>> batches = new ArrayList<>();
        for (int i = 0; i < bulkOperations.size(); i += batchSize) {
            batches.add(bulkOperations.subList(i, Math.min(i + batchSize, bulkOperations.size())));
        }
        executorPool.setCountDownLatch(batches.size());
        long start = System.currentTimeMillis();
        for (List<WriteModel<Document>> batch: batches) {
            executorPool.executeMongoFunction(dao -> dao.bulkWrite(batch, collection));
        }
        return waitForLastTask(start);
    }



    private long[] waitForLastTask(long start) {
        long lastThreadExecuted = System.currentTimeMillis() - start;
        long lastThreadFinished;
        try {
            executorPool.getLatch().await();
            lastThreadFinished = System.currentTimeMillis() - start;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new long[] {lastThreadExecuted, lastThreadFinished};
    }

    private List<String> generateAviationSqlQueries(int count, int minId, int maxId) {
        List<DatabaseEntry> entries = generator.generateAviationBatch(count, minId, maxId);
        return checkInnerDbGetQueries(entries);
    }
    private List<String> generateSizedCustomRowBatch(int count, int minId, int maxId, int messageSize) {
        List<DatabaseEntry> entries = generator.generateCustomRowsByteBatch(count, minId, maxId, messageSize);
        return checkInnerDbGetQueries(entries);
    }

    private List<String> checkInnerDbGetQueries(List<DatabaseEntry> entries) {
        createTable(entries.get(0));
        List<String> queries = new ArrayList<>();
        for (DatabaseEntry entry: entries) {
            if (database.upsertEntry(entry)) {
                queries.add(mysqlQueryCreator.updateQuery(entry));
            } else {
                queries.add(mysqlQueryCreator.insertQuery(entry));
            }
        }
        return queries;
    }

    private List<WriteModel<Document>> generateSizedMongoBulk(int count, int maxRows, int messageSize) {
        List<DatabaseEntry> entries = generator.generateByteBatch(count, maxRows, messageSize);
        createTable(entries.get(0));
        for (DatabaseEntry entry: entries) {
            database.upsertEntry(entry);
        }
        return mongoBsonCreator.bulkUpdateBson(entries);
    }

}
