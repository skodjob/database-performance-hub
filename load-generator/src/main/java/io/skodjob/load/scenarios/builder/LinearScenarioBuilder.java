package io.skodjob.load.scenarios.builder;

import io.skodjob.load.scenarios.ScenarioRequest;
import io.skodjob.load.scenarios.ScenarioRequestExecutor;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class LinearScenarioBuilder implements ScenarioBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinearScenarioBuilder.class);
    private final int delta;

    private final int rate;

    public LinearScenarioBuilder(int delta, int rate) {
        this.delta = delta;
        this.rate = rate;
    }

    @Override
    public ScenarioRequestExecutor prepareScenario(List<Request> requestList) {

        int requestCounter = 0;
        int expectedAmount = delta;

        List<ScenarioRequest> result = new ArrayList<>();

        while (requestCounter < requestList.size()) {
            List<Request> batchList = new ArrayList<>();
            for (int i = 0; i < expectedAmount && requestCounter < requestList.size(); i++) {
                batchList.add(requestList.get(i));
                requestCounter++;
            }
            expectedAmount += delta;
            result.add(new ScenarioRequest(batchList, () -> {
                try {
                    LOGGER.debug("Waiting " + rate + " until next request");
                    Thread.sleep(rate);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
        return new ScenarioRequestExecutor(result);
    }
}
