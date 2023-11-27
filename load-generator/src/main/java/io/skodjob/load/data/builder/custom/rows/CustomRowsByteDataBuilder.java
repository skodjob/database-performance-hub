package io.skodjob.load.data.builder.custom.rows;

import io.skodjob.dmt.schema.DatabaseColumnEntry;
import io.skodjob.dmt.schema.DatabaseEntry;
import io.skodjob.load.data.enums.Tables;


public class CustomRowsByteDataBuilder extends CustomRowsDataBuilder {
    int dataSize;

    public CustomRowsByteDataBuilder(int dataSize, Tables table) {
        super();
        this.dataSize = dataSize;
        super.table = table;
    }

    @Override
    public DatabaseEntry generateDataRow(Integer idPool) {
        return generateCustomDataRow(0, idPool);
    }

    @Override
    public DatabaseEntry generateCustomDataRow(Integer minId, Integer maxId) {
        Integer id = RANDOM.nextInt(minId, maxId);
        DatabaseEntry schema = createDefaultScheme(id, table.toString().toLowerCase());
        String message = "a".repeat(dataSize);
        Integer payload_variable = RANDOM.nextInt(1000000);

        schema.getColumnEntries().add(new DatabaseColumnEntry(payload_variable + message, "payload",
                String.format("Varchar(%s)", dataSize + 7)));

        normalise(schema);
        return schema;
    }
}
