
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
package org.opencloudb.route.function;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.opencloudb.config.model.rule.RuleAlgorithm;

/**
 * auto partition by Long
 * 
 * @author hexiaobin
 */
public class PartitionByPattern implements RuleAlgorithm {
	private static final int PARTITION_LENGTH = 1024;
	private int patternValue = PARTITION_LENGTH;// 分区长度，取模数值
	private String mapFile;
	private LongRange[] longRongs;
	private int defaultNode = 0;// 包含非数值字符，默认存储节点

	@Override
	public void init() {

		initialize();
	}

	public void setMapFile(String mapFile) {
		this.mapFile = mapFile;
	}

	public void setPatternValue(int patternValue) {
		this.patternValue = patternValue;
	}

	public void setDefaultNode(int defaultNode) {
		this.defaultNode = defaultNode;
	}

	@Override
	public Integer calculate(String columnValue) {
		if (!isNumeric(columnValue)) {
			return defaultNode;
		}
		long value = Long.valueOf(columnValue);
		Integer rst = null;
		for (LongRange longRang : this.longRongs) {
			long hash = value % patternValue;
			if (hash <= longRang.valueEnd && hash >= longRang.valueStart) {
				return longRang.nodeIndx;
			}
		}
		return rst;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
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
			LinkedList<LongRange> longRangeList = new LinkedList<LongRange>();

			for (String line = null; (line = in.readLine()) != null;) {
				line = line.trim();
				if (line.startsWith("#") || line.startsWith("//"))
					continue;
				int ind = line.indexOf('=');
				if (ind < 0) {
					System.out.println(" warn: bad line int " + mapFile + " :"
							+ line);
					continue;
				}
				try {
					String pairs[] = line.substring(0, ind).trim().split("-");
					long longStart = Long.parseLong(pairs[0].trim());
					long longEnd = Long.parseLong(pairs[1].trim());
					int nodeId = Integer.parseInt(line.substring(ind + 1)
							.trim());
					longRangeList
							.add(new LongRange(nodeId, longStart, longEnd));

				} catch (Exception e) {
				}
			}
			longRongs = longRangeList.toArray(new LongRange[longRangeList
					.size()]);
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

	static class LongRange {
		public final int nodeIndx;
		public final long valueStart;
		public final long valueEnd;

		public LongRange(int nodeIndx, long valueStart, long valueEnd) {
			super();
			this.nodeIndx = nodeIndx;
			this.valueStart = valueStart;
			this.valueEnd = valueEnd;
		}

	}
}
