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
package com.openddal.test.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import com.openddal.test.BaseTestCase;

/**
 * Tests the server by creating many JDBC objects (result sets and so on).
 */
public class ManyJdbcObjectsTestCase extends BaseTestCase {

    @Test
    public void test() throws SQLException {
        testNestedResultSets();
        testManyConnections();
        testOneConnectionPrepare();
    }

    private void testNestedResultSets() throws SQLException {
        Connection conn = getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rsTables = meta.getColumns(null, null, null, null);
        while (rsTables.next()) {
            meta.getExportedKeys(null, null, null);
            meta.getImportedKeys(null, null, null);
        }
        conn.close();
    }

    private void testManyConnections() throws SQLException {
        // SERVER_CACHED_OBJECTS = 1000: connections = 20 (1250)
        // SERVER_CACHED_OBJECTS = 500: connections = 40
        // SERVER_CACHED_OBJECTS = 50: connections = 120
        int connCount = 400;
        Connection[] conn = new Connection[connCount];
        for (int i = 0; i < connCount; i++) {
            conn[i] = getConnection();
        }
        int len = 500;
        for (int j = 0; j < len; j++) {
            if ((j % 10) == 0) {
                trace("j=" + j);
            }
            for (int i = 0; i < connCount; i++) {
                conn[i].getMetaData().getSchemas().close();
            }
        }
        for (int i = 0; i < connCount; i++) {
            conn[i].close();
        }
    }

    private void testOneConnectionPrepare() throws SQLException {
        Connection conn = getConnection();
        PreparedStatement prep;
        Statement stat;
        int size = 1000;
        for (int i = 0; i < size; i++) {
            conn.getMetaData();
        }
        for (int i = 0; i < size; i++) {
            conn.createStatement();
        }
        stat = conn.createStatement();
        stat.execute("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR)");
        stat.execute("INSERT INTO TEST VALUES(1, 'Hello')");
        for (int i = 0; i < size; i++) {
            stat.executeQuery("SELECT * FROM TEST WHERE 1=0");
        }
        for (int i = 0; i < size; i++) {
            stat.executeQuery("SELECT * FROM TEST");
        }
        for (int i = 0; i < size; i++) {
            conn.prepareStatement("SELECT * FROM TEST");
        }
        prep = conn.prepareStatement("SELECT * FROM TEST WHERE 1=0");
        for (int i = 0; i < size; i++) {
            prep.executeQuery();
        }
        prep = conn.prepareStatement("SELECT * FROM TEST");
        for (int i = 0; i < size; i++) {
            prep.executeQuery();
        }
        conn.close();
    }

}
