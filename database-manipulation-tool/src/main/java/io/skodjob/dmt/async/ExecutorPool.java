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

package io.skodjob.dmt.async;

import io.skodjob.dmt.dao.Dao;
import io.skodjob.dmt.dao.DaoManager;
import io.skodjob.dmt.dao.MongoDao;
import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@ApplicationScoped
@Startup
public class ExecutorPool {
    private final ExecutorService pool;
    private final BlockingQueue<RunnableUpsertSecond> runnableUpsertSecondQueue;
    private CountDownLatch latch;


    @Inject
    public ExecutorPool(@ConfigProperty(name = "executor.size", defaultValue = "10") int poolSize, DaoManager manager) {
        pool = Executors.newFixedThreadPool(poolSize);
        latch = new CountDownLatch(0);
        runnableUpsertSecondQueue = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            runnableUpsertSecondQueue.add(new RunnableUpsertSecond(manager.getEnabledDbs()));
        }
    }

    public void executeQuery(String sqlQuery) {
        executeFunction(dao -> dao.executeStatement(sqlQuery));
    }

    public void executeBatchQuery(List<String> sqlQueries) {
        executeFunction(dao -> dao.executeBatchStatement(sqlQueries));
    }

    public void executeFunction(Consumer<Dao> daoFunction) {
        RunnableUpsertSecond task;
        try {
            task = runnableUpsertSecondQueue.take();
        } catch (InterruptedException e) {
            return;
        }
        pool.submit(() -> {
            task.setDaoFunctionAndExecute(daoFunction);
            try {
                runnableUpsertSecondQueue.put(task);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            latch.countDown();
        });
    }

    public void executeMongoFunction(Consumer<MongoDao> daoFunction) {
        RunnableUpsertSecond task;
        try {
            task = runnableUpsertSecondQueue.take();
        } catch (InterruptedException e) {
            return;
        }
        pool.submit(() -> {
            task.executeMongoFunction(daoFunction);
            try {
                runnableUpsertSecondQueue.put(task);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            latch.countDown();
        });
    }



    public void setCountDownLatch(int number) {
        latch = new CountDownLatch(number);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public int getPoolSize() {
        return ConfigProvider.getConfig().getValue("executor.size", int.class);
    }
}
