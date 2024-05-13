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
package io.skodjob.dmt.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;


@Singleton
public final class DaoManager {
    List<Dao> enabledDbs;

    @Inject
    Instance<MysqlDao> mysql;
    @Inject
    Instance<PostgresDao> postgres;
    @Inject
    Instance<MongoDao> mongo;

    public DaoManager() {
        enabledDbs = new ArrayList<>();
        if (CDI.current().select(PostgresDao.class).isResolvable()) {
            enabledDbs.add(CDI.current().select(PostgresDao.class).get());
        }
        if (CDI.current().select(MysqlDao.class).isResolvable()) {
            enabledDbs.add(CDI.current().select(MysqlDao.class).get());
        }
        if (CDI.current().select(MongoDao.class).isResolvable()) {
            enabledDbs.add(CDI.current().select(MongoDao.class).get());
        }
    }

    public List<Dao> getEnabledDbs() {
        List<Dao> returnList = new ArrayList<>();
        if (postgres.isResolvable()) {
            returnList.add(postgres.get());
        }
        if (mongo.isResolvable()) {
            returnList.add(mongo.get());
        }
        if (mysql.isResolvable()) {
            returnList.add(mysql.get());
        }
        return returnList;
    }

    public List<String> getEnabledDbsNames() {
        return enabledDbs.stream().map(this::prettifyDaoName)
                .collect(Collectors.toList());
    }

    public String prettifyDaoName(Dao dao) {
        String daoName = dao.getClass().getSimpleName();
        String split = daoName.split("_")[0];
        return split.substring(0, split.length() - 3);
    }

    public MongoDao getMongoDao() {
        if (mongo.isResolvable()) {
            return mongo.get();
        }
        return null;
    }
}
