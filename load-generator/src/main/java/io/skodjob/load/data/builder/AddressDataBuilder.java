package io.skodjob.load.data.builder;

import io.skodjob.dmt.schema.DatabaseColumnEntry;
import io.skodjob.dmt.schema.DatabaseEntry;
import io.skodjob.load.data.DataTypeConvertor;
import io.skodjob.load.data.enums.Tables;

public class AddressDataBuilder extends DataBuilder {

    public AddressDataBuilder() {
        super();
        super.table = Tables.ADDRESS;
    }

    @Override
    public DatabaseEntry generateDataRow(Integer idPool) {
        DatabaseEntry schema = createDefaultScheme(RANDOM.nextInt(idPool), table.toString().toLowerCase());
        //schema.setOperation(randomEnum(Operations.class, RANDOM).toString().toLowerCase());

        String city = dataFaker.address().cityName();
        String cityPref = dataFaker.address().cityPrefix();
        String street = dataFaker.address().streetName();
        Integer streetNumber = Integer.valueOf(dataFaker.address().streetAddressNumber());
        String zipCode = dataFaker.address().zipCode();
        Integer floor = RANDOM.nextInt(72);
        Double latitude = Double.valueOf(dataFaker.address().latitude());
        Double longtitude = Double.valueOf(dataFaker.address().longitude());


        schema.getColumnEntries().add(new DatabaseColumnEntry(city, "city",
                DataTypeConvertor.convertDataType(city)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(cityPref, "city_prefix",
                DataTypeConvertor.convertDataType(cityPref)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(street, "street",
                DataTypeConvertor.convertDataType(street)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(streetNumber.toString(), "street_number",
                DataTypeConvertor.convertDataType(streetNumber)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(zipCode, "zip_code",
                DataTypeConvertor.convertDataType(zipCode)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(floor.toString(), "floor",
                DataTypeConvertor.convertDataType(floor)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(latitude.toString(), "latitude",
                DataTypeConvertor.convertDataType(latitude)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(longtitude.toString(), "longtitude",
                DataTypeConvertor.convertDataType(longtitude)));

        normalise(schema);
        return schema;
    }
}
