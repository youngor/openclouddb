package org.opencloudb.route.function;

import org.junit.Assert;
import org.junit.Test;

public class PartitionByPatternTest {

	@Test
	public void test() {
		PartitionByPattern autoPartition = new PartitionByPattern();
		autoPartition.setPatternValue(256);
		autoPartition.setDefaultNode(2);
		autoPartition.setMapFile("partition-pattern.txt");
		autoPartition.init();
		String idVal = "0";
		Assert.assertEquals(true, 7 == autoPartition.calculate(idVal));
		idVal = "45a";
		Assert.assertEquals(true, 2 == autoPartition.calculate(idVal));

	}
}
