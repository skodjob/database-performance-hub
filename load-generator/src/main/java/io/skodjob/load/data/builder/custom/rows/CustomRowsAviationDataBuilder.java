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
