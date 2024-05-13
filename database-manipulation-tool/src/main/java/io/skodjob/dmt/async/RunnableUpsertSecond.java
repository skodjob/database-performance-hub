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

package io.skodjob.dmt.async;

import io.skodjob.dmt.dao.Dao;
import io.skodjob.dmt.dao.MongoDao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RunnableUpsertSecond implements Runnable {
    List<Dao> dbs;
    MongoDao mongoDao;
    Consumer<Dao> daoFunction;

    public RunnableUpsertSecond(List<Dao> dbs) {
        this.dbs = new ArrayList<>();
        for (Dao dao: dbs) {
            if (dao.getClass().getName().contains("Mongo")) {
                this.mongoDao = (MongoDao) dao;
            } else {
                this.dbs.add(dao);
            }
        }
    }

    @Override
    public void run()
    {
        for (Dao dao : dbs) {
            daoFunction.accept(dao);
        }
    }

    public Consumer<Dao> getDaoFunction() {
        return daoFunction;
    }

    public void setDaoFunction(Consumer<Dao> daoFunction) {
        this.daoFunction = daoFunction;
    }

    public void setDaoFunctionAndExecute(Consumer<Dao> daoFunction) {
        setDaoFunction(daoFunction);
        run();
    }

    public void executeMongoFunction(Consumer<MongoDao> daoFunction) {
        daoFunction.accept(mongoDao);
    }
}
