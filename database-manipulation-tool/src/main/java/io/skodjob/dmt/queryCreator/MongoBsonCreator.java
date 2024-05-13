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
package io.skodjob.dmt.queryCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import io.skodjob.dmt.model.DatabaseColumnEntry;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

import com.mongodb.client.model.Updates;

import io.skodjob.dmt.model.DatabaseEntry;

@ApplicationScoped
public class MongoBsonCreator {
    private static final Logger LOG = Logger.getLogger(MongoBsonCreator.class);

    public Bson updateBson(DatabaseEntry databaseEntry) {
        return Updates.combine(
                databaseEntry.getColumnEntries().stream()
                        .map(columnEntry -> Updates.set(columnEntry.columnName(), columnEntry.value()))
                        .collect(Collectors.toList()));
    }

    public Document insertDocument(DatabaseEntry databaseEntry) {
        Document document = new Document();
        databaseEntry.getColumnEntries()
                .forEach(columnEntry -> document.put(columnEntry.columnName(), columnEntry.value()));
        return document;
    }

    public List<WriteModel<Document>> bulkUpdateBson(List<DatabaseEntry> databaseEntries) {
        List<WriteModel<Document>> bulkOperations = new ArrayList<>();
        for (DatabaseEntry databaseEntry: databaseEntries) {
            Bson filter = getPrimaryFilter(databaseEntry);
            Bson update = updateBson(databaseEntry);
            UpdateOptions options = new UpdateOptions().upsert(true);
            UpdateOneModel<Document> updateOneModel = new UpdateOneModel<>(filter, update, options);
            bulkOperations.add(updateOneModel);
        }
        return bulkOperations;
    }

    public Bson getPrimaryFilter(DatabaseEntry databaseEntry) {
        DatabaseColumnEntry primary = databaseEntry.getPrimaryColumnEntry();
        return Filters.eq(primary.columnName(), primary.value());
    }
}
