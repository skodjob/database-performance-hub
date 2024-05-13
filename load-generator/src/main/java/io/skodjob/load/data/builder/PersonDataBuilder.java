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
