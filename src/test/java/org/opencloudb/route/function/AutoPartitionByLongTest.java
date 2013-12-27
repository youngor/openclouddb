package org.opencloudb.route.function;

import junit.framework.Assert;

import org.junit.Test;

public class AutoPartitionByLongTest {

	@Test
	public void test()
	{
		AutoPartitionByLong autoPartition=new AutoPartitionByLong();
		autoPartition.setMapFile("autopartition-long.txt");
		autoPartition.init();
		String idVal="0";
		Assert.assertEquals(true, 0==autoPartition.calculate(idVal)); 
		
		idVal="2000000";
		Assert.assertEquals(true, 0==autoPartition.calculate(idVal)); 
		
		idVal="2000001";
		Assert.assertEquals(true, 1==autoPartition.calculate(idVal)); 
		
		idVal="4000000";
		Assert.assertEquals(true, 1==autoPartition.calculate(idVal)); 
		
		idVal="4000001";
		Assert.assertEquals(true, 2==autoPartition.calculate(idVal)); 
	}
}
