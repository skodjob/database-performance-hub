package io.skodjob.dmt.generator;

import io.skodjob.dmt.parser.DmtSchemaParser;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.load.data.builder.ByteDataBuilder;
import io.skodjob.load.data.builder.RequestBuilder;
import io.skodjob.load.data.builder.custom.rows.CustomRowsAviationDataBuilder;
import io.skodjob.load.data.builder.custom.rows.CustomRowsByteDataBuilder;
import io.skodjob.load.data.builder.custom.rows.CustomRowsRequestBuilder;
import io.skodjob.load.data.enums.Tables;
import io.skodjob.load.scenarios.builder.ConstantScenarioBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class Generator {
    @Inject
    DmtSchemaParser parser;
    public List<DatabaseEntry> generateAviationBatch(int count, int minId, int maxId) {
        CustomRowsRequestBuilder<ConstantScenarioBuilder> requestBuilder
                = new CustomRowsRequestBuilder<>(new CustomRowsAviationDataBuilder(), new ConstantScenarioBuilder(1, 1));

        List<io.skodjob.dmt.schema.DatabaseEntry> entries = requestBuilder
                .setMinId(minId)
                .setMaxId(maxId)
                .setRequestCount(count)
                .buildPlain();

        return entries.stream().map(parser::parse).toList();
    }

    public List<DatabaseEntry> generateByteBatch(int count, int maxRows, int messageSize) {
        RequestBuilder<ConstantScenarioBuilder> requestBuilder = new RequestBuilder<>(new ByteDataBuilder(1), new ConstantScenarioBuilder(1,1));
        AtomicInteger i = new AtomicInteger(1);
        String message = "a".repeat(messageSize);
        List<io.skodjob.dmt.schema.DatabaseEntry> entries = requestBuilder
                .setRequestCount(count)
                .setMaxRows(maxRows)
                .buildPlain();
        entries.forEach(databaseEntry -> {
            databaseEntry.setPrimary("_id");
            databaseEntry.getColumnEntries().forEach(databaseColumnEntry -> {
                if (databaseColumnEntry.getColumnName().equals("id")){
                    databaseColumnEntry.setColumnName("_id");
                }
                if (databaseColumnEntry.getColumnName().equals("payload")) {
                    databaseColumnEntry.setValue(i.getAndIncrement() + message);
                }
            });
        });
        return entries.stream().map(parser::parse).toList();
    }

    public List<DatabaseEntry> generateCustomRowsByteBatch(int count, int minId, int maxId, int messageSize) {
        CustomRowsRequestBuilder<ConstantScenarioBuilder> customRowsRequestBuilder = new CustomRowsRequestBuilder<>(new CustomRowsByteDataBuilder(messageSize, Tables.BYTE_TABLE), new ConstantScenarioBuilder(1,1));
        List<io.skodjob.dmt.schema.DatabaseEntry> entries = customRowsRequestBuilder
                .setMinId(minId)
                .setMaxId(maxId)
                .setRequestCount(count)
                .buildPlain();
        return entries.stream().map(parser::parse).toList();
    }
}
