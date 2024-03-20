package io.skodjob.dmt.queryCreator;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OracleQueryCreator extends AbstractBasicQueryCreator {
    @Override
    public String dropDatabaseQuery(String schema) {
        return null;
    }

    @Override
    public String createDatabaseQuery(String schema) {
        return null;
    }
}
