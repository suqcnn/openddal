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
package com.openddal.excutor.cursor;

import java.util.ArrayList;

import com.openddal.dbobject.index.IndexCondition;
import com.openddal.dbobject.table.RangeTable;
import com.openddal.dbobject.table.Table;
import com.openddal.dbobject.table.TableFilter;
import com.openddal.engine.Session;
import com.openddal.message.DbException;
import com.openddal.result.Row;
import com.openddal.result.SearchRow;

/**
 * @author <a href="mailto:jorgie.mail@gmail.com">jorgie li</a>
 */
public class SearchCursor implements Cursor {

    private final TableFilter tableFilter;
    private Table table;
    private Cursor cursor;
    private boolean alwaysFalse;

    public SearchCursor(TableFilter tableFilter) {
        this.tableFilter = tableFilter;
        this.table = tableFilter.getTable();
    }


    public void find(Session s, ArrayList<IndexCondition> indexConditions) {
        if(table instanceof RangeTable) {
            this.cursor = find(s, (RangeTable)table);
        }
    }

    /**
     * Check if the result is empty for sure.
     *
     * @return true if it is
     */
    public boolean isAlwaysFalse() {
        return alwaysFalse;
    }

    @Override
    public Row get() {
        if (cursor == null) {
            return null;
        }
        return cursor.get();
    }

    @Override
    public SearchRow getSearchRow() {
        return cursor.getSearchRow();
    }

    @Override
    public boolean next() {
        while (true) {
            if (cursor == null) {
                nextCursor();
                if (cursor == null) {
                    return false;
                }
            }
            if (cursor.next()) {
                return true;
            }
            cursor = null;
        }
    }

    private void nextCursor() {

    }

    @Override
    public boolean previous() {
        throw DbException.throwInternalError();
    }

    public Cursor find(Session session, RangeTable table) {
        long min = table.getMin(session), start = min;
        long max = table.getMax(session), end = max;
        return new RangeCursor(start, end);
    }

}
