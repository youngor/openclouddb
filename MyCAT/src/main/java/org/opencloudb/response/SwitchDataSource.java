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

import java.util.Map;

import org.opencloudb.MycatServer;
import org.opencloudb.backend.PhysicalDBPool;
import org.opencloudb.manager.ManagerConnection;
import org.opencloudb.net.mysql.OkPacket;
import org.opencloudb.parser.ManagerParseSwitch;
import org.opencloudb.parser.util.Pair;

/**
 * 切换数据节点的数据源
 * 
 * @author mycat
 */
public final class SwitchDataSource {

    public static void response(String stmt, ManagerConnection c) {
        int count = 0;
        Pair<String[], Integer> pair = ManagerParseSwitch.getPair(stmt);
        Map<String, PhysicalDBPool> dns = MycatServer.getInstance().getConfig().getDataHosts();
        Integer idx = pair.getValue();
        for (String key : pair.getKey()) {
        	PhysicalDBPool dn = dns.get(key);
            if (dn != null) {
                int m = dn.getActivedIndex();
                int n = (idx == null) ? dn.next(m) : idx.intValue();
                if (dn.switchSource(n, false, "MANAGER")) {
                    ++count;
                }
            }
        }
        OkPacket packet = new OkPacket();
        packet.packetId = 1;
        packet.affectedRows = count;
        packet.serverStatus = 2;
        packet.write(c);
    }

}