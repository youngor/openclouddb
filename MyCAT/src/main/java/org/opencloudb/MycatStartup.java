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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.helpers.LogLog;
import org.opencloudb.config.model.SystemConfig;

/**
 * @author mycat
 */
public final class MycatStartup {
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";

	public static void main(String[] args) {
		try {
			String home = SystemConfig.getHomePath();
			if (home == null) {
				System.out.println(SystemConfig.SYS_HOME + "  is not set.");
				System.exit(-1);
			}
			// init
			MycatServer server = MycatServer.getInstance();
			server.beforeStart();

			// startup
			server.startup();
			System.out.println("MyCAT Server startup successfully. see logs in logs/mycat.log");
		} catch (Throwable e) {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			LogLog.error(sdf.format(new Date()) + " startup error", e);
			System.exit(-1);
		}
	}
}