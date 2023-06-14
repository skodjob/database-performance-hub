package io.debezium.performance.dmt.async;

import io.debezium.performance.dmt.dao.Dao;
import io.debezium.performance.dmt.model.DatabaseEntry;
import io.debezium.performance.dmt.model.QueryType;

import java.util.List;
import java.util.function.Consumer;

public class RunnableUpsert implements Runnable {

    List<Dao> dbs;
    String statement;

    public RunnableUpsert(List<Dao> dbs) {
        this.dbs = dbs;
    }

    @Override
    public void run() {
        executeToDaos(dao -> dao.executeStatement(statement));
    }

    private void executeToDaos(Consumer<Dao> func) {
        for (Dao dao : dbs) {
            func.accept(dao);
        }
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
}
