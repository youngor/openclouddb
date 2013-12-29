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
package org.opencloudb.heartbeat;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.opencloudb.MycatServer;
import org.opencloudb.config.model.DBHostConfig;
import org.opencloudb.net.factory.BackendConnectionFactory;

/**
 * @author mycat
 */
public class MySQLDetectorFactory extends BackendConnectionFactory {

    public MySQLDetectorFactory() {
        this.idleTimeout = 300 * 1000L;
    }

    public MySQLDetector make(MySQLHeartbeat heartbeat) throws IOException {
        SocketChannel channel = openSocketChannel();
        DBHostConfig dsc = heartbeat.getSource().getConfig();
        MySQLDetector detector = new MySQLDetector(channel);
        detector.setHost(dsc.getIp());
        detector.setPort(dsc.getPort());
        detector.setUser(dsc.getUser());
        detector.setPassword(dsc.getPassword());
        //detector.setSchema(dsc.getDatabase());
        detector.setHeartbeatTimeout(heartbeat.getHeartbeatTimeout());
        detector.setHeartbeat(heartbeat);
        postConnect(detector, MycatServer.getInstance().getConnector());
        return detector;
    }

}