/*
 * Copyright 2012-2015 org.opencloudb.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencloudb.server.handler;

import org.opencloudb.server.ServerConnection;
import org.opencloudb.server.parser.ServerParse;
import org.opencloudb.server.parser.ServerParseShow;
import org.opencloudb.server.response.ShowCobarCluster;
import org.opencloudb.server.response.ShowCobarStatus;
import org.opencloudb.server.response.ShowDatabases;
import org.opencloudb.util.StringUtil;
/**
 * @author mycat
 */
public final class ShowHandler {

    public static void handle(String stmt, ServerConnection c, int offset) {
    	
    	// 排除 “ ` ” 符号
    	stmt = StringUtil.replaceChars(stmt, "`", null);
    	
        switch (ServerParseShow.parse(stmt, offset)) {
        case ServerParseShow.DATABASES:
            ShowDatabases.response(c);
            break;
        case ServerParseShow.COBAR_STATUS:
            ShowCobarStatus.response(c);
            break;
        case ServerParseShow.COBAR_CLUSTER:
            ShowCobarCluster.response(c);
            break;
        default:
            c.execute(stmt, ServerParse.SHOW);
        }
    }

}