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
package org.opencloudb.mysql.nio;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.opencloudb.MycatServer;
import org.opencloudb.config.model.DBHostConfig;
import org.opencloudb.mysql.nio.handler.ResponseHandler;
import org.opencloudb.net.factory.BackendConnectionFactory;

/**
 * @author mycat
 */
public class MySQLConnectionFactory extends BackendConnectionFactory {
	public MySQLConnection make(MySQLDataSource pool, ResponseHandler handler)
			throws IOException {
		SocketChannel channel = openSocketChannel();
		DBHostConfig dsc = pool.getConfig();

		MySQLConnection c = new MySQLConnection(channel, pool.isReadNode());
		c.setHost(dsc.getIp());
		c.setPort(dsc.getPort());
		c.setUser(dsc.getUser());
		c.setPassword(dsc.getPassword());
		// c.setSchema(dsc.getDatabase());
		c.setHandler(new MySQLConnectionAuthenticator(c, handler));
		c.setPool(pool);
		postConnect(c, MycatServer.getInstance().getConnector());
		return c;
	}

}