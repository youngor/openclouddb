package org.opencloudb.performance;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuzh
 * 
 */
public class TestInsertPerf extends AbstractMultiTreadBatchTester {

	public static void main(String[] args) throws Exception {
       new TestInsertPerf().run(args);
       

	}

	@Override
	public Runnable createJob(SimpleConPool conPool2, int myCount, int batch,
			int startId, AtomicInteger finshiedCount2,
			AtomicInteger failedCount2) {
		  return new TravelRecordInsertJob(conPool2,
					myCount, batch, startId, finshiedCount, failedCount);
	}

	

	



	
}
