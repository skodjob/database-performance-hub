package io.skodjob.load.data.builder.custom.rows;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.skodjob.dmt.schema.DatabaseEntry;
import io.skodjob.load.data.builder.RequestBuilder;
import io.skodjob.load.scenarios.builder.ScenarioBuilder;
import okhttp3.Request;

import java.net.MalformedURLException;
import java.util.List;

public class CustomRowsRequestBuilder<G extends ScenarioBuilder> extends RequestBuilder<G> {
    CustomRowsDataBuilder customRowsDataBuilder;
    Integer minId;
    Integer maxId;

    public CustomRowsRequestBuilder(CustomRowsDataBuilder dataBuilder, G scenarioBuilder) {
        super(dataBuilder, scenarioBuilder);
        this.customRowsDataBuilder = dataBuilder;
    }

    public CustomRowsRequestBuilder<G> setMinId(Integer minId) {
        this.minId = minId;
        return this;
    }

    public CustomRowsRequestBuilder<G> setMaxId(Integer maxId) {
        this.maxId = maxId;
        return this;
    }

    @Override
    public List<DatabaseEntry> buildPlain() {
        return customRowsDataBuilder.addRequestsWithCustomRows(minId, maxId, this.getRequestCount()).build();
    }
}
