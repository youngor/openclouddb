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
import org.opencloudb.config.model.MycatNodeConfig;
import org.opencloudb.config.model.SystemConfig;
import org.opencloudb.net.factory.BackendConnectionFactory;

/**
 * @author mycat
 */
public class MyCATDetectorFactory extends BackendConnectionFactory {

    public MyCATDetectorFactory() {
        this.idleTimeout = 120 * 1000L;
    }

    public MyCATDetector make(MyCATHeartbeat heartbeat) throws IOException {
        SocketChannel channel = openSocketChannel();
        MycatNodeConfig cnc = heartbeat.getNode().getConfig();
        SystemConfig sys = MycatServer.getInstance().getConfig().getSystem();
        MyCATDetector detector = new MyCATDetector(channel);
        detector.setHost(cnc.getHost());
        detector.setPort(cnc.getPort());
        detector.setUser(sys.getClusterHeartbeatUser());
        detector.setPassword(sys.getClusterHeartbeatPass());
        detector.setHeartbeatTimeout(sys.getClusterHeartbeatTimeout());
        detector.setHeartbeat(heartbeat);
        postConnect(detector, MycatServer.getInstance().getConnector());
        return detector;
    }

}