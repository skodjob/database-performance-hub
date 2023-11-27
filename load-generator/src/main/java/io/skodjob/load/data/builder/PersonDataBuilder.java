package io.skodjob.load.data.builder;

import io.skodjob.dmt.schema.DatabaseColumnEntry;
import io.skodjob.dmt.schema.DatabaseEntry;
import io.skodjob.load.data.DataTypeConvertor;
import io.skodjob.load.data.enums.Tables;

public class PersonDataBuilder extends DataBuilder {

    public PersonDataBuilder() {
        super();
        super.table = Tables.PERSON;
    }

    @Override
    public DatabaseEntry generateDataRow(Integer idPool) {
        DatabaseEntry schema = createDefaultScheme(RANDOM.nextInt(idPool), table.toString().toLowerCase());
        //schema.setOperation(randomEnum(Operations.class, RANDOM).toString().toLowerCase());

        String firstName = dataFaker.name().firstName();
        String lastName = dataFaker.name().lastName();
        // AGE
        Integer age = RANDOM.nextInt(100);
        // Average day computer work
        Double avg = RANDOM.nextDouble();
        String city = dataFaker.address().city();
        String company = dataFaker.company().name();
        String color = dataFaker.color().name();

        schema.getColumnEntries().add(new DatabaseColumnEntry(firstName, "name",
                DataTypeConvertor.convertDataType(firstName)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(lastName, "lastname",
                DataTypeConvertor.convertDataType(lastName)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(age.toString(), "age",
                DataTypeConvertor.convertDataType(age)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(avg.toString(), "computeraverage",
                DataTypeConvertor.convertDataType(avg)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(city, "city",
                DataTypeConvertor.convertDataType(city)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(company, "company",
                DataTypeConvertor.convertDataType(company)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(color, "favouritecolor",
                DataTypeConvertor.convertDataType(color)));

        normalise(schema);
        return schema;
    }
}
