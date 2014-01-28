package org.opencloudb.performance;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuzh
 * 
 */
public class TestUpdatePerf extends AbstractMultiTreadBatchTester {
	private int repeats = 1;

	public TestUpdatePerf(int repearts) {
		this.repeats = repearts;
		if (repeats > 1) {
			this.outputMiddleInf = false;
		}
	}

	public static void main(String[] args) throws Exception {
		int repeats = 1;
		if (args.length > 5) {
			repeats = Integer.parseInt(args[5]);
		}
		for (int i = 0; i < repeats; i++) {
			new TestUpdatePerf(repeats).run(args);
		}

	}

	@Override
	public Runnable createJob(SimpleConPool conPool2, int myCount, int batch,
			int startId, AtomicInteger finshiedCount2,
			AtomicInteger failedCount2) {
		return new TravelRecordUpdateJob(conPool2, myCount, batch, startId,
				finshiedCount, failedCount);
	}

}
