package org.opencloudb.performance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuzh
 * 
 */
public class TestInsertPerf {

	private static AtomicInteger finshiedCount = new AtomicInteger();
	private static AtomicInteger failedCount = new AtomicInteger();

	public static void addFinshed(int count) {
		finshiedCount.addAndGet(count);
	}

	public static void addFailed(int count) {
		failedCount.addAndGet(count);
	}

	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		if (args.length < 5) {
			System.out
					.println("input param,format: [jdbcurl] [user] [password]  [threadpoolsize]  [recordrange]  [startId]");
			return;
		}
		int threadCount = 0;// 线程数
		String url = args[0];
		String user = args[1];
		String password = args[2];
		threadCount = Integer.parseInt(args[3]);
		System.out.println("concerent threads:" + threadCount);
		String[] rangeItems = args[4].split(",");
		SimpleConPool conPool = new SimpleConPool(url, user, password,
				threadCount);
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		ArrayList<TravelRecordInsertJob>[] allJobs = new ArrayList[rangeItems.length];
		for (int i = 0; i < rangeItems.length; i++) {
			String[] items = rangeItems[i].split("-");
			int min = Integer.parseInt(items[0]);
			int max = Integer.parseInt(items[1]);
			allJobs[i] = createJobs(conPool, min, max);

		}
		Iterator<TravelRecordInsertJob>[] itors = new Iterator[allJobs.length];
		for (int i = 0; i < allJobs.length; i++) {
			itors[i] = allJobs[i].iterator();
		}
		int total = 0;
		long start = System.currentTimeMillis();
		boolean finished = false;
		while (!finished) {

			finished = true;
			for (int i = 0; i < itors.length; i++) {
				if (itors[i].hasNext()) {
					total++;
					executor.execute(itors[i].next());
					if (finished) {
						finished = !itors[i].hasNext();
					}

				}
			}
		}

		// executor.execute(job);
		System.out.println("success create job count: " + total);
		executor.shutdown();
		while (!executor.isTerminated()) {
			long sucess = finshiedCount.get() - failedCount.get();
			System.out.println("finished records :" + finshiedCount.get()
					+ " failed:" + failedCount.get() + " speed:" + sucess
					* 1000.0 / (System.currentTimeMillis() - start));
			Thread.sleep(1000);
		}

		long usedTime = (System.currentTimeMillis() - start) / 1000;
		System.out.println("finishend:" + finshiedCount.get() + " failed:"
				+ failedCount.get());
		long sucess = finshiedCount.get() - failedCount.get();
		System.out.println("used time total:" + usedTime + "seconds");
		System.out.println("tps:" + sucess / usedTime);
	}

	private static ArrayList<TravelRecordInsertJob> createJobs(
			SimpleConPool conPool, int minId, int maxId) {
		int recordCount = maxId - minId + 1;
		int batchSize = 10000;
		int totalBatch = recordCount / batchSize;
		ArrayList<TravelRecordInsertJob> jobs = new ArrayList<TravelRecordInsertJob>(
				totalBatch);
		for (int i = 0; i < totalBatch; i++) {
			int startId = minId + i * batchSize;
			int endId = (startId + batchSize);
			if (endId >= maxId) {
				endId=maxId;
			} 
           int myCount=endId-startId+1;
			TravelRecordInsertJob job = new TravelRecordInsertJob(conPool,
					myCount, 100, startId, finshiedCount,
					failedCount);
			 System.out.println("job insert record id is "+startId+"-"+endId);
			jobs.add(job);

		}
		return jobs;
	}

}
