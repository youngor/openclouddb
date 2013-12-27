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
package org.opencloudb.server;

import org.apache.log4j.Logger;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.net.handler.FrontendQueryHandler;
import org.opencloudb.net.mysql.OkPacket;
import org.opencloudb.server.handler.BeginHandler;
import org.opencloudb.server.handler.ExplainHandler;
import org.opencloudb.server.handler.KillHandler;
import org.opencloudb.server.handler.SavepointHandler;
import org.opencloudb.server.handler.SelectHandler;
import org.opencloudb.server.handler.SetHandler;
import org.opencloudb.server.handler.ShowHandler;
import org.opencloudb.server.handler.StartHandler;
import org.opencloudb.server.handler.UseHandler;
import org.opencloudb.server.parser.ServerParse;

/**
 * @author mycat
 */
public class ServerQueryHandler implements FrontendQueryHandler {
	private static final Logger LOGGER = Logger
			.getLogger(ServerQueryHandler.class);

	private final ServerConnection source;

	public ServerQueryHandler(ServerConnection source) {
		this.source = source;
	}

	@Override
	public void query(String sql) {
		ServerConnection c = this.source;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(new StringBuilder().append(c).append(sql).toString());
		}
		int rs = ServerParse.parse(sql);
		switch (rs & 0xff) {
		case ServerParse.EXPLAIN:
			ExplainHandler.handle(sql, c, rs >>> 8);
			break;
		case ServerParse.SET:
			SetHandler.handle(sql, c, rs >>> 8);
			break;
		case ServerParse.SHOW:
			ShowHandler.handle(sql, c, rs >>> 8);
			break;
		case ServerParse.SELECT:
			SelectHandler.handle(sql, c, rs >>> 8);
			break;
		case ServerParse.START:
			StartHandler.handle(sql, c, rs >>> 8);
			break;
		case ServerParse.BEGIN:
			BeginHandler.handle(sql, c);
			break;
		case ServerParse.SAVEPOINT:
			SavepointHandler.handle(sql, c);
			break;
		case ServerParse.KILL:
			KillHandler.handle(sql, rs >>> 8, c);
			break;
		case ServerParse.KILL_QUERY:
			c.writeErrMessage(ErrorCode.ER_UNKNOWN_COM_ERROR,
					"Unsupported command");
			break;
		case ServerParse.USE:
			UseHandler.handle(sql, c, rs >>> 8);
			break;
		case ServerParse.COMMIT:
			c.commit();
			break;
		case ServerParse.ROLLBACK:
			c.rollback();
			break;
		case ServerParse.HELP:
			c.writeErrMessage(ErrorCode.ER_SYNTAX_ERROR, "Unsupported command");
			break;
		case ServerParse.MYSQL_CMD_COMMENT:
			c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
			break;
		case ServerParse.MYSQL_COMMENT:
			c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
			break;
		default:
			c.execute(sql, rs & 0xff);
		}
	}

}