package io.skodjob.load.scenarios.builder;

import io.skodjob.load.scenarios.ScenarioRequest;
import io.skodjob.load.scenarios.ScenarioRequestExecutor;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PeakScenarioBuilder implements ScenarioBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeakScenarioBuilder.class);
    private final int peakLevel;
    private final int peakRounds;
    private final int rate;

    private final int quietRounds;


    public PeakScenarioBuilder(int peakLevel, int peakRounds, int rate, int quietRounds) {
        this.peakLevel = peakLevel;
        this.peakRounds = peakRounds;
        this.rate = rate;
        this.quietRounds = quietRounds;
    }

    @Override
    public ScenarioRequestExecutor prepareScenario(List<Request> requestList) {

        int peakRequiredCnt = peakRounds * peakLevel;
        int requestSize = requestList.size();
        int quietRoundLevel = (requestSize - peakRequiredCnt) / quietRounds;

        int peakStart = new Random().nextInt(quietRounds);

        int requestCounter = 0;

        List<ScenarioRequest> result = new ArrayList<>();

        for (int currentRound = 0; currentRound < (quietRounds + peakRounds); currentRound++) {
            List<Request> batchList = new ArrayList<>();
            if (currentRound >= peakStart && currentRound < (peakStart + peakRounds)) {
                for (int i = 0; i < peakLevel && requestCounter < requestSize; i++) {
                    batchList.add(requestList.get(requestCounter));
                    requestCounter++;
                }
            } else {
                for (int i = 0; i < quietRoundLevel && requestCounter < requestSize; i++) {
                    batchList.add(requestList.get(requestCounter));
                    requestCounter++;
                }
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
