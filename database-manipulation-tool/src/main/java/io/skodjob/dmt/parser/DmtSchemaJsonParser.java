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
package io.skodjob.dmt.parser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.skodjob.dmt.model.DatabaseEntry;

@ApplicationScoped
public class DmtSchemaJsonParser implements DataParser<DatabaseEntry, JsonObject> {

    private static final Logger LOG = Logger.getLogger(DmtSchemaJsonParser.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DatabaseEntry parse(JsonObject inputObject) throws JsonException {
        io.skodjob.dmt.schema.DatabaseEntry schemaEntry;
        try {
            schemaEntry = objectMapper.readValue(inputObject.toString(), io.skodjob.dmt.schema.DatabaseEntry.class);
        }
        catch (Exception ex) {
            LOG.error("Could not parse DatabaseEntity from Json object " + inputObject);
            LOG.error(ex.getMessage());
            throw new JsonException("Could not parse DatabaseEntity from Json object", ex.getCause());
        }
        return ParseUtils.fromSchema(schemaEntry);
    }
}
