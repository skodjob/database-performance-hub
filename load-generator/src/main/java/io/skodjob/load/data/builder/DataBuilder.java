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
