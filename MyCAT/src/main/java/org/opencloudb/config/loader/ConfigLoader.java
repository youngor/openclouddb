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
/**
 * (created at 2012-6-13)
 */
package org.opencloudb.config.loader;

import java.util.Map;

import org.opencloudb.config.model.ClusterConfig;
import org.opencloudb.config.model.DataHostConfig;
import org.opencloudb.config.model.DataNodeConfig;
import org.opencloudb.config.model.QuarantineConfig;
import org.opencloudb.config.model.SchemaConfig;
import org.opencloudb.config.model.SystemConfig;
import org.opencloudb.config.model.UserConfig;

/**
 * @author mycat
 */
public interface ConfigLoader {
	SchemaConfig getSchemaConfig(String schema);

	Map<String, SchemaConfig> getSchemaConfigs();

	Map<String, DataNodeConfig> getDataNodes();

	Map<String, DataHostConfig> getDataHosts();

	SystemConfig getSystemConfig();

	UserConfig getUserConfig(String user);

	Map<String, UserConfig> getUserConfigs();

	QuarantineConfig getQuarantineConfig();

	ClusterConfig getClusterConfig();
}