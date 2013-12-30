package org.opencloudb.performance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shenzhw
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

	private static Connection getCon(String url, String user, String passwd)
			throws SQLException {
		Connection theCon = DriverManager.getConnection(url, user, passwd);
		return theCon;
	}

	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		if (args.length < 5) {
			System.out
					.println("input param,format: [jdbcurl] [user] [password]  [threadpoolsize]  [record] ");
			return;
		}
		int threadCount = 0;// 线程数
		int recordCount = 0;// 要插入的记录数
		String url = args[0];
		String user = args[1];
		String password = args[2];
		threadCount = Integer.parseInt(args[3]);
		recordCount = Integer.parseInt(args[4]);

		System.out.println("concerent threads:" + threadCount);
		System.out.println("total insert records:" + recordCount);
		if (recordCount < 5000) {
			System.out.println("recoud count must > 5000");
		}
		int threadTotalRecord = recordCount / threadCount;
		int batchSize = 100;
		ArrayList<Thread> threads = new ArrayList<Thread>(threadCount);
		for (int i = 0; i < threadCount; i++) {
			try {

				int startId = i * threadTotalRecord;
				Connection con = getCon(url, user, password);
				System.out.println("create thread " + i
						+ " insert record start at " + startId);
				TravelRecordInsertJob job = new TravelRecordInsertJob(con,
						threadTotalRecord, batchSize, startId,finshiedCount,failedCount);
				Thread thread = new Thread(job);
				threads.add(thread);
			} catch (Exception e) {
				System.out.println("failed create thread " + i + " err "
						+ e.toString());
			}
		}

		recordCount = threads.size() * threadTotalRecord;
		System.out.println("success create thread count: " + threads.size()
				+ " insert total records: " + recordCount);
		for (Thread thread : threads) {
			thread.start();
		}
		long start = System.currentTimeMillis();
		System.out.println("all thread started,waiting finsh...");
		while (finshiedCount.get() < recordCount) {
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

}

