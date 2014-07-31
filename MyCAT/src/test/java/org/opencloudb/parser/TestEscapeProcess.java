/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights
 * reserved. DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. This
 * code is free software;Designed and Developed mainly by many Chinese
 * opensource volunteers. you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation. This code is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License version 2 for more details (a copy is included in the LICENSE
 * file that accompanied this code). You should have received a copy of the GNU
 * General Public License version 2 along with this work; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA. Any questions about this component can be directed to it's
 * project Web address https://code.google.com/p/opencloudb/.
 */
package org.opencloudb.parser;

import java.sql.SQLSyntaxErrorException;

import org.junit.Test;
import org.opencloudb.mpp.InsertParseInf;
import org.opencloudb.mpp.InsertSQLAnalyser;
import org.testng.AssertJUnit;

import com.foundationdb.sql.parser.QueryTreeNode;

public class TestEscapeProcess {

    String sql = "insert  into t_uud_user_account(USER_ID,USER_NAME,PASSWORD,CREATE_TIME,STATUS,NICK_NAME,USER_ICON_URL,USER_ICON_URL2,USER_ICON_URL3,ACCOUNT_TYPE) "
            + "values (2488899998,'u\\'aa\\'\\'a''aa','af8f9dffa5d420fbc249141645b962ee','2013-12-01 00:00:00',0,NULL,NULL,NULL,NULL,1)";

    String sqlret = "insert  into t_uud_user_account(USER_ID,USER_NAME,PASSWORD,CREATE_TIME,STATUS,NICK_NAME,USER_ICON_URL,USER_ICON_URL2,USER_ICON_URL3,ACCOUNT_TYPE) "
            + "values (2488899998,'u''aa''''a''aa','af8f9dffa5d420fbc249141645b962ee','2013-12-01 00:00:00',0,NULL,NULL,NULL,NULL,1)";

    String starWithEscapeSql = "\\insert  into t_uud_user_account(USER_ID,USER_NAME,PASSWORD,CREATE_TIME,STATUS,NICK_NAME,USER_ICON_URL,USER_ICON_URL2,USER_ICON_URL3,ACCOUNT_TYPE) "
            + "values (2488899998,'u\\'aa\\'\\'a''aa','af8f9dffa5d420fbc249141645b962ee','2013-12-01 00:00:00',0,NULL,NULL,NULL,NULL,1)\\";

    String starWithEscapeSqlret = "\\insert  into t_uud_user_account(USER_ID,USER_NAME,PASSWORD,CREATE_TIME,STATUS,NICK_NAME,USER_ICON_URL,USER_ICON_URL2,USER_ICON_URL3,ACCOUNT_TYPE) "
            + "values (2488899998,'u''aa''''a''aa','af8f9dffa5d420fbc249141645b962ee','2013-12-01 00:00:00',0,NULL,NULL,NULL,NULL,1)\\";

    @Test
    public void testFunctionEscapeProcess() throws SQLSyntaxErrorException {
        QueryTreeNode ast = null;
        InsertParseInf parsInf = null;
        ast = SQLParserDelegate.parse(sql, SQLParserDelegate.DEFAULT_CHARSET);
        parsInf = InsertSQLAnalyser.analyse(ast);
        AssertJUnit.assertEquals("t_uud_user_account".toUpperCase(), parsInf.tableName);
        AssertJUnit.assertEquals(6, parsInf.columnPairMap.size());
        AssertJUnit.assertNull(parsInf.fromQryNode);
    }

    @Test
    public void testEscapeProcess() {
        String sqlProcessed = SQLParserDelegate.processEscape(sql);
        AssertJUnit.assertEquals(sqlProcessed, sqlret);
        String sqlProcessed1 = SQLParserDelegate.processEscape(starWithEscapeSql);
        AssertJUnit.assertEquals(sqlProcessed1, starWithEscapeSqlret);
    }

}