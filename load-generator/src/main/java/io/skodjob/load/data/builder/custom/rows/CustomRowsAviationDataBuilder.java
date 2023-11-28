package io.skodjob.load.data.builder.custom.rows;

import io.skodjob.dmt.schema.DatabaseColumnEntry;
import io.skodjob.dmt.schema.DatabaseEntry;
import io.skodjob.load.data.DataTypeConvertor;
import io.skodjob.load.data.enums.Tables;

public class CustomRowsAviationDataBuilder extends CustomRowsDataBuilder {

    public CustomRowsAviationDataBuilder() {
        super();
        super.table = Tables.AVIATION;
    }

    @Override
    public DatabaseEntry generateDataRow(Integer idPool) {
        return generateCustomDataRow(0, idPool);
    }

    @Override
    public DatabaseEntry generateCustomDataRow(Integer minId, Integer maxId) {
        int id = RANDOM.nextInt(minId, maxId);
        DatabaseEntry schema = createDefaultScheme(id, table.toString().toLowerCase());


        String aircraft = dataFaker.aviation().aircraft();
        String airline = dataFaker.aviation().airline();
        Integer passengers = RANDOM.nextInt(374);
        String airport = dataFaker.aviation().airport();
        String flight = dataFaker.aviation().flight();
        String metar = dataFaker.aviation().METAR();
        Double flightDistance = RANDOM.nextDouble(4000.0);


        schema.getColumnEntries().add(new DatabaseColumnEntry(aircraft, "aircraft",
                DataTypeConvertor.convertDataType(aircraft)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(airline, "airline",
                DataTypeConvertor.convertDataType(airline)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(passengers.toString(), "passengers",
                DataTypeConvertor.convertDataType(passengers)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(airport, "airport",
                DataTypeConvertor.convertDataType(airport)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(flight, "flight",
                DataTypeConvertor.convertDataType(flight)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(metar, "metar",
                DataTypeConvertor.convertDataType(metar)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(flightDistance.toString(), "flight_distance",
                DataTypeConvertor.convertDataType(flightDistance)));

        normalise(schema);
        return schema;
    }
}
