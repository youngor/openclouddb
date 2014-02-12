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
package org.opencloudb.backend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.opencloudb.mysql.nio.handler.ResponseHandler;
import org.opencloudb.net.ClosableConnection;
import org.opencloudb.route.RouteResultsetNode;
import org.opencloudb.server.ServerConnection;

public interface PhysicalConnection extends ClosableConnection {

	public boolean isModifiedSQLExecuted();

	public boolean isFromSlaveDB();

	public String getSchema();

	public void setSchema(String newSchema);

	public long getLastTime();

	public boolean isClosedOrQuit();

	public void setAttachment(Object attachment);

	public void quit();

	public void setLastTime(long currentTimeMillis);

	public void release();

	public void setRunning(boolean running);

	public boolean setResponseHandler(ResponseHandler commandHandler);

	public void commit();

	public void query(String sql) throws UnsupportedEncodingException;

	public Object getAttachment();

	public long getThreadId();

	public String getCharset();

	public void execute(RouteResultsetNode node, ServerConnection source,
			boolean autocommit) throws IOException;

	public void recordSql(String host, String schema, String statement);

	public boolean syncAndExcute();

	public void rollback();

	public boolean isSuppressReadTemporay();

	public void setSuppressReadTemporay(boolean b);

	public boolean isRunning();

	public boolean isBorrowed();

	public void setBorrowed(boolean borrowed);

	public int getTxIsolation();

	public boolean isAutocommit();
}