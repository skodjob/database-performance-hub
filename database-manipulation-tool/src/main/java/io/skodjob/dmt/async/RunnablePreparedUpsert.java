package io.skodjob.dmt.async;

import io.skodjob.dmt.dao.Dao;

import java.util.List;

public class RunnablePreparedUpsert extends RunnableUpsert {

    public RunnablePreparedUpsert(List<Dao> dbs) {
        super(dbs);
    }

    @Override
    public void run() {
        executeToDaos(dao -> dao.executePreparedStatement(sqlQuery));
    }
}
