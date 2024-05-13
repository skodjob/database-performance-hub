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
