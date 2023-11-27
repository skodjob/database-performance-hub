package io.skodjob.load.scenarios.builder;

import io.skodjob.load.scenarios.ScenarioRequest;
import io.skodjob.load.scenarios.ScenarioRequestExecutor;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class ConstantScenarioBuilder implements ScenarioBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstantScenarioBuilder.class);
    private final int rounds;

    private final int rate;

    public ConstantScenarioBuilder(int rounds, int rate) {
        this.rounds = rounds;
        this.rate = rate;
    }

    @Override
    public ScenarioRequestExecutor prepareScenario(List<Request> requestList) {

        int requestCounter = 0;
        int requestCnt = requestList.size();
        int roundLevel = requestCnt / rounds;

        List<ScenarioRequest> result = new ArrayList<>();

        while (requestCounter < requestCnt) {
            List<Request> batchList = new ArrayList<>();
            for (int i = 0; i < roundLevel && requestCounter < requestList.size(); i++) {
                batchList.add(requestList.get(i));
                requestCounter++;
            }
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
