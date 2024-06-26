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

package io.skodjob.load.scenarios;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ScenarioRequestExecutor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioRequestExecutor.class);
    List<ScenarioRequest> requestScenario;

    public ScenarioRequestExecutor(List<ScenarioRequest> requestScenario) {
        this.requestScenario = requestScenario;
    }

    @Override
    public void run() {
        //LOGGER.info("I am in run now");

        AtomicInteger numberOfSucc = new AtomicInteger(0);
        AtomicInteger numberOfFailed = new AtomicInteger(0);
        AtomicInteger endCalls = new AtomicInteger(0);
        AtomicLong startTime = new AtomicLong(0);
        AtomicLong end = new AtomicLong(0);
        int numberOfCalls = requestScenario.get(0).getBatchSize();



        Dispatcher disp = new Dispatcher(Executors.newCachedThreadPool());
        disp.setMaxRequests(1000);
        disp.setMaxRequestsPerHost(1000);

        EventListener eventListener = new EventListener() {
            @Override
            public void callEnd(@NotNull Call call) {
                if (numberOfCalls == endCalls.incrementAndGet()) {
                    end.set(System.currentTimeMillis());
                    long elapsed = startTime.get() - end.get();
                    LOGGER.info(String.format("Executor sent %d successful requests and %d failed requests.", numberOfSucc.get(), numberOfFailed.get()));
                    LOGGER.info("Requests took around" + elapsed + " miliseconds");
                }
                super.callEnd(call);
            }

            @Override
            public void callStart(@NotNull Call call) {
                startTime.compareAndSet(0, System.currentTimeMillis());
                super.callStart(call);
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Duration.of(2, ChronoUnit.SECONDS))
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(400, 15, TimeUnit.MINUTES))
                .dispatcher(disp)
                .eventListener(eventListener)
                .build();

        for (ScenarioRequest sr : requestScenario) {
            LOGGER.info(String.format("Sending %s requests in this batch", sr.getBatchSize()));

            for (Request r : sr.getRequests()) {
                client.newCall(r).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        LOGGER.error("Found exception during send: " + e.getMessage());
                        numberOfFailed.incrementAndGet();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        //LOGGER.info("Sent was successful");
                        if (response.isSuccessful()) {
                            //LOGGER.debug("Request was sent successfully");
                            numberOfSucc.incrementAndGet();
                        } else {
                            LOGGER.error("Received error status code: " + response);
                        }
                    }
                });
            }
            if (sr.getWait() != null) {
                sr.getWait().run();
            }
        }
    }

    public List<ScenarioRequest> getRequestScenario() {
        return requestScenario;
    }
}
