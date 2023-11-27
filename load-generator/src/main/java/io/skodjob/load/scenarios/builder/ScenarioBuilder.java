package io.skodjob.load.scenarios.builder;

import io.skodjob.load.scenarios.ScenarioRequestExecutor;
import okhttp3.Request;

import java.util.List;

public interface ScenarioBuilder {
    ScenarioRequestExecutor prepareScenario(List<Request> requestList);
}
