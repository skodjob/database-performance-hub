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
