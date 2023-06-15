package io.debezium.performance.dmt.async;

import io.debezium.performance.dmt.dao.DaoManager;
import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
@Startup
public class ExecutorPool {
    private final ExecutorService pool;
    private final BlockingQueue<RunnableUpsert> runnableUpsertsQueue;

    private AtomicInteger counter;

    private long timer = 0;
    private int requestsCount = 0;
    private static final Logger LOG = Logger.getLogger(ExecutorPool.class);


    @Inject
    public ExecutorPool(@ConfigProperty(name = "executor.size", defaultValue = "10") int poolSize, DaoManager manager) {
        pool = Executors.newFixedThreadPool(poolSize);
        counter = new AtomicInteger(0);
        runnableUpsertsQueue = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            runnableUpsertsQueue.add(new RunnablePreparedUpsert(manager.getEnabledDbs()));
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
            int localCounter = counter.addAndGet(1);
            if (localCounter >= requestsCount) {
                LOG.info("Counter is: " + localCounter);
                LOG.info("Last executor finished: " + (System.currentTimeMillis() - timer));
            }
//            LOG.info("Returned runnable");
        });
    }

    public void restartCounter() {
        counter = new AtomicInteger(0);
    }

    public void startTimer() {
        timer = System.currentTimeMillis();
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public void setRequestsCount(int requestsCount) {
        this.requestsCount = requestsCount;
    }
}
