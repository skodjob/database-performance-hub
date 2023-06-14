package io.debezium.performance.dmt.async;

import io.debezium.performance.dmt.dao.DaoManager;
import io.debezium.performance.dmt.model.DatabaseEntry;
import io.debezium.performance.dmt.model.QueryType;
import io.debezium.performance.dmt.resource.MainResource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class ExecutorPool {
    private final ExecutorService pool;
    private final BlockingQueue<RunnableUpsert> runnableUpsertsQueue;

    private static final Logger LOG = Logger.getLogger(ExecutorPool.class);


    @Inject
    public ExecutorPool(@ConfigProperty(name = "executor.size", defaultValue = "10") int poolSize, DaoManager manager) {
        pool = Executors.newFixedThreadPool(poolSize);
        runnableUpsertsQueue = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            runnableUpsertsQueue.add(new RunnableUpsert(manager.getEnabledDbs()));
        }
    }

    public void execute(String statement) {
        RunnableUpsert task;
        try {
            task = runnableUpsertsQueue.take();
        } catch (InterruptedException e) {
            return;
        }
//        LOG.info("Taken task");
        pool.submit(() -> {
            task.setStatement(statement);
//            LOG.info("Set statement");
            task.run();
//            LOG.info("Finished task");
            try {
                runnableUpsertsQueue.put(task);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            LOG.info("Returned runnable");
        });
    }


}
