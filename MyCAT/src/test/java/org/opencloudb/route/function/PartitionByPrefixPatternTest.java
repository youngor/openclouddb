package org.opencloudb.route.function;

import junit.framework.Assert;

import org.junit.Test;

public class PartitionByPrefixPatternTest {

	@Test
	public void test()
	{
		/**
		 * ASCII编码：
		 * 48-57=0-9阿拉伯数字
		 * 64、65-90=@、A-Z 
		 * 97-122=a-z
		 * 
		 */
		PartitionByPrefixPattern autoPartition=new PartitionByPrefixPattern();
		autoPartition.setPatternValue(32);
		autoPartition.setPrefixLength(5);
		autoPartition.setMapFile("partition_prefix_pattern.txt");
		autoPartition.init();
		
		String idVal="gf89f9a";
		Assert.assertEquals(true, 0==autoPartition.calculate(idVal)); 
		
		idVal="8df99a";
		Assert.assertEquals(true, 4==autoPartition.calculate(idVal)); 
		
		idVal="8dhdf99a";
		Assert.assertEquals(true, 3==autoPartition.calculate(idVal)); 
	}
}
