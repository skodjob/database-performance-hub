package io.skodjob.dmt.parser;


import io.skodjob.dmt.model.DatabaseEntry;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DmtSchemaParser implements DataParser<DatabaseEntry, io.skodjob.dmt.schema.DatabaseEntry>{
    @Override
    public DatabaseEntry parse(io.skodjob.dmt.schema.DatabaseEntry inputObject) {
        return ParseUtils.fromSchema(inputObject);
    }
}
