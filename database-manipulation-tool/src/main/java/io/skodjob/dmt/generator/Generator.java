package io.skodjob.dmt.generator;

import io.skodjob.dmt.parser.DmtSchemaParser;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.load.data.builder.AviationDataBuilder;
import io.skodjob.load.data.builder.ByteDataBuilder;
import io.skodjob.load.data.builder.RequestBuilder;
import io.skodjob.load.scenarios.builder.ConstantScenarioBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class Generator {
    @Inject
    DmtSchemaParser parser;
    public List<DatabaseEntry> generateAviationBatch(int count, int maxRows) {
        RequestBuilder<ConstantScenarioBuilder> requestBuilder
                = new RequestBuilder<>(new AviationDataBuilder(), new ConstantScenarioBuilder(1, 1));

        List<io.skodjob.dmt.schema.DatabaseEntry> entries = requestBuilder
                .setRequestCount(count)
                .setMaxRows(maxRows)
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
        System.out.println(entries.get(0));
        return entries.stream().map(parser::parse).toList();
    }
}
