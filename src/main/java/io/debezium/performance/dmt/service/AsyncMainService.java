package io.debezium.performance.dmt.service;

import io.debezium.performance.dmt.async.ExecutorPool;
import io.debezium.performance.dmt.generator.Generator;
import io.debezium.performance.dmt.model.DatabaseEntry;
import io.debezium.performance.dmt.model.QueryType;
import io.debezium.performance.dmt.queryCreator.MysqlQueryCreator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class AsyncMainService extends MainService {
    @Inject
    Generator generator;
    @Inject
    ExecutorPool executorPool;
    @Inject
    MysqlQueryCreator mysqlQueryCreator;
    public long generateLoad(int count, int maxRows) {
        List<DatabaseEntry> entries = generator.generateBatch(count, maxRows);
        createTable(entries.get(0));
        List<String> statements = new ArrayList<>();
        for (DatabaseEntry entry: entries) {
            if (database.upsertEntry(entry)) {
                statements.add(mysqlQueryCreator.updateQuery(entry));
            } else {
                statements.add(mysqlQueryCreator.insertQuery(entry));
            }
        }
        long start = System.currentTimeMillis();
        for (String statement: statements) {
            executorPool.execute(statement);
        }
        return System.currentTimeMillis() - start;
    }
}
