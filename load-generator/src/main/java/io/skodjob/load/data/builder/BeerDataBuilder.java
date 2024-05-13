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

public class BeerDataBuilder extends DataBuilder {

    public BeerDataBuilder() {
        super();
        super.table = Tables.BEER;
    }

    @Override
    public DatabaseEntry generateDataRow(Integer idPool) {
        DatabaseEntry schema = createDefaultScheme(RANDOM.nextInt(idPool), table.toString().toLowerCase());
        //schema.setOperation(randomEnum(Operations.class, RANDOM).toString().toLowerCase());


        String name = dataFaker.beer().name();
        String brand = dataFaker.beer().brand();
        String hop = dataFaker.beer().hop();
        String malt = dataFaker.beer().malt();
        String yeast = dataFaker.beer().yeast();
        String style = dataFaker.beer().style();

        schema.getColumnEntries().add(new DatabaseColumnEntry(name, "name",
                DataTypeConvertor.convertDataType(name)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(brand, "brand",
                DataTypeConvertor.convertDataType(brand)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(hop, "hop",
                DataTypeConvertor.convertDataType(hop)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(malt, "malt",
                DataTypeConvertor.convertDataType(malt)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(yeast, "yeast",
                DataTypeConvertor.convertDataType(yeast)));

        schema.getColumnEntries().add(new DatabaseColumnEntry(style, "style",
                DataTypeConvertor.convertDataType(style)));

        normalise(schema);
        return schema;
    }
}
