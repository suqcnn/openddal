/*
 * Copyright 2014-2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the “License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.openddal.engine;

import com.openddal.config.SequenceRule;
import com.openddal.config.TableRule;
import com.openddal.dbobject.schema.Schema;
import com.openddal.dbobject.schema.Sequence;
import com.openddal.dbobject.table.TableMate;
import com.openddal.excutor.works.WorkerFactory;
import com.openddal.tx.Transaction;

/**
 * @author <a href="mailto:jorgie.mail@gmail.com">jorgie li</a>
 *
 */
public interface Repository {
    
    void init(Database database);

    String getName();
    
    TableMate loadMataData(Schema schema, TableRule tableRule);
    
    Sequence loadMataData(Schema schema, SequenceRule sequenceRule);

    Transaction beginTransaction();

    WorkerFactory getWorkerFactory();
    
    String getPublicDB();
    
    boolean isAsyncSupported();
    
    void close();

}
