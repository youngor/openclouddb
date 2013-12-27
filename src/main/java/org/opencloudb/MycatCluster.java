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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencloudb.config.model.ClusterConfig;
import org.opencloudb.config.model.MycatNodeConfig;

/**
 * @author mycat
 */
public final class MycatCluster {

    private final Map<String, MycatNode> nodes;
    private final Map<String, List<String>> groups;

    public MycatCluster(ClusterConfig clusterConf) {
        this.nodes = new HashMap<String, MycatNode>(clusterConf.getNodes().size());
        this.groups = clusterConf.getGroups();
        for (MycatNodeConfig conf : clusterConf.getNodes().values()) {
            String name = conf.getName();
            MycatNode node = new MycatNode(conf);
            this.nodes.put(name, node);
        }
    }

    public Map<String, MycatNode> getNodes() {
        return nodes;
    }

    public Map<String, List<String>> getGroups() {
        return groups;
    }

}