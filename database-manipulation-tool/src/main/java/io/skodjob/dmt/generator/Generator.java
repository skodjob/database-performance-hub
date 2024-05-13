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
