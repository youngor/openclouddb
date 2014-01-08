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
package org.opencloudb.response;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.ConfigInitializer;
import org.opencloudb.MycatCluster;
import org.opencloudb.MycatConfig;
import org.opencloudb.MycatServer;
import org.opencloudb.backend.PhysicalDBNode;
import org.opencloudb.backend.PhysicalDBPool;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.config.model.QuarantineConfig;
import org.opencloudb.config.model.SchemaConfig;
import org.opencloudb.config.model.UserConfig;
import org.opencloudb.manager.ManagerConnection;
import org.opencloudb.net.mysql.OkPacket;

/**
 * @author mycat
 */
public final class ReloadConfig {
	private static final Logger LOGGER = Logger.getLogger(ReloadConfig.class);

	public static void execute(ManagerConnection c) {
		final ReentrantLock lock = MycatServer.getInstance().getConfig()
				.getLock();
		lock.lock();
		try {
			if (reload()) {
				StringBuilder s = new StringBuilder();
				s.append(c).append("Reload config success by manager");
				LOGGER.warn(s.toString());
				OkPacket ok = new OkPacket();
				ok.packetId = 1;
				ok.affectedRows = 1;
				ok.serverStatus = 2;
				ok.message = "Reload config success".getBytes();
				ok.write(c);
			} else {
				c.writeErrMessage(ErrorCode.ER_YES, "Reload config failure");
			}
		} finally {
			lock.unlock();
		}
	}

	private static boolean reload() {
		// 载入新的配置
		ConfigInitializer loader = new ConfigInitializer();
		Map<String, UserConfig> users = loader.getUsers();
		Map<String, SchemaConfig> schemas = loader.getSchemas();
		Map<String, PhysicalDBNode> dataNodes = loader.getDataNodes();
		Map<String, PhysicalDBPool> dataHosts = loader.getDataHosts();
		MycatCluster cluster = loader.getCluster();
		QuarantineConfig quarantine = loader.getQuarantine();

		// 应用新配置
		MycatConfig conf = MycatServer.getInstance().getConfig();
		Map<String, PhysicalDBPool> cNodes = conf.getDataHosts();
		boolean reloadStatus = true;
		for (PhysicalDBPool dn : dataHosts.values()) {
			dn.init(0);
			if (!dn.isInitSuccess()) {
				reloadStatus = false;
				break;
			}
		}
		// 如果重载不成功，则清理已初始化的资源。
		if (!reloadStatus) {
			LOGGER.warn("reload failed ,clear previously created datasources ");
			for (PhysicalDBPool dn : dataHosts.values()) {
				dn.clearDataSources("reload config");
				dn.stopHeartbeat();
			}
			return false;
		}

		// 应用重载
		conf.reload(users, schemas, dataNodes, dataHosts, cluster, quarantine);

		// 处理旧的资源
		for (PhysicalDBPool dn : cNodes.values()) {
			dn.clearDataSources("reload config clear old datasources");
			dn.stopHeartbeat();
		}

		//清理缓存
		 MycatServer.getInstance().getCacheService().clearCache();
		return true;
	}

}