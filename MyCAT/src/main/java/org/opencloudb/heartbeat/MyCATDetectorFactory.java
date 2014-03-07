/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package org.opencloudb.heartbeat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import org.opencloudb.MycatServer;
import org.opencloudb.config.model.MycatNodeConfig;
import org.opencloudb.config.model.SystemConfig;
import org.opencloudb.net.factory.BackendConnectionFactory;

/**
 * @author mycat
 */
public class MyCATDetectorFactory extends BackendConnectionFactory {

    public MyCATDetectorFactory() {
      
    }

    public MyCATDetector make(MyCATHeartbeat heartbeat) throws IOException {
    	
        MycatNodeConfig cnc = heartbeat.getNode().getConfig();
        SystemConfig sys = MycatServer.getInstance().getConfig().getSystem();
        AsynchronousSocketChannel channel = openSocketChannel();
        MyCATDetector detector = new MyCATDetector(channel);
        detector.setHost(cnc.getHost());
        detector.setPort(cnc.getPort());
        detector.setUser(sys.getClusterHeartbeatUser());
        detector.setPassword(sys.getClusterHeartbeatPass());
        detector.setHeartbeatTimeout(sys.getClusterHeartbeatTimeout());
        detector.setHeartbeat(heartbeat);
        channel.connect(new InetSocketAddress(cnc.getHost(), cnc.getPort()),detector,MycatServer.getInstance().getConnector());
        return detector;
    }

}