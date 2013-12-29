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
package org.opencloudb.config.model;

/**
 * 用于描述一个数据节点的配置
 * 
 * @author mycat
 */
public final class DataNodeConfig {

	private final String name;
	private final String database;
	private final String dataHost;

	public DataNodeConfig(String name, String database, String dataHost) {
		super();
		this.name = name;
		this.database = database;
		this.dataHost = dataHost;
	}

	public String getName() {
		return name;
	}

	public String getDatabase() {
		return database;
	}

	public String getDataHost() {
		return dataHost;
	}

}