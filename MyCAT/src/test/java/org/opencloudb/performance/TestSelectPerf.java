package org.opencloudb.performance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author shenzhw
 * 
 */
public class TestSelectPerf {

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
					.println("input param,format: [jdbcurl] [user] [password]  [threadpoolsize]  [executetimes] [maxId] ");
			return;
		}
		int threadCount = 0;// 线程数
		String url = args[0];
		String user = args[1];
		String password = args[2];
		threadCount = Integer.parseInt(args[3]);
		int executetimes = Integer.parseInt(args[4]);
		long maxId = Integer.parseInt(args[5]);
		System.out.println("concerent threads:" + threadCount);
		System.out.println("execute sql times:" + executetimes);
		System.out.println("maxId:" + maxId);
		ArrayList<Thread> threads = new ArrayList<Thread>(threadCount);
		for (int i = 0; i < threadCount; i++) {
			try {

				Connection con = getCon(url, user, password);
				System.out.println("create thread " + i);
				TravelRecordSelectJob job = new TravelRecordSelectJob(con,
						maxId, executetimes,finshiedCount,failedCount);
				Thread thread = new Thread(job);
				threads.add(thread);
			} catch (Exception e) {
				System.out.println("failed create thread " + i + " err "
						+ e.toString());
			}
		}

		System.out.println("success create thread count: " + threads.size());
		for (Thread thread : threads) {
			thread.start();
		}
		long start = System.currentTimeMillis();
		System.out.println("all thread started,waiting finsh...");
		boolean notFinished = true;
		while (notFinished) {
			notFinished = false;
			for (Thread thread : threads) {
				if (thread.isAlive()) {
					notFinished = true;
					break;
				}
			}
			long sucess = finshiedCount.get() - failedCount.get();
			System.out.println("finished records :" + finshiedCount.get()
					+ " failed:" + failedCount.get() + " speed:" + sucess
					* 1000.0 / (System.currentTimeMillis() - start));
			Thread.sleep(1000);
		}
		long usedTime = (System.currentTimeMillis() - start)/1000;
		System.out.println("finishend:" + finshiedCount.get() + " failed:"
				+ failedCount.get());
		long sucess = finshiedCount.get() - failedCount.get();
		System.out.println("used time total:" + usedTime  + "seconds");
		System.out.println("tps:" + sucess * 1.0 / usedTime);
	}
}

