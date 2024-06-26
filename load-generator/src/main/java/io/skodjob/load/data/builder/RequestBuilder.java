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

package io.skodjob.load.data.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.skodjob.dmt.schema.DatabaseEntry;
import io.skodjob.load.scenarios.ScenarioRequestExecutor;
import io.skodjob.load.scenarios.builder.ScenarioBuilder;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestBuilder<G extends ScenarioBuilder> {
    private final DataBuilder dataBuilder;
    private final G scenarioBuilder;
    private String endpoint;
    private Integer maximalRowCount;
    private int requestCount;


    public RequestBuilder(DataBuilder dataBuilder, G scenarioBuilder) {
        this.dataBuilder = dataBuilder;
        this.scenarioBuilder = scenarioBuilder;
    }

    public RequestBuilder<G> setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }


    public RequestBuilder<G> setMaxRows(Integer maximalRowCount) {
        this.maximalRowCount = maximalRowCount;
        return this;
    }

    public RequestBuilder<G> setRequestCount(int requestCount) {
        this.requestCount = requestCount;
        return this;
    }

    private List<Request> generateRequests(List<DatabaseEntry> payloads, int rate) throws MalformedURLException, JsonProcessingException {
        List<Request> requests = new ArrayList<>();
        for (DatabaseEntry schema : payloads) {

            String serializedRequest = new ObjectMapper().writeValueAsString(schema);

            requests.add(new Request.Builder()
                    .url(new URL(endpoint))
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(serializedRequest, MediaType.parse("application/json")))
                    .build());
        }
        return requests;
    }

    public void buildAndExecute(int rate) throws MalformedURLException, JsonProcessingException {
        List<DatabaseEntry> payloads = dataBuilder.addRequests(maximalRowCount, requestCount).build();
        List<Request> requests = this.generateRequests(payloads, rate);
        scenarioBuilder.prepareScenario(requests).run();
    }

    public ScenarioRequestExecutor buildScenario(int rate) throws MalformedURLException, JsonProcessingException {
        List<DatabaseEntry> payloads = dataBuilder.addRequests(maximalRowCount, requestCount).build();
        List<Request> requests = this.generateRequests(payloads, rate);
        return scenarioBuilder.prepareScenario(requests);
    }

    public int getRequestCount() {
        return requestCount;
    }

    public List<Request> build(int rate) throws MalformedURLException, JsonProcessingException {
        List<DatabaseEntry> payloads = dataBuilder.addRequests(maximalRowCount, requestCount).build();
        return this.generateRequests(payloads, rate);
    }

    public List<DatabaseEntry> buildPlain() {
        return dataBuilder.addRequests(maximalRowCount, requestCount).build();
    }

}
