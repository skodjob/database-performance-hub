package io.skodjob.load.data.builder.custom.rows;

import io.skodjob.dmt.schema.DatabaseEntry;
import io.skodjob.load.data.builder.DataBuilder;

public abstract class CustomRowsDataBuilder extends DataBuilder {

    public abstract DatabaseEntry generateCustomDataRow(Integer minId, Integer maxId);

    DataBuilder addRequestsWithCustomRows(Integer minId, Integer maxId, int count) {
        for (int i = 0; i < count; i++) {
            this.requests.add(this.generateCustomDataRow(minId, maxId));
        }
        return this;
    }
}
