package io.skodjob.load.scenarios;

import okhttp3.Request;

import java.net.http.HttpRequest;
import java.util.List;

public class ScenarioRequest {
    private final int batchSize;
    private List<Request> requests;
    private Runnable wait;

    public ScenarioRequest(List<Request> requests, Runnable wait) {
        this.requests = requests;
        this.batchSize = requests.size();
        this.wait = wait;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public Runnable getWait() {
        return wait;
    }

    public void setWait(Runnable wait) {
        this.wait = wait;
    }

    public int getBatchSize() {
        return batchSize;
    }
}
