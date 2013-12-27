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
package org.opencloudb.response;

import org.opencloudb.MycatConfig;
import org.opencloudb.MycatServer;
import org.opencloudb.backend.PhysicalDBNode;
import org.opencloudb.backend.PhysicalDBPool;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.config.model.SchemaConfig;
import org.opencloudb.manager.ManagerConnection;
import org.opencloudb.net.mysql.OkPacket;

/**
 * @author mycat
 */
public class ClearSlow {

    public static void dataNode(ManagerConnection c, String name) {
    	PhysicalDBNode dn = MycatServer.getInstance().getConfig().getDataNodes().get(name);
    	PhysicalDBPool ds = null;
        if (dn != null && ((ds = dn.getDbPool())!= null)) {
           // ds.getSqlRecorder().clear();
            c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
        } else {
            c.writeErrMessage(ErrorCode.ER_YES, "Invalid DataNode:" + name);
        }
    }

    public static void schema(ManagerConnection c, String name) {
        MycatConfig conf = MycatServer.getInstance().getConfig();
        SchemaConfig schema = conf.getSchemas().get(name);
        if (schema != null) {
//            Map<String, MySQLDataNode> dataNodes = conf.getDataNodes();
//            for (String n : schema.getAllDataNodes()) {
//                MySQLDataNode dn = dataNodes.get(n);
//                MySQLDataSource ds = null;
//                if (dn != null && (ds = dn.getSource()) != null) {
//                    ds.getSqlRecorder().clear();
//                }
//            }
            c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
        } else {
            c.writeErrMessage(ErrorCode.ER_YES, "Invalid Schema:" + name);
        }
    }

}