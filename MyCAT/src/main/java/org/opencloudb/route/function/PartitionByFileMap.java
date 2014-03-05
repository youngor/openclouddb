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
package org.opencloudb.route.function;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.opencloudb.config.model.rule.RuleAlgorithm;

/**
 * @author mycat
 */
public class PartitionByFileMap extends AbstractPartionAlgorithm implements RuleAlgorithm {

	private String mapFile;
	private Map<Integer, Integer> app2Partition;

	@Override
	public void init() {

		initialize();
	}

	public void setMapFile(String mapFile) {
		this.mapFile = mapFile;
	}

	@Override
	public Integer calculate(String columnValue) {
		Integer value = Integer.valueOf(columnValue);
		Integer rst = null;
		Integer pid = app2Partition.get(value);
		if (pid != null) {
			rst = pid;
		}
		return rst;
	}

	private void initialize() {
		BufferedReader in = null;
		try {
			// FileInputStream fin = new FileInputStream(new File(fileMapPath));
			InputStream fin = this.getClass().getClassLoader()
					.getResourceAsStream(mapFile);
			if (fin == null) {
				throw new RuntimeException("can't find class resource file "
						+ mapFile);
			}
			in = new BufferedReader(new InputStreamReader(fin));
			app2Partition = new HashMap<Integer, Integer>();
			for (String line = null; (line = in.readLine()) != null;) {
				line = line.trim();
				if (line.startsWith("#") || line.startsWith("//"))
					continue;
				int ind = line.indexOf('=');
				if (ind < 0)
					continue;
				try {
					String key = line.substring(0, ind).trim();
					int pid = Integer.parseInt(line.substring(ind + 1).trim());
					app2Partition.put(Integer.parseInt(key), pid);
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new RuntimeException(e);
			}

		} finally {
			try {
				in.close();
			} catch (Exception e2) {
			}
		}
	}
}