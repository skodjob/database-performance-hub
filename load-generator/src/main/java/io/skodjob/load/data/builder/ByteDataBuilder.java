package io.skodjob.load.data.builder;

import io.skodjob.dmt.schema.DatabaseColumnEntry;
import io.skodjob.dmt.schema.DatabaseEntry;
import io.skodjob.load.data.enums.Tables;

/**
 * Creates messages with arbitrary sized data payload in a single column
 */
public class ByteDataBuilder extends DataBuilder {
    int dataSize;

    /**
     * @param dataSize Size of the payload
     */
    public ByteDataBuilder(int dataSize) {
        super();
        super.table = Tables.BYTE_TABLE;
        this.dataSize = dataSize;
    }

    @Override
    public DatabaseEntry generateDataRow(Integer idPool) {
        DatabaseEntry schema = createDefaultScheme(RANDOM.nextInt(idPool), table.toString().toLowerCase());
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        for ( int i = 0; i < dataSize; i++ ) {
            builder.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        }

        schema.getColumnEntries().add(new DatabaseColumnEntry(builder.toString(), "payload",
                String.format("Varchar(%s)", dataSize + 5)));

        normalise(schema);
        return schema;
    }
}
