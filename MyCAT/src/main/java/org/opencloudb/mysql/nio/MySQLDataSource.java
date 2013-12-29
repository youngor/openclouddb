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
 * (created at 2012-4-17)
 */
package org.opencloudb.mysql.nio;

import java.io.IOException;

import org.opencloudb.backend.PhysicalConnection;
import org.opencloudb.backend.PhysicalDatasource;
import org.opencloudb.config.model.DBHostConfig;
import org.opencloudb.config.model.DataHostConfig;
import org.opencloudb.heartbeat.DBHeartbeat;
import org.opencloudb.heartbeat.MySQLHeartbeat;
import org.opencloudb.mysql.nio.handler.ResponseHandler;

/**
 * @author mycat
 */
public class MySQLDataSource extends PhysicalDatasource {

	private final MySQLConnectionFactory factory;
	public MySQLDataSource(DBHostConfig config,DataHostConfig hostConfig,boolean isReadNode) {
		super(config,hostConfig,isReadNode);
		this.factory = new MySQLConnectionFactory();
	
	}


	@Override
	public PhysicalConnection createNewConnection(ResponseHandler handler) throws IOException {
		return factory.make(this, handler);	}


	@Override
	public DBHeartbeat createHeartBeat() {
		return new MySQLHeartbeat(this);
	}


}