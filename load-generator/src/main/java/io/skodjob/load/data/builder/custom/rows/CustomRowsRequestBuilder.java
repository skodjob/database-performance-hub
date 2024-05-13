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
