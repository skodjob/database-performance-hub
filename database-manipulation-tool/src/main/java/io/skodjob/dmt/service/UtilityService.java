/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.skodjob.dmt.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;

import org.jboss.logging.Logger;

import io.skodjob.dmt.dao.DaoManager;
import io.skodjob.dmt.database.Database;
import io.skodjob.dmt.utils.ResponseJsonBuilder;

@ApplicationScoped
public class UtilityService {

    @Inject
    Database database;

    @Inject
    DaoManager daoManager;

    private static final Logger LOG = Logger.getLogger(UtilityService.class);

    public JsonObject getAllEntries() {
        return new ResponseJsonBuilder(database)
                .addDatabases(daoManager.getEnabledDbsNames())
                .build();
    }
}
