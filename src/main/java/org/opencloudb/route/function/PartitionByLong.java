/*
 * Copyright 1999-2012 HP.
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
 * (created at 2011-7-20)
 */
package org.opencloudb.route.function;

import org.opencloudb.config.model.rule.RuleAlgorithm;
import org.opencloudb.route.util.PartitionUtil;

public final class PartitionByLong implements RuleAlgorithm {
	protected int[] count;
	protected int[] length;
	protected PartitionUtil partitionUtil;

	private static int[] toIntArray(String string) {
		String[] strs = org.opencloudb.util.SplitUtil.split(string, ',', true);
		int[] ints = new int[strs.length];
		for (int i = 0; i < strs.length; ++i) {
			ints[i] = Integer.parseInt(strs[i]);
		}
		return ints;
	}

	public void setPartitionCount(String partitionCount) {
		this.count = toIntArray(partitionCount);
	}

	public void setPartitionLength(String partitionLength) {
		this.length = toIntArray(partitionLength);
	}

	@Override
	public void init() {
		partitionUtil = new PartitionUtil(count, length);

	}

	@Override
	public Integer calculate(String columnValue) {
		long key = Long.parseLong(columnValue);
		return partitionUtil.partition(key);
	}

}
