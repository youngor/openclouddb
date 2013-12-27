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
 * (created at 2011-7-19)
 */
package org.opencloudb.route.function;

import org.opencloudb.config.model.rule.RuleAlgorithm;
import org.opencloudb.parser.util.Pair;
import org.opencloudb.route.util.PartitionUtil;
import org.opencloudb.util.StringUtil;

/**
 * @author <a href="mailto:daasadmin@hp.com">yangwenx</a>
 */
public final class PartitionByString implements RuleAlgorithm  {
  
    private int hashSliceStart = 0;
    /** 0 means str.length(), -1 means str.length()-1 */
    private int hashSliceEnd = 8;
    protected int[] count;
    protected int[] length;
    protected PartitionUtil partitionUtil;

    public void setPartitionCount(String partitionCount) {
        this.count = toIntArray(partitionCount);
    }

    public void setPartitionLength(String partitionLength) {
        this.length = toIntArray(partitionLength);
    }


	public void setHashLength(int hashLength) {
        setHashSlice(String.valueOf(hashLength));
    }

    public void setHashSlice(String hashSlice) {
        Pair<Integer, Integer> p = sequenceSlicing(hashSlice);
        hashSliceStart = p.getKey();
        hashSliceEnd = p.getValue();
    }


    /**
     * "2" -&gt; (0,2)<br/>
     * "1:2" -&gt; (1,2)<br/>
     * "1:" -&gt; (1,0)<br/>
     * "-1:" -&gt; (-1,0)<br/>
     * ":-1" -&gt; (0,-1)<br/>
     * ":" -&gt; (0,0)<br/>
     */
    public static Pair<Integer, Integer> sequenceSlicing(String slice) {
        int ind = slice.indexOf(':');
        if (ind < 0) {
            int i = Integer.parseInt(slice.trim());
            if (i >= 0) {
                return new Pair<Integer, Integer>(0, i);
            } else {
                return new Pair<Integer, Integer>(i, 0);
            }
        }
        String left = slice.substring(0, ind).trim();
        String right = slice.substring(1 + ind).trim();
        int start, end;
        if (left.length() <= 0) {
            start = 0;
        } else {
            start = Integer.parseInt(left);
        }
        if (right.length() <= 0) {
            end = 0;
        } else {
            end = Integer.parseInt(right);
        }
        return new Pair<Integer, Integer>(start, end);
    }

	@Override
	public void init() {
		partitionUtil = new PartitionUtil(count,length);
		
	}
	private static int[] toIntArray(String string) {
		String[] strs = org.opencloudb.util.SplitUtil.split(string, ',', true);
		int[] ints = new int[strs.length];
		for (int i = 0; i < strs.length; ++i) {
			ints[i] = Integer.parseInt(strs[i]);
		}
		return ints;
	}
	@Override
	public Integer calculate(String key) {
        int start = hashSliceStart >= 0 ? hashSliceStart : key.length() + hashSliceStart;
        int end = hashSliceEnd > 0 ? hashSliceEnd : key.length() + hashSliceEnd;
        long hash = StringUtil.hash(key, start, end);
        return partitionUtil.partition(hash);
	}

}
