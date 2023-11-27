package io.skodjob.load.data.builder;

import io.skodjob.dmt.schema.DatabaseColumnEntry;
import io.skodjob.dmt.schema.DatabaseEntry;
import io.skodjob.load.data.DataTypeConvertor;
import io.skodjob.load.data.enums.Tables;
import net.datafaker.Faker;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class DataBuilder {
    protected static final Random RANDOM = new SecureRandom();
    protected Tables table = Tables.PERSON;
    protected Faker dataFaker = new Faker();
    protected List<DatabaseEntry> requests;

    public DataBuilder() {
        this.requests = new ArrayList<>();
    }

    public abstract DatabaseEntry generateDataRow(Integer idPool);


    protected void normalise(DatabaseEntry entry) {
        for (DatabaseColumnEntry column : entry.getColumnEntries()) {
            if (column.dataType().contains("VarChar")) {
                column.setValue(column.getValue().replace("'", " "));
            }
        }
    }

    DataBuilder addRequests(Integer idPool, int count) {
        for (int i = 0; i < count; i++) {
            this.requests.add(this.generateDataRow(idPool));
        }
        return this;
    }

    public List<DatabaseEntry> build() {
        return this.requests;
    }

    protected DatabaseEntry createDefaultScheme(Integer value, String tableName) {
        DatabaseEntry schema = new DatabaseEntry(new ArrayList<>(), tableName, "id");
        schema.getColumnEntries().add(new DatabaseColumnEntry(value.toString(), "id",
                DataTypeConvertor.convertDataType(value)));
        return schema;
    }

    protected <T extends Enum<?>> T randomEnum(Class<T> clazz, Random random) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }


}
