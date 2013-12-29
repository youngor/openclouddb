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
package org.opencloudb;

import org.apache.log4j.Logger;
import org.opencloudb.config.model.MycatNodeConfig;
import org.opencloudb.heartbeat.MyCATHeartbeat;

/**
 * @author mycat
 */
public class MycatNode {
    private static final Logger LOGGER = Logger.getLogger(MycatNode.class);

    private final String name;
    private final MycatNodeConfig config;
    private final MyCATHeartbeat heartbeat;

    public MycatNode(MycatNodeConfig config) {
        this.name = config.getName();
        this.config = config;
        this.heartbeat = new MyCATHeartbeat(this);
    }

    public String getName() {
        return name;
    }

    public MycatNodeConfig getConfig() {
        return config;
    }

    public MyCATHeartbeat getHeartbeat() {
        return heartbeat;
    }

    public void stopHeartbeat() {
        heartbeat.stop();
    }

    public void startHeartbeat() {
        heartbeat.start();
    }

    public void doHeartbeat() {
        if (!heartbeat.isStop()) {
            try {
                heartbeat.heartbeat();
            } catch (Throwable e) {
                LOGGER.error(name + " heartbeat error.", e);
            }
        }
    }

    public boolean isOnline() {
        return (heartbeat.getStatus() == MyCATHeartbeat.OK_STATUS);
    }

}