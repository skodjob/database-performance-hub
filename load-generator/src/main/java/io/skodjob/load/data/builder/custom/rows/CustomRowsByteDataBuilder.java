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
        int id = RANDOM.nextInt(minId, maxId);
        DatabaseEntry schema = createDefaultScheme(id, table.toString().toLowerCase());
        String message = "a".repeat(dataSize);
        Integer payload_variable = RANDOM.nextInt(1000000);

        schema.getColumnEntries().add(new DatabaseColumnEntry(payload_variable + message, "payload",
                String.format("Varchar(%s)", dataSize + 7)));

        normalise(schema);
        return schema;
    }
}
