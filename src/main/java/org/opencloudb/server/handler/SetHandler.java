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

import static org.opencloudb.server.parser.ServerParseSet.AUTOCOMMIT_OFF;
import static org.opencloudb.server.parser.ServerParseSet.AUTOCOMMIT_ON;
import static org.opencloudb.server.parser.ServerParseSet.CHARACTER_SET_CLIENT;
import static org.opencloudb.server.parser.ServerParseSet.CHARACTER_SET_CONNECTION;
import static org.opencloudb.server.parser.ServerParseSet.CHARACTER_SET_RESULTS;
import static org.opencloudb.server.parser.ServerParseSet.NAMES;
import static org.opencloudb.server.parser.ServerParseSet.TX_READ_COMMITTED;
import static org.opencloudb.server.parser.ServerParseSet.TX_READ_UNCOMMITTED;
import static org.opencloudb.server.parser.ServerParseSet.TX_REPEATED_READ;
import static org.opencloudb.server.parser.ServerParseSet.TX_SERIALIZABLE;

import org.apache.log4j.Logger;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.config.Isolations;
import org.opencloudb.net.mysql.OkPacket;
import org.opencloudb.server.ServerConnection;
import org.opencloudb.server.parser.ServerParseSet;
import org.opencloudb.server.response.CharacterSet;

/**
 * SET 语句处理
 * 
 * @author mycat
 */
public final class SetHandler {

    private static final Logger logger = Logger.getLogger(SetHandler.class);
    private static final byte[] AC_OFF = new byte[] { 7, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 };

    public static void handle(String stmt, ServerConnection c, int offset) {
    	//System.out.println("SetHandler: "+stmt);
        int rs = ServerParseSet.parse(stmt, offset);
        switch (rs & 0xff) {
        case AUTOCOMMIT_ON:
            if (c.isAutocommit()) {
                c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
            } else {
                c.commit();
                c.setAutocommit(true);
            }
            break;
        case AUTOCOMMIT_OFF: {
            if (c.isAutocommit()) {
                c.setAutocommit(false);
            }
            c.write(c.writeToBuffer(AC_OFF, c.allocate()));
            break;
        }
        case TX_READ_UNCOMMITTED: {
            c.setTxIsolation(Isolations.READ_UNCOMMITTED);
            c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
            break;
        }
        case TX_READ_COMMITTED: {
            c.setTxIsolation(Isolations.READ_COMMITTED);
            c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
            break;
        }
        case TX_REPEATED_READ: {
            c.setTxIsolation(Isolations.REPEATED_READ);
            c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
            break;
        }
        case TX_SERIALIZABLE: {
            c.setTxIsolation(Isolations.SERIALIZABLE);
            c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
            break;
        }
        case NAMES:
            String charset = stmt.substring(rs >>> 8).trim();
            if (c.setCharset(charset)) {
                c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
            } else {
                c.writeErrMessage(ErrorCode.ER_UNKNOWN_CHARACTER_SET, "Unknown charset '" + charset + "'");
            }
            break;
        case CHARACTER_SET_CLIENT:
        case CHARACTER_SET_CONNECTION:
        case CHARACTER_SET_RESULTS:
            CharacterSet.response(stmt, c, rs);
            break;
        default:
            StringBuilder s = new StringBuilder();
            logger.warn(s.append(c).append(stmt).append(" is not executed").toString());
            c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
        }
    }

}