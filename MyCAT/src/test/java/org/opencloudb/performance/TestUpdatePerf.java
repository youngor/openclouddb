package org.opencloudb.performance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuzh
 * 
 */
public class TestUpdatePerf extends AbstractMultiTreadBatchTester {

	public static void main(String[] args) throws Exception {
		new TestUpdatePerf().run(args);

	}

	@Override
	public Runnable createJob(SimpleConPool conPool2, int myCount, int batch,
			int startId, AtomicInteger finshiedCount2,
			AtomicInteger failedCount2) {
		  return new TravelRecordUpdateJob(conPool2,
					myCount, batch, startId, finshiedCount, failedCount);
	}


}
